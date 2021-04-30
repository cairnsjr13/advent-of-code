package com.cairns.rich.aoc._2020;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cairns.rich.aoc.EnumUtils;

class Day08 extends Base2020 {
  private static final Map<String, InstructionType> instructionTypes = EnumUtils.getLookup(InstructionType.class);
  
  @Override
  protected void run() {
    List<Instruction> instructions = fullLoader.ml(Instruction::new);
    System.out.println(runUntilBeforeLoopGetAccumulator(instructions));
    System.out.println(getAccumulatorOnTerminate(instructions));
  }
  
  private int runUntilBeforeLoopGetAccumulator(List<Instruction> instructions) {
    State state = new State();
    while (true) {
      if (state.executedInstructions.contains(state.instructionIndex)) {
        return state.accumulator;
      }
      state.executedInstructions.add(state.instructionIndex);
      instructions.get(state.instructionIndex).execute(state);
    }
  }
  
  private int getAccumulatorOnTerminate(List<Instruction> instructions) {
    for (int i = 0; i < instructions.size(); ++i) {
      Instruction instruction = instructions.get(i);
      if (instruction.instructionType == InstructionType.Acc) {
        continue;
      }
      else if (instruction.instructionType == InstructionType.Jmp) {
        instructions.set(i, new Instruction(InstructionType.Nop, instruction.argument));
      }
      else {
        instructions.set(i, new Instruction(InstructionType.Jmp, instruction.argument));
      }
      Integer terminatedAccumulator = getTerminatedAccumulatorOrNullOnLoop(instructions);
      if (terminatedAccumulator != null) {
        return terminatedAccumulator;
      }
      instructions.set(i, instruction);
    }
    throw fail();
  }
  
  private Integer getTerminatedAccumulatorOrNullOnLoop(List<Instruction> instructions) {
    State state = new State();
    while (state.instructionIndex < instructions.size()) {
      if (state.executedInstructions.contains(state.instructionIndex)) {
        return null;
      }
      state.executedInstructions.add(state.instructionIndex);
      instructions.get(state.instructionIndex).execute(state);
    }
    return state.accumulator;
  }
  
  private static class State {
    private final Set<Integer> executedInstructions = new HashSet<>();
    private int accumulator;
    private int instructionIndex;
  }
  
  private static class Instruction {
    private final InstructionType instructionType;
    private final int argument;
    
    private Instruction(String spec) {
      this.instructionType = instructionTypes.get(spec.substring(0, 3));
      this.argument = Integer.parseInt(spec.substring(4));
    }
    
    private Instruction(InstructionType instructionType, int argument) {
      this.instructionType = instructionType;
      this.argument = argument;
    }
    
    private void execute(State state) {
      instructionType.execute(state, argument);
    }
  }
  
  private enum InstructionType implements HasId<String> {
    Acc {
      @Override
      protected void execute(State state, int argument) {
        state.accumulator += argument;
        ++state.instructionIndex;
      }
    },
    Jmp{
      @Override
      protected void execute(State state, int argument) {
        state.instructionIndex += argument;
      }
    },
    Nop {
      @Override
      protected void execute(State state, int argument) {
        ++state.instructionIndex;
      }
    };
    
    protected abstract void execute(State state, int argument);
    
    @Override
    public String getId() {
      return name().toLowerCase();
    }
  }
}
