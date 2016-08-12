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

#ifndef COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_EXT_UTILITY_H
#define COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_EXT_UTILITY_H

#include "base/stringprintf.h"
#include "driver/dex_compilation_unit.h"
#include "nodes.h"
#include <ostream>
#include <unordered_set>

namespace art {
  // Forward declarations.
  struct ConstantValue;
  class HInstruction;

  // This macro can be used to print messages within passes. The possible usage is:
  // PRINT_PASS_MESSAGE(this, "Value of x = %.2f", x);
  #define PRINT_PASS_MESSAGE(pass, format, ...) \
    do { \
      if (pass->IsVerbose()) { \
        LOG(INFO) << pass->GetPassName() << ": " << StringPrintf(format, ##__VA_ARGS__); \
      } \
    } while (false)

  // PRINT_PASS_OSTREAM_MESSAGE(this, x << yy << zz);
  #define PRINT_PASS_OSTREAM_MESSAGE(pass, ...) \
    do { \
      if (pass->IsVerbose()) { \
        LOG(INFO) << pass->GetPassName() << ": " << __VA_ARGS__; \
      } \
    } while (false)

  /**
   * @brief Get the integer constant value from the HConstant.
   * @param constant the HConstant we care about.
   * @param value the destination where we put the HConstant value.
   * @return if we obtained the constant value.
   */
  bool GetIntConstantValue(HConstant* constant, int64_t& value);

  /**
   * @brief Get the compare instruction from the uses of instruction.
   * @param instruction the base instruction.
   * @return the compare instruction based on the uses of the instruction,
             instruction if neither is one.
   */
  HInstruction* GetCompareInstruction(HInstruction* instruction);

  /**
   * @brief Get the FP Constant value.
   * @param constant the HConstant.
   * @param value the value of the constant filled out.
   * @return whether or not the constant is assessed.
   */
  bool GetFPConstantValue(HConstant* constant, double& value);

  /**
   * @brief Flip the condition if we flip the operands.
   * @details if we have A OP B, this returns OP2 with B OP2 A being equivalent.
   * @param cond the original condition.
   * @return the new flipped condition.
   */
  IfCondition FlipConditionForOperandSwap(IfCondition cond);


  /**
   * @brief Write an instruction to an output stream.
   * @param os Output stream.
   * @param instruction Instruction to write.
   */
  std::ostream& operator<<(std::ostream& os, HInstruction* instruction);

  /**
   * @brief Evaluates a cast operation.
   * @param conv The HTypeConversion input to cast.
   * @param x The ConstantValue holding the input value of the conversion.
   * @param res The ConstantValue resulting of the cast operation.
   * @return Returns true if the cast operation was successfully evaluated, or false otherwise.
   */
  bool EvaluateCast(HTypeConversion* conv, const ConstantValue& x, ConstantValue& res);

  /**
   * @brief Evaluates an unary instruction with a specific constant input.
   * @param insn The HInstruction we want to evaluate.
   * @param x The HConstant value we want to set as a specific input.
   * @param res The ConstantValue resulting of the unary operation.
   * @return Returns true if the unary operation was successfully evaluated, or false otherwise.
   */
  bool Evaluate(HInstruction* insn, const ConstantValue& x, ConstantValue& res);

  /**
   * @brief Evaluates a binary instruction with specific constant inputs.
   * @param insn The binary operation we want to evaluate.
   * @param x The left operand value of the instruction.
   * @param y The right operand value of the instruction.
   * @param res The ConstantValue resulting of the binary operation.
   * @return Returns true if the binary operation was successfully evaluated, or false otherwise.
   */
  bool Evaluate(HBinaryOperation* insn, const ConstantValue& x, const ConstantValue& y, ConstantValue& res);

  /**
   * @brief Facility to get the name of a method from an invoke call.
   * @param insn The corresponding invoke call.
   * @param outer_compilation_unit The DexCompilationUnit of the method call.
   */
  std::string GetInvokeMethodName(HInstruction* insn, DexCompilationUnit* outer_compilation_unit);

  /**
   * @brief Facility to get the name of the current method.
   * @param graph The HGgraph corresponding to the method.
   * @return The name of the current method.
   */
  std::string GetMethodName(const HGraph* graph);

  /**
   * @brief Facility to get the name of the current method.
   * @param c_unit The DexCompilationUnit of the current method.
   * @return The name of the current method.
   */
  std::string GetMethodName(const DexCompilationUnit& c_unit);

  /**
   * @brief Get the character equivalent of the type for print or debug purposes.
   * @param type The type we want to get the equivalent of.
   * @return The resulting character equivalent.
   */
  char GetTypeId(Primitive::Type type);

  /**
   * @brief Remove the given instruction as user of its inputs and its environment inputs.
   * @param instruction The HInstruction to handle.
   * @details RemoveEnvAsUser is called internally to handle environment inputs.
   */
  void RemoveAsUser(HInstruction* instruction);

  /**
   * @brief Remove the given instruction's environment as user of its inputs.
   * @param instruction The HInstruction to handle.
   */
  void RemoveEnvAsUser(HInstruction* instruction);

  /**
   * @brief Used to mark the current instruction as not being used by any environments.
   * @param instruction The HInstruction to handle.
   */
  void RemoveFromEnvironmentUsers(HInstruction* instruction);

  /**
   * @brief Class representing constant values that is used for static evaluation.
   * @details Structure representing a constant value. The reason we are not using
   * HConstant is because this class is read-only, and the constant value they
   * hold are only defined in their derived class (HIntConstant for example).
   *
   * With this structure, we want to be able to hold the different types of
   * constants so that it can be easily dealt with independently of their type.
   * It is especially useful for static evaluation of an instruction.
   */
  struct ConstantValue {
    /** The type of the constant value. */
    Primitive::Type type;

    /** The effective value holder. It currently supports JAVA int, long, float and double types. */
    union {
      int32_t i;
      int64_t l;
      float f;
      double d;
    } value;

    /**
     * @brief Copy constructor.
     */
    ConstantValue(const ConstantValue& c_value) : type(c_value.type),
      value(c_value.value) {}

    /**
     * @brief Copy assigner.
     */
    ConstantValue& operator=(const ConstantValue& c_value) {
      type = c_value.type;
      value = c_value.value;

      return *this;
    }

    /**
     * @brief Default constructor.
     */
    explicit ConstantValue() {
      Reset();
    }

    /**
     * @brief Reset the values of the class.
     */
    void Reset() {
      type = Primitive::kPrimLong;
      value.l = 0L;
    }

    /**
     * @brief Integer constructor.
     * @param constant The constant to initialize from.
     */
    explicit ConstantValue(HIntConstant* constant) : type(Primitive::kPrimInt) {
      value.i = constant->GetValue();
    }

    /**
     * @brief Long constructor.
     * @param constant The constant to initialize from.
     */
    explicit ConstantValue(HLongConstant* constant) : type(Primitive::kPrimLong) {
      value.l = constant->GetValue();
    }

    /**
     * @brief Float constructor.
     * @param constant The constant to initialize from.
     */
    explicit ConstantValue(HFloatConstant* constant) : type(Primitive::kPrimFloat) {
      value.f = constant->GetValue();
    }

    /**
     * @brief Double constructor.
     * @param constant The constant to initialize from.
     */
    explicit ConstantValue(HDoubleConstant* constant) : type(Primitive::kPrimDouble) {
      value.d = constant->GetValue();
    }

    /**
     * @brief HConstant constructor.
     * @details Initializes the structure according to the HConstant dynamic type.
     * @param constant The constant to initialize from.
     */
    explicit ConstantValue(HConstant* constant);

    /**
     * @brief Transforms the ConstantValue structure to a HConstant structure.
     * The HConstant structure is added to the provided @p graph.
     * @param graph The HGraph the HConstant will be added to.
     * @return The corresponding HConstant pointer if the transformation occured
     * succesfully, or nullptr otherwise.
     */
    HConstant* ToHConstant(HGraph* graph) const;
  };

  /**
   * Write constant value to ostream.
   * @param os Stream to write value to.
   * @param cv Constant value to write.
   * @returns os.
   */
  std::ostream& operator<<(std::ostream& os, const ConstantValue& cv);

  /**
   * @brief Get the negated conditional operation.
   * For example, for if-ge, it returns if-lt.
   * @param cond The conditional if operation to negate.
   * @return Returns the negated conditional if.
   */
  IfCondition NegateCondition(IfCondition cond);

  /**
   * @brief Get sign, power and mantissa of floating point value.
   * @param value The FP value represented as double.
   * @param is_double Whether the value is double-precision FP or not.
   * @param with_implicit_one Whether we should get mantissa with implicit 1 or not.
   * @param sign The output value for sign (0 or 1).
   * @param power The output value for power. Derived from the exponent of FP.
                  For non-zeros, power = exponent - 1111...11; power(1) = 0.
   * @param mantissa The output value for mantissa. If with_implicit_one was true, it will
   *                 also contain the 23-rd (52-nd for double) bit set to 1 for non-zero value.
   */
  void DeconstructFP(double value, bool is_double, bool with_implicit_one, int32_t& sign, int32_t& power, int64_t& mantissa);

  /**
   * @brief Get the number of zeros in the end of mantissa of floating point value.
   * @param mantissa The mantissa.
   * @param is_double Whether the mantissa is of double-precision FP or not.
   * @return The number of zeros in the end of mantissa. The maximum return
   *         value is mantissa length (23 for single FP and 52 for double FP).
   */
  int CountEndZerosInMantissa(int64_t mantissa, bool is_double);

  /**
   * @brief Split string by delimeter and put tokens to the set.
   * @param s The string to split.
   * @param delim The delimeter character.
   * @param tokens The output set for tokens.
   */
  void SplitStringIntoSet(const std::string& s, char delim, std::unordered_set<std::string>& tokens);

  /**
   * @brief Returns pretty method name for the invoke.
   * @param call The invoke.
   * @return Called method name as string.
   */
  std::string CalledMethodName(HInvokeStaticOrDirect* call);
}  // namespace art

#endif  // COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_EXT_UTILITIES_H
