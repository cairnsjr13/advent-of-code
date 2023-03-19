package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

class Day08 extends Base2020 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Instruction> instructions = loader.ml(Instruction::new);
    State state = new State();
    while (!state.executedInstructions.get(state.instructionIndex)) {
      state.execute(instructions);
    }
    return state.accumulator;
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Instruction> instructions = loader.ml(Instruction::new);
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
      OptionalInt terminatedAccumulator = getTerminatedAccumulatorOrNullOnLoop(instructions);
      if (terminatedAccumulator.isPresent()) {
        return terminatedAccumulator.getAsInt();
      }
      instructions.set(i, instruction);
    }
    throw fail();
  }

  private OptionalInt getTerminatedAccumulatorOrNullOnLoop(List<Instruction> instructions) {
    State state = new State();
    while (state.instructionIndex < instructions.size()) {
      if (state.executedInstructions.get(state.instructionIndex)) {
        return OptionalInt.empty();
      }
      state.execute(instructions);
    }
    return OptionalInt.of(state.accumulator);
  }

  private static class State {
    private final BitSet executedInstructions = new BitSet();
    private int accumulator;
    private int instructionIndex;

    private void execute(List<Instruction> instructions) {
      executedInstructions.set(instructionIndex);
      instructions.get(instructionIndex).execute(this);
    }
  }

  private static class Instruction {
    private static final Map<String, InstructionType> instructionTypes = EnumUtils.getLookup(InstructionType.class);

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
