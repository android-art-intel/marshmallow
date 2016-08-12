/*
 * Copyright (C) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


#include "ext_utility.h"
#include "nodes.h"
#include "optimization.h"
#include <sstream>

namespace art {

  std::ostream& operator<<(std::ostream& os, const ConstantValue& cv) {
    switch (cv.type) {
      case Primitive::kPrimInt:
        os << cv.value.i;
        break;
      case Primitive::kPrimLong:
        os << cv.value.l;
        break;
      case Primitive::kPrimFloat:
        os << cv.value.f;
        break;
      case Primitive::kPrimDouble:
        os << cv.value.d;
        break;
      default:
        os << "Unknown constant type " << cv.type;
    }
    return os;
  }

  std::ostream& operator<<(std::ostream& os, HInstruction* instruction) {
    os << GetTypeId(instruction->GetType()) << instruction->GetId() << " ";
    os << instruction->DebugName();
    switch (instruction->GetKind()) {
      case HInstruction::kIntConstant:
        os << ' ' << instruction->AsIntConstant()->GetValue();
        break;
      case HInstruction::kLongConstant:
        os << ' ' << instruction->AsLongConstant()->GetValue();
        break;
      case HInstruction::kFloatConstant:
        os << ' ' << instruction->AsFloatConstant()->GetValue();
        break;
      case HInstruction::kDoubleConstant:
        os << ' ' << instruction->AsDoubleConstant()->GetValue();
        break;
      default:
        break;
    }
    if (instruction->InputCount() > 0) {
      os << " [ ";
      for (HInputIterator inputs(instruction); !inputs.Done(); inputs.Advance()) {
        os << GetTypeId(inputs.Current()->GetType()) << inputs.Current()->GetId() << ' ';
      }
      os << ']';
    }
    return os;
  }

  IfCondition NegateCondition(IfCondition cond) {
    switch (cond) {
      case kCondNE:
        return kCondEQ;
      case kCondEQ:
        return kCondNE;
      case kCondLT:
        return kCondGE;
      case kCondGE:
        return kCondLT;
      case kCondGT:
        return kCondLE;
      case kCondLE:
        return kCondGT;
      default:
        LOG(FATAL) << "Unknown if condition";
    }

    // Unreachable code.
    return kCondEQ;
  }

  IfCondition FlipConditionForOperandSwap(IfCondition cond) {
    switch (cond) {
      case kCondEQ:
      case kCondNE:
        // Do nothing, a == b <==> b == a.
        // Same for a != b.
        return cond;
      case kCondLT:
        return kCondGT;
      case kCondLE:
        return kCondGE;
      case kCondGT:
        return kCondLT;
      case kCondGE:
        return kCondLE;
      default:
        LOG(FATAL) << "Unknown if condition";
    }

    // Unreachable code.
    return kCondEQ;
  }

  bool GetFPConstantValue(HConstant* constant, double& value) {
    if (constant->IsFloatConstant()) {
      HFloatConstant* fp_cst = constant->AsFloatConstant();
      value = fp_cst->GetValue();
      return true;
    } else if (constant->IsDoubleConstant()) {
      HDoubleConstant* double_cst = constant->AsDoubleConstant();
      value = double_cst->GetValue();
      return true;
    }

    // If not int or long, bail.
    return false;
  }

  bool GetIntConstantValue(HConstant* constant, int64_t& value) {
    if (constant->IsIntConstant()) {
      HIntConstant* int_cst = constant->AsIntConstant();
      value = int_cst->GetValue();
      return true;
    } else if (constant->IsLongConstant()) {
      HLongConstant* long_cst = constant->AsLongConstant();
      value = long_cst->GetValue();
      return true;
    }

    // If not int or long, bail.
    return false;
  }

  HInstruction* GetCompareInstruction(HInstruction* instruction) {
    // Look at the first instruction, is it a compare?
    HInstruction* first = instruction->InputAt(0);

    if (first->IsCompare()) {
      return first;
    }

    // Not the first one, what about the second one?
    HInstruction* second = instruction->InputAt(1);

    if (second->IsCompare()) {
      return second;
    }

    // None of the two, so return the original one.
    return instruction;
  }

  static Primitive::Type GetResultType(HUnaryOperation* insn) {
    Primitive::Type type = insn->GetResultType();
    return type == Primitive::kPrimBoolean ? Primitive::kPrimInt : type;
  }

  static Primitive::Type GetResultType(HBinaryOperation* insn) {
    Primitive::Type type = insn->GetResultType();
    return type == Primitive::kPrimBoolean ? Primitive::kPrimInt : type;
  }

  static Primitive::Type GetResultType(HTypeConversion* insn) {
    Primitive::Type type = insn->GetResultType();
    return type == Primitive::kPrimBoolean ? Primitive::kPrimInt : type;
  }

  bool EvaluateCast(HTypeConversion* conv, const ConstantValue& x, ConstantValue& res) {
    constexpr int32_t int32_min_value = std::numeric_limits<int32_t>::min();
    constexpr int32_t int32_max_value = std::numeric_limits<int32_t>::max();
    constexpr int64_t int64_min_value = std::numeric_limits<int64_t>::min();
    constexpr int64_t int64_max_value = std::numeric_limits<int64_t>::max();
    Primitive::Type cast_to = GetResultType(conv);
    bool success = true;

    DCHECK(cast_to != Primitive::kPrimBoolean);
    DCHECK(x.type != Primitive::kPrimBoolean);

    if (x.type == Primitive::kPrimInt) {
      int32_t value = x.value.i;
      switch (cast_to) {
        case Primitive::kPrimLong:
          res.value.l = static_cast<int64_t>(value);
          break;
        case Primitive::kPrimFloat:
          res.value.f = static_cast<float>(value);
          break;
        case Primitive::kPrimDouble:
          res.value.d = static_cast<double>(value);
          break;
        case Primitive::kPrimByte:
          res.value.i = static_cast<int32_t>(static_cast<int8_t>(value));
          cast_to = Primitive::kPrimInt;
          break;
        case Primitive::kPrimShort:
          res.value.i = static_cast<int32_t>(static_cast<int16_t>(value));
          cast_to = Primitive::kPrimInt;
          break;
        default:
          success = false;
      }
    } else if (x.type == Primitive::kPrimLong) {
      int64_t value = x.value.l;
      switch (cast_to) {
        case Primitive::kPrimInt:
          res.value.i = static_cast<int32_t>(value);
          break;
        case Primitive::kPrimFloat:
          res.value.f = static_cast<float>(value);
          break;
        case Primitive::kPrimDouble:
          res.value.d = static_cast<double>(value);
          break;
        default:
          success = false;
      }
    } else if (x.type == Primitive::kPrimFloat) {
      float value = x.value.f;
      switch (cast_to) {
        case Primitive::kPrimInt:
          if (std::isnan(value)) {
            res.value.i = 0;
          } else if (value >= int32_max_value) {
            res.value.i = int32_max_value;
          } else if (value <= int32_min_value) {
            res.value.i = int32_min_value;
          } else {
            res.value.i = static_cast<int32_t>(value);
          }
          break;
        case Primitive::kPrimLong:
          if (std::isnan(value)) {
            res.value.l = 0L;
          } else if (value >= int64_max_value) {
            res.value.l = int64_max_value;
          } else if (value <= int64_min_value) {
            res.value.l = int64_min_value;
          } else {
            res.value.l = static_cast<int64_t>(value);
          }
          break;
        case Primitive::kPrimDouble:
          res.value.d = static_cast<double>(value);
          break;
        default:
          success = false;
      };
    } else if (x.type == Primitive::kPrimDouble) {
      double value = x.value.d;
      switch (cast_to) {
        case Primitive::kPrimInt:
          if (std::isnan(value)) {
            res.value.i = 0;
          } else if (value >= int32_max_value) {
            res.value.i = int32_max_value;
          } else if (value <= int32_min_value) {
            res.value.i = int32_min_value;
          } else {
            res.value.i = static_cast<int32_t>(value);
          }
          break;
        case Primitive::kPrimLong:
          if (std::isnan(value)) {
            res.value.l = 0;
          } else if (value >= int64_max_value) {
            res.value.l = int64_max_value;
          } else if (value <= int64_min_value) {
            res.value.l = int64_min_value;
          } else {
            res.value.l = static_cast<int64_t>(value);
          }
          break;
        case Primitive::kPrimFloat:
          res.value.f = static_cast<float>(value);
          break;
        default:
          success = false;
      }
    } else {
      success = false;
    }

    // Finally, update the type of the instruction.
    res.type = cast_to;

    return success;
  }

  bool Evaluate(HInstruction* insn, const ConstantValue& x, ConstantValue& res) {
    DCHECK(insn != nullptr);
    bool success = true;

    DCHECK(x.type != Primitive::kPrimBoolean);

    if (insn->IsUnaryOperation()) {
      HUnaryOperation* uop = insn->AsUnaryOperation();
      res.type = GetResultType(uop);
      DCHECK(res.type != Primitive::kPrimBoolean);
      switch (res.type) {
        case Primitive::kPrimBoolean:
          FALLTHROUGH_INTENDED;
        case Primitive::kPrimInt: {
          int32_t int_res = 0;
          switch (x.type) {
            case Primitive::kPrimInt:
              int_res = uop->Evaluate(x.value.i);
              break;
            case Primitive::kPrimFloat:
              int_res = static_cast<int32_t>(uop->Evaluate(x.value.f));
              break;
            case Primitive::kPrimLong:
              int_res = static_cast<int32_t>(uop->Evaluate(x.value.l));
              break;
            case Primitive::kPrimDouble:
              int_res = static_cast<int32_t>(uop->Evaluate(x.value.d));
              break;
            default:
              success = false;
          };
          res.value.i = int_res;
          break;
        }
        case Primitive::kPrimFloat: {
          float float_res = 0;
          switch (x.type) {
            case Primitive::kPrimInt:
              float_res = static_cast<float>(uop->Evaluate(x.value.i));
              break;
            case Primitive::kPrimFloat:
              float_res = uop->Evaluate(x.value.f);
              break;
            case Primitive::kPrimLong:
              float_res = static_cast<float>(uop->Evaluate(x.value.l));
              break;
            case Primitive::kPrimDouble:
              float_res = static_cast<float>(uop->Evaluate(x.value.d));
              break;
            default:
              success = false;
          };
          res.value.f = float_res;
          break;
        }
        case Primitive::kPrimLong: {
          int64_t long_res = 0;
          switch (x.type) {
            case Primitive::kPrimInt:
              long_res = static_cast<int64_t>(uop->Evaluate(x.value.i));
              break;
            case Primitive::kPrimFloat:
              long_res = static_cast<int64_t>(uop->Evaluate(x.value.f));
              break;
            case Primitive::kPrimLong:
              long_res = uop->Evaluate(x.value.l);
              break;
            case Primitive::kPrimDouble:
              long_res = static_cast<int64_t>(uop->Evaluate(x.value.d));
              break;
            default:
              success = false;
          };
          res.value.l = long_res;
          break;
        }
        case Primitive::kPrimDouble: {
          double double_res = 0;
          switch (x.type) {
            case Primitive::kPrimInt:
              double_res = static_cast<double>(uop->Evaluate(x.value.i));
              break;
            case Primitive::kPrimFloat:
              double_res = static_cast<double>(uop->Evaluate(x.value.f));
              break;
            case Primitive::kPrimLong:
              double_res = static_cast<double>(uop->Evaluate(x.value.l));
              break;
            case Primitive::kPrimDouble:
              double_res = uop->Evaluate(x.value.d);
              break;
            default:
              success = false;
          };
          res.value.d = double_res;
          break;
        }
        default:
          success = false;
      };
    } else if (insn->IsTypeConversion()) {
      HTypeConversion* cast = insn->AsTypeConversion();
      success = EvaluateCast(cast, x, res);
    }

    return success;
  }

  bool Evaluate(HBinaryOperation* insn, const ConstantValue& x, const ConstantValue& y, ConstantValue& res) {
    DCHECK(insn != nullptr);
    bool success = true;

    res.type = GetResultType(insn);

    DCHECK(res.type != Primitive::kPrimBoolean);
    DCHECK(x.type != Primitive::kPrimBoolean);
    DCHECK(y.type != Primitive::kPrimBoolean);

    switch (res.type) {
      case Primitive::kPrimInt: {
        int32_t int_res = 0;
        switch (x.type) {
          case Primitive::kPrimInt:
            DCHECK(y.type == Primitive::kPrimInt);
            int_res = insn->Evaluate(x.value.i, y.value.i);
            break;
          case Primitive::kPrimFloat:
            DCHECK(y.type == Primitive::kPrimFloat);
            int_res = static_cast<int32_t>(insn->Evaluate(x.value.f, y.value.f));
            break;
          case Primitive::kPrimLong:
            DCHECK(y.type == Primitive::kPrimLong);
            int_res = static_cast<int32_t>(insn->Evaluate(x.value.l, y.value.l));
            break;
          case Primitive::kPrimDouble:
            DCHECK(y.type == Primitive::kPrimDouble);
            int_res = static_cast<int32_t>(insn->Evaluate(x.value.d, y.value.d));
            break;
          default:
            success = false;
        };
        res.value.i = int_res;
        break;
      }
      case Primitive::kPrimFloat: {
        float float_res = 0.0f;
        switch (x.type) {
          case Primitive::kPrimInt:
            DCHECK(y.type == Primitive::kPrimInt);
            float_res = static_cast<float>(insn->Evaluate(x.value.i, y.value.i));
            break;
          case Primitive::kPrimFloat:
            DCHECK(y.type == Primitive::kPrimFloat);
            float_res = insn->Evaluate(x.value.f, y.value.f);
            break;
          case Primitive::kPrimLong:
            DCHECK(y.type == Primitive::kPrimLong);
            float_res = static_cast<float>(insn->Evaluate(x.value.l, y.value.l));
            break;
          case Primitive::kPrimDouble:
            DCHECK(y.type == Primitive::kPrimDouble);
            float_res = static_cast<float>(insn->Evaluate(x.value.d, y.value.d));
            break;
          default:
            success = false;
        };
        res.value.f = float_res;
        break;
      }
      case Primitive::kPrimLong: {
        int64_t long_res = 0;
        switch (x.type) {
          case Primitive::kPrimInt:
            DCHECK(y.type == Primitive::kPrimInt);
            long_res = static_cast<int64_t>(insn->Evaluate(x.value.i, y.value.i));
            break;
          case Primitive::kPrimFloat:
            DCHECK(y.type == Primitive::kPrimFloat);
            long_res = static_cast<int64_t>(insn->Evaluate(x.value.f, y.value.f));
            break;
          case Primitive::kPrimLong:
            if (y.type == Primitive::kPrimLong) {
              long_res = insn->Evaluate(x.value.l, y.value.l);
            } else {
              long_res = insn->Evaluate(x.value.l, static_cast<int64_t>(y.value.i));
            }
            break;
          case Primitive::kPrimDouble:
            DCHECK(y.type == Primitive::kPrimDouble);
            long_res = static_cast<float>(insn->Evaluate(x.value.d, y.value.d));
            break;
          default:
            success = false;
        };
        res.value.l = long_res;
        break;
      }
      case Primitive::kPrimDouble: {
        double double_res = 0.0;
        switch (x.type) {
          case Primitive::kPrimInt:
            DCHECK(y.type == Primitive::kPrimInt);
            double_res = static_cast<double>(insn->Evaluate(x.value.i, y.value.i));
            break;
          case Primitive::kPrimFloat:
            DCHECK(y.type == Primitive::kPrimFloat);
            double_res = static_cast<double>(insn->Evaluate(x.value.f, y.value.f));
            break;
          case Primitive::kPrimLong:
            DCHECK(y.type == Primitive::kPrimLong);
            double_res = static_cast<double>(insn->Evaluate(x.value.l, y.value.l));
            break;
          case Primitive::kPrimDouble:
            DCHECK(y.type == Primitive::kPrimDouble);
            double_res = insn->Evaluate(x.value.d, y.value.d);
            break;
          default:
            success = false;
        };
        res.value.d = double_res;
        break;
      }
      default:
        success = false;
    };

    return success;
  }

  std::string GetInvokeMethodName(HInstruction* insn, DexCompilationUnit* outer_compilation_unit) {
    if (insn == nullptr) {
      return nullptr;
    }
    HInvokeStaticOrDirect* call = insn->AsInvokeStaticOrDirect();
    if (call == nullptr) {
      return nullptr;
    }
    return PrettyMethod(call->GetDexMethodIndex(), *outer_compilation_unit->GetDexFile());
  }

  std::string GetMethodName(const DexCompilationUnit& c_unit) {
    return PrettyMethod(c_unit.GetDexMethodIndex(), *c_unit.GetDexFile());
  }

  std::string GetMethodName(const HGraph* graph) {
    DCHECK(graph != nullptr);

    return PrettyMethod(graph->GetMethodIdx(), graph->GetDexFile());
  }

  char GetTypeId(Primitive::Type type) {
    // Note that Primitive::Descriptor would not work for us
    // because it does not handle reference types (that is kPrimNot).
    switch (type) {
      case Primitive::kPrimBoolean: return 'z';
      case Primitive::kPrimByte: return 'b';
      case Primitive::kPrimChar: return 'c';
      case Primitive::kPrimShort: return 's';
      case Primitive::kPrimInt: return 'i';
      case Primitive::kPrimLong: return 'j';
      case Primitive::kPrimFloat: return 'f';
      case Primitive::kPrimDouble: return 'd';
      case Primitive::kPrimNot: return 'l';
      case Primitive::kPrimVoid: return 'v';
    }
    LOG(FATAL) << "Unreachable";
    return 'v';
  }

  void RemoveEnvAsUser(HInstruction* instruction) {
    for (HEnvironment* environment = instruction->GetEnvironment();
         environment != nullptr;
         environment = environment->GetParent()) {
      for (size_t i = 0, e = environment->Size(); i < e; ++i) {
        if (environment->GetInstructionAt(i) != nullptr) {
          environment->RemoveAsUserOfInput(i);
        }
      }
    }
  }

  void RemoveAsUser(HInstruction* instruction) {
    for (size_t i = 0; i < instruction->InputCount(); i++) {
      instruction->RemoveAsUserOfInput(i);
    }

    RemoveEnvAsUser(instruction);
  }

  void RemoveFromEnvironmentUsers(HInstruction* insn) {
    for (HUseIterator<HEnvironment*> use_it(insn->GetEnvUses()); !use_it.Done();
         use_it.Advance()) {
      HUseListNode<HEnvironment*>* user_node = use_it.Current();
      HEnvironment* user = user_node->GetUser();
      user->SetRawEnvAt(user_node->GetIndex(), nullptr);
    }
  }

  ConstantValue::ConstantValue(HConstant* constant) {
    DCHECK(constant != nullptr);

    if (constant->IsIntConstant()) {
      type = Primitive::kPrimInt;
      value.i = constant->AsIntConstant()->GetValue();
    } else if (constant->IsFloatConstant()) {
      type = Primitive::kPrimFloat;
      value.f = constant->AsFloatConstant()->GetValue();
    } else if (constant->IsLongConstant()) {
      type = Primitive::kPrimLong;
      value.l = constant->AsLongConstant()->GetValue();
    } else if (constant->IsDoubleConstant()) {
      type = Primitive::kPrimDouble;
      value.d = constant->AsDoubleConstant()->GetValue();
    } else {
      LOG(FATAL) << "Could not handle constant value";
    }
  }

  HConstant* ConstantValue::ToHConstant(HGraph* graph) const {
    DCHECK(graph != nullptr);

    switch (type) {
      case Primitive::kPrimInt:
        return graph->GetIntConstant(value.i);
      case Primitive::kPrimLong:
        return graph->GetLongConstant(value.l);
      case Primitive::kPrimFloat:
        return graph->GetFloatConstant(value.f);
      case Primitive::kPrimDouble:
        return graph->GetDoubleConstant(value.d);
      default:
        LOG(FATAL) << "Unknown constant type: " << type;
    };

    return nullptr;
  }

  void DeconstructFP(double value, bool is_double, bool with_implicit_one, int32_t& sign, int32_t& power, int64_t& mantissa) {
    int32_t exponent_length;
    int32_t mantissa_length;
    int64_t as_int;
    const int64_t one = 1;

    if (is_double) {
      union {
        double d;
        int64_t l;
      } conversion;
      conversion.d = value;
      as_int = conversion.l;
      exponent_length = 11;
      mantissa_length = 52;
    } else {
      union {
        float f;
        int32_t i;
      } conversion;
      conversion.f = static_cast<float>(value);
      // Extend to 64 bit.
      as_int = conversion.i | (int64_t)0;
      exponent_length = 8;
      mantissa_length = 23;
    }

    // Get the actual output values.
    // Sign is the leftmost (31th or 63th) bit.
    sign = static_cast<int32_t>((as_int >> (is_double ? 63 : 31)) & 1);
    // Mantissa bits are the last ones.
    mantissa = as_int & ((one << mantissa_length) - one);
    // Exponent bits are between them.
    int32_t exponent = static_cast<int32_t>
                       ((as_int >> mantissa_length) &
                       ((one << exponent_length) - one));
    // For non-zero values, power is exponent - 01111...11.
    if (value == 0.0) {
      power = 0;
    } else {
      power = exponent - ((1 << (exponent_length - 1)) - 1);
    }

    // For non-zero value, add an implicit 1 to mantissa.
    if (with_implicit_one && (value != 0.0)) {
      mantissa |= ((int64_t)1 << mantissa_length);
    }
  }

  int32_t CountEndZerosInMantissa(int64_t mantissa, bool is_double) {
    const int32_t limit = (is_double ? 52 : 23);
    int32_t ret = 0;
    while ((ret < limit) && ((mantissa & 1) == 0)) {
      ret++;
      mantissa >>= 1;
    }
    return ret;
  }

  void SplitStringIntoSet(const std::string& s, char delim, std::unordered_set<std::string>& tokens) {
    std::stringstream stream(s);
    std::string item;
    while (std::getline(stream, item, delim)) {
      if (item.size() > 1) {
        tokens.insert(item);
      }
    }
  }

  std::string CalledMethodName(HInvokeStaticOrDirect* call) {
    DCHECK(call != nullptr);
    const MethodReference target_method = call->GetTargetMethod();
    return PrettyMethod(target_method.dex_method_index,
                        *target_method.dex_file);
  }
}  // namespace art
