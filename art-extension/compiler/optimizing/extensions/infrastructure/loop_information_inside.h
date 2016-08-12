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

#ifndef ART_OPT_INFRASTRUCTURE_LOOP_INFORMATION_INSIDE_H_
#define ART_OPT_INFRASTRUCTURE_LOOP_INFORMATION_INSIDE_H_

/*
 * This is done without namespace in order to be included
 * both in loop_information.h and nodes.h.
 */

#ifndef NDEBUG
#define LOOPINFO_MAGIC 0xdeadcafe
#define LOOPINFO_TO_LOOPINFO_X86(X) HLoopInformation_X86::DownCast(X)
#else
#define LOOPINFO_TO_LOOPINFO_X86(X) static_cast<HLoopInformation_X86*>(X)
#endif

// Forward declarations.
class HInductionVariable;
class HOptimization_X86;

class HLoopInformation_X86 : public HLoopInformation {
 public:
  HLoopInformation_X86(HBasicBlock* block, HGraph* graph) :
      HLoopInformation(block, graph),
      depth_(0), count_up_(false),
      suppress_suspend_check_(false), bottom_tested_(false),
      outer_(nullptr), sibling_previous_(nullptr),
      sibling_next_(nullptr), inner_(nullptr),
      test_suspend_(nullptr), suspend_(nullptr),
      iv_list_(graph->GetArena(), 0),
      inter_iteration_variables_(graph->GetArena()->Adapter(kArenaAllocMisc)),
      peeled_blocks_(graph->GetArena(), 0), graph_(graph) {
#ifndef NDEBUG
        down_cast_checker_ = LOOPINFO_MAGIC;
#endif
  }

  void ResetRelationships() {
    depth_ = 0;
    inner_ = nullptr;
    outer_ = nullptr;
    sibling_next_ = nullptr;
    sibling_previous_ = nullptr;
  }

  /**
   * @brief Clear back edges.
   */
  void ClearBackEdges() {
    back_edges_.Reset();
  }

  /**
   * @brief Get next sibling loop.
   * @return the next sibling loop.
   */
  HLoopInformation_X86* GetNextSibling() const {
    return sibling_next_;
  }

  /**
   * @brief Get previous sibling loop.
   * @return the previous sibling loop.
   */
  HLoopInformation_X86* GetPrevSibling() const {
    return sibling_previous_;
  }

  /**
   * @brief Get previous sibling loop.
   * @return the previous sibling loop.
   */
  HLoopInformation_X86* GetInner() const {
    return inner_;
  }

  /**
   * @brief Get previous sibling loop.
   * @return the previous sibling loop.
   */
  HLoopInformation_X86* GetParent() const {
    return outer_;
  }

  /**
   * @brief Add the parameter in the hierarchy or it is a sibling loop.
   * @param other the other HLoopInformation.
   */
  void Add(HLoopInformation_X86* other);

  /**
   * @brief Dump the loop hierarchy.
   * @param max_depth the maximum depth for the printout (default: 10).
   */
  void Dump(int max_depth = 10) const;

  /**
   * @brief Get the number of blocks.
   * @return the number of blocks in the HLoopInformation_X86.
   */
  int NumberOfBlocks() const {
    return blocks_.NumSetBits();
  }

  /**
   * @brief Set the depth for each HLoopInformation_X86.
   * @param depth the current depth (default 0).
   */
  void SetDepth(int depth = 0);

  /**
   * @brief Get Depth.
   * @return the depth of the loop.
   */
  int GetDepth() const {
    return depth_;
  }

  /**
   * @brief Is this the inner loop?
   * @return whether or not this loop is the inner loop?
   */
  bool IsInner() const {
    return inner_ == nullptr;
  }

  /**
    * @brief Get the loop basic induction variable.
    * @return the basic induction variable.
    */
  HInductionVariable* GetBasicIV() const {
    return bound_info_.loop_biv_;
  }

  /**
   * @brief Get the induction variables.
   * @return the induction variables.
   */
  GrowableArray<HInductionVariable*>& GetInductionVariables() {
    return iv_list_;
  }

  /**
   * @brief Get the induction variable using the instruction.
   * @param insn the HInstruction we care about, it can be the linear or phi.
   * @return the induction variable associated to the register.
   */
  HInductionVariable* GetInductionVariable(HInstruction* insn) const;

  /**
   * @brief Clear inter iteration variables.
   */
  void ClearInterIterationVariables() {
    inter_iteration_variables_.clear();
  }

  /**
   * @brief Get the inter-iteration variables.
   * @return the set of inter-iteration variables.
   */
  const ArenaSet<int>& GetInterIterationVariables() const {
    return inter_iteration_variables_;
  }

  /**
   * @brief Add a variable as an inter-iteration variable.
   * @param reg the register.
   */
  void AddInterIterationVariable(int reg) {
    // inter_iteration_variables_ is a set, inserting it does not
    //   mean it really gets inserted if it is already there.
    inter_iteration_variables_.insert(reg);
  }

  /**
   * @brief Set the count up loop boolean.
   */
  void SetCountUpLoop(bool b) {
    count_up_ = b;
  }

  /**
   * @brief Get the count up loop boolean.
   * @return whether or not the HLoopInformation_X86 is count up.
   */
  bool IsCountUp() const {
    return count_up_;
  }

  /**
   * @brief  Set the bottom tested loop boolean.
   * @param b 'true' if the loop should be reported as bottom tested.
   */
  void SetBottomTested(bool b) {
    bottom_tested_ = b;
  }

  /**
   * @brief Is the loop bottom tested?
   * @return 'true' if the test for continuing the loop is at the bottom of the loop.
   */
  bool IsBottomTested() const {
    return bottom_tested_;
  }

  /**
   * @brief Is the basic block executed every iteration?
   * @param bb the basic block.
   * @return whether or not the block is executed every iteration.
   */
  bool ExecutedPerIteration(HBasicBlock* bb) const;

  /**
   * @brief Is the instruction executed every iteration?
   * @param candidate the HInstruction.
   * @return whether or not the instruction is executed every iteration.
   */
  bool ExecutedPerIteration(HInstruction* candidate) const;

  /**
   * @brief Used to obtain number of loop exits edges.
   * @return Returns the number of loop exit edges.
   */
  size_t GetLoopExitCount() const;

  /**
   * @brief Does the loop only have one exit edge?
   * @return Returns whether or not the loop has one single exit edge.
   */
  bool HasOneExitEdge() const;

  /**
   * @brief Get the exit block if there is only one exit edge.
   * @return Returns the exit block, nullptr if more than one.
   */
  HBasicBlock* GetExitBlock() const;

  /**
   * @brief Get the number of iterations of given basic block.
   *        Before calling this method, ensure that bb is
   *        ExecutedPerIteration.
   * @param bb The basic block.
   * @return the number of iterations, -1 if unknown.
   */
  int64_t GetNumIterations(HBasicBlock* bb) const;

  /**
   * @brief Do we know the number of iterations?
   * @return whether we know the number of iterations.
   */
  bool HasKnownNumIterations() const {
    return (bound_info_.num_iterations_ != -1);
  }

  /**
   * @brief Split a SuspendCheck into a TestSuspend and an Suspend.
   */
  void SplitSuspendCheck();

  /**
   * @brief Insert an HInstruction before the call to Suspend.  Will
   * call SplitSuspendCheck if there isn't already a split suspend.
   * @param instruction HInstruction to insert just before call to Suspend.
   * @returns 'true' if the suspend block was created.
   * @note RebuildDominators() must be called before an optimization that uses
   * this method is returns 'true'.
   */
  bool InsertInstructionInSuspendBlock(HInstruction* instruction);

  /**
   * @brief Add a block to all nested loops, and set the loop_information
   * for the block to 'this'.
   * @param block Block to add to loop nest.
   */
  void AddToAll(HBasicBlock* block);

  /**
   * @brief Return a pointer to the HSuspend instruction for this loop, if present.
   * @returns HSuspend instruction, or nullptr.
   */
  HSuspend* GetSuspend() const { return suspend_; }

  /**
   * @brief Set the pointer to the HSuspend instruction for this loop.
   * @param suspend HSuspend instruction for the loop.
   */
  void SetSuspend(HSuspend* suspend) { suspend_ = suspend; }

  /**
   * @brief Does this loop have an HSuspend instruction?
   * @returns 'true' if there is an HSuspend instruction present in the loop.
   */
  bool HasSuspend() const { return suspend_ != nullptr; }

  /**
   * @brief Return a pointer to the HTestSuspend instruction for this loop, if present.
   * @returns HTestSuspend instruction, or nullptr.
   */

  HTestSuspend* GetTestSuspend() const { return test_suspend_; }

  /**
   * @brief Set the pointer to the HTestSuspend instruction for this loop.
   * @param test_suspend HTestSuspend instruction for the loop.
   */
  void SetTestSuspend(HTestSuspend* test_suspend) { test_suspend_ = test_suspend; }

  /**
   * @brief Does this loop have an HTestSuspend instruction?
   * @returns 'true' if there is an HTestSuspend instruction present in the loop.
   */
  bool HasTestSuspend() const { return test_suspend_ != nullptr; }

  /**
   * @brief Should a SuspendCheck be inserted into this loop?
   * @param value 'true' if no suspend check is needed in the loop.
   */
  void SetSuppressSuspendCheck(bool value) { suppress_suspend_check_ = value; }

  /**
   * @brief Does this loop need to have a HSuspendCheck added?
   * @returns 'true' if a SuspendCheck should not be added.
   * @note If we have converted to HTestSuspend/HSuspend, we don't want
   * another HSuspendCheck to be added to the loop.
   */
  bool DontAddSuspendCheck() const OVERRIDE { return suppress_suspend_check_ || HasTestSuspend(); }

  /*
   * @brief Is the loop very simple?
   * @details A loop is very simple if it meets the following criteria:
   * -# Has one or two basic blocks (optional with the @p require_at_most_two_blocks parameter).
   * -# Is not nested.
   * -# Does not have "complicated" bytecodes (invokes, switches, throws, new-instance).
   * @param require_at_most_two_blocks Whether having one or two blocks is a requirement.
   * @return Returns whether or not a loop is very simple.
   */
  bool IsVerySimple(bool require_at_most_two_blocks = true) const;

  /**
   * @return True if the loop has at least one HInvoke among the instructions
   * of its basic blocks, or false otherwise.
   */
  bool HasInvokes() const;

#ifndef NDEBUG
  static HLoopInformation_X86* DownCast(HLoopInformation* info) {
    HLoopInformation_X86* res = static_cast<HLoopInformation_X86*>(info);

    if (res != nullptr) {
      DCHECK_EQ(res->down_cast_checker_, LOOPINFO_MAGIC);
    }
    return res;
  }
#endif

  /**
   * @brief Compute the bound information.
   */
  bool ComputeBoundInformation();

  /**
   * @brief Get the HBoundInformation.
   * @return the bound information.
   */
  const HLoopBoundInformation& GetBoundInformation() const {
    return bound_info_;
  }

  /**
   * @brief Used to check if loop can be peeled.
   * @param optim Useful during development of using the interface to understand
   * why peeling failed.
   * @return Returns true if loop peeling will surely succeed and false otherwise.
   */
  bool IsPeelable(HOptimization_X86* optim) const;

  /**
   * @brief Used to peel one iteration from the loop on top.
   * @param optim Useful during development of using the interface to understand
   * @details This method should be called when caller knows the loop is peelable.
   */
  void PeelHead(HOptimization_X86* optim) {
    DCHECK_EQ(IsPeelable(optim), true);
    bool peeled = PeelHelperHead();
    CHECK_EQ(peeled, true);
  }

  /**
   * @brief Used to peel one iteration from the loop on top.
   * @details This method should be called when caller does not know if loop is peelable.
   * @param optim Useful during development of using the interface to understand
   * @return Returns true if peeling succeeds and false otherwise.
   */
  bool PeelHeadWithCheck(HOptimization_X86* optim) {
    if (IsPeelable(optim)) {
      return PeelHelperHead();
    }
    return false;
  }

  /**
   * @brief Used to check whether a loop has already been peeled.
   * @returns True if the loop has been peeled.
   */
  bool HasBeenPeeled() {
    return !peeled_blocks_.IsEmpty();
  }

  /**
   * @brief Used to check if loop has a catch handler block.
   * @return Returns true if loop has catch block.
   */
  bool HasCatchHandler() const;

  /**
   * @brief This is used to get a list of ids for the peeled blocks.
   * @details The reason this does not return a list of block pointers is because
   * we do not want to maintain correctness of this list. Namely, other optimizations
   * may remove/merge blocks and thus invalidate some blocks. The list returned here
   * is for reference only to see if a known id was ever the result of peeling. This
   * method can be used in testing to see which blocks were result of peeling.
   * @return Returns the list of ids for the peeled blocks.
   */
  const GrowableArray<int>& GetPeeledBlockIds() {
    return peeled_blocks_;
  }

  /*
   * @brief Determines whether the loop contains opcodes that can exit the block unexpectedly.
   * @param ignore_suspends Should HSuspendCheck and HSuspend be ignored during the check?
   * @return 'true' if any instruction in the loop has an environment.
   */
  bool CanSideExit(bool ignore_suspends = true) const;

  /**
   * @brief Estimate the number of cycles for one loop execution.
   * @param cost Returned cycle estimation count.
   * @return 'true' if the estimation was successful.
   */
  bool GetLoopCost(uint64_t* cost) const;

  /**
   * @brief Ensure that the links from this HLoopInformation_X86 are reset.
   */
  void ResetLinks() {
    sibling_next_ = nullptr;
    sibling_previous_ = nullptr;
    inner_ = nullptr;
    outer_ = nullptr;
  }

  /**
   * @brief Removes the loop from the graph it belongs to.
   * @details This method takes care of handling graph and loop internal structures as well, especially:
   * - Delete the basic blocks inside of the loop.
   * If the loop is nested:
   * - Remove the inner loop from its first parent.
   * - Remove the loop's BBs from every outer loop level.
   * Currently, only innermost loops are supported by this method.
   * @return Returns true if the loop has been successfully removed from the graph, or false otherwise.
   */
  bool RemoveFromGraph();

  /**
   * @brief Determines whether the loop contains instructions that can throw exceptions.
   * @return Whether the loop contains instructions that can throw exceptions.
   */
  bool CanThrow() const;

  /**
   * @brief Compute the number of instructions in a loop. Phi nodes and
   * suspend checks (if requested so) are not considered in the computation.
   * @param skip_suspend_checks Whether we want to count suspend checks as loop instructions.
   * This argument is true by default.
   */
  uint64_t CountInstructionsInBody(bool skip_suspend_checks = true) const;

  /**
   * @return The graph attached to the loop information instance.
   */
  HGraph* GetGraph() const {
    return graph_;
  }

  /**
    * @brief Returns the phi input that is either inside or outside of the loop.
    * @param phi A phi node that must have 2 inputs, and that must be in a loop.
    * @param inside_of_loop Whether we want to retrieve the phi input that is inside
    * of the loop.
    * @return Returns the phi input that is either inside or outside of the loop.
    */
  HInstruction* PhiInput(HPhi* phi, bool inside_of_loop);

 protected:
  /**
   * @brief Find the constant entry SSA associated to the Phi instruction.
   * @param phi the HPhi instruction.
   * @return the constant that is the entry point to the Phi, nullptr otherwise.
   */
  HConstant* FindBIVEntrySSA(HPhi* phi) const;

  /**
   * @brief Fill the floating-point bound information.
   * @param entry_value the constant definining the loop's start value.
   * @param is_double are we dealing with 64-bit value.
   * @return whether or not the information was filled.
   */
  bool FillFloatingPointBound(HConstant* entry_value, bool is_double);

  /**
   * @brief Determines whether the loop contains certain opcodes that would block memory aliasing.
   * @return whether or not the loop contains any non supported opcodes in its basic blocks.
   */
  bool ContainsComplexInstructions() const;

#ifndef NDEBUG
  uint32_t down_cast_checker_;
#endif

  int depth_;
  bool count_up_;
  bool suppress_suspend_check_;
  bool bottom_tested_;

  HLoopInformation_X86* outer_;
  HLoopInformation_X86* sibling_previous_;
  HLoopInformation_X86* sibling_next_;
  HLoopInformation_X86* inner_;

  /** @brief The bound information. */
  HLoopBoundInformation bound_info_;

  /** @brief Pointer to the split HTestSuspend, if present. */
  HTestSuspend* test_suspend_;

  /** @brief Pointer to the split HSuspend, if present. */
  HSuspend* suspend_;

  /** @brief The Induction Variable list. */
  GrowableArray<HInductionVariable*> iv_list_;

  /** @brief Inter-iteration dependent variables. */
  ArenaSet<int> inter_iteration_variables_;

  /**
   * @brief Holds the block ids of blocks that were peeled.
   */
  GrowableArray<int> peeled_blocks_;

  /**
   * @brief Internal utility used for peeling.
   * @details This is guaranteed to succeed if IsPeelable returns true.
   */
  bool PeelHelperHead();

  /** @brief The HGraph the loop belongs to. */
  HGraph* graph_;
};

#endif  // ART_OPT_INFRASTRUCTURE_LOOP_INFORMATION_INSIDE_H_
