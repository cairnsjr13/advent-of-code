package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class Day23 extends Base2015 {
  private static final Map<String, InstructionType> instsByName = EnumUtils.getLookup(InstructionType.class);

  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<Consumer<State>> instructions = loader.ml(this::parseInstruction);
    result.part1(runAndGetRegisterB(instructions, 0, 0));
    result.part2(runAndGetRegisterB(instructions, 1, 0));
  }

  private int runAndGetRegisterB(List<Consumer<State>> instructions, int initA, int initB) {
    State state = new State(initA, initB);
    while (state.currentInstruction < instructions.size()) {
      instructions.get(state.currentInstruction).accept(state);
    }
    return state.registers[1];
  }

  private Consumer<State> parseInstruction(String spec) {
    String[] parts = spec.split(",? ");
    String inst = parts[0];
    if ("jmp".equals(inst)) {
      int offset = Integer.parseInt(parts[1]);
      return (state) -> InstructionType.Jmp.execute(state, -1, offset);
    }
    else if (inst.startsWith("j")) {
      int register = parts[1].charAt(0) - 'a';
      int offset = Integer.parseInt(parts[2]);
      return (state) -> instsByName.get(inst).execute(state, register, offset);
    }
    else {
      int register = parts[1].charAt(0) - 'a';
      return (state) -> instsByName.get(inst).execute(state, register, Integer.MIN_VALUE);
    }
  }

  private static class State {
    private int[] registers;
    private int currentInstruction;

    private State(int a, int b) {
      this.registers = new int[] { a, b };
    }
  }

  private enum InstructionType implements HasId<String> {
    Hlf {
      @Override
      protected void execute(State state, int register, int offset) {
        state.registers[register] /= 2;
        ++state.currentInstruction;
      }
    },
    Tpl {
      @Override
      protected void execute(State state, int register, int offset) {
        state.registers[register] *= 3;
        ++state.currentInstruction;
      }
    },
    Inc {
      @Override
      protected void execute(State state, int register, int offset) {
        ++state.registers[register];
        ++state.currentInstruction;
      }
    },
    Jmp {
      @Override
      protected void execute(State state, int register, int offset) {
        state.currentInstruction += offset;
      }
    },
    Jie {
      @Override
      protected void execute(State state, int register, int offset) {
        state.currentInstruction += (state.registers[register] % 2 == 0) ? offset : 1;
      }
    },
    Jio {
      @Override
      protected void execute(State state, int register, int offset) {
        state.currentInstruction += (state.registers[register] == 1) ? offset : 1;
      }
    };

    @Override
    public String getId() {
      return name().toLowerCase();
    }

    protected abstract void execute(State state, int register, int offset);
  }
}
