package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Jane Marie got a computer that we need to emulate.  Only two registers and six instructions.
 *
 * @implNote If you look at the full input program, this is the hailstone number algorithm (Collatz conjecture)
 */
class Day23 extends Base2015 {
  private static final ConfigToken<Integer> returnRegister = ConfigToken.of("returnRegister", (s) -> s.charAt(0) - 'a');
  private static final Map<String, InstructionType> instsByName = EnumUtils.getLookup(InstructionType.class);

  /**
   * Returns the configured register's value after executing the input program.
   */
  @Override
  protected Object part1(Loader loader) {
    return runAndGetRegister(loader, 0, 0);
  }

  /**
   * Returns the configured register's value after executing the input program with an initial a value of 1.
   */
  @Override
  protected Object part2(Loader loader) {
    return runAndGetRegister(loader, 1, 0);
  }

  /**
   * Returns the configured registers value after running the input program with the given initial registers.
   */
  private int runAndGetRegister(Loader loader, int initA, int initB) {
    List<Consumer<State>> instructions = loader.ml(this::parseInstruction);
    State state = new State(initA, initB);
    while (state.currentInstruction < instructions.size()) {
      instructions.get(state.currentInstruction).accept(state);
    }
    return state.registers[loader.getConfig(returnRegister)];
  }

  /**
   * Parser method that returns an instruction action to mutate state based on the type.
   */
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

  /**
   * Container class describing the values of the registers and the instruction we need to run.
   */
  private static class State {
    private int[] registers;
    private int currentInstruction;

    private State(int a, int b) {
      this.registers = new int[] { a, b };
    }
  }

  /**
   * The computer offers these instructions.  Each one takes a register and an offset to perform its action.
   */
  private enum InstructionType implements HasId<String> {
    /**
     * Sets the given register index to half of its current value and proceeds to the next instruction.
     */
    Hlf {
      @Override
      protected void execute(State state, int register, int offset) {
        state.registers[register] /= 2;
        ++state.currentInstruction;
      }
    },
    /**
     * Sets the given register index to 3x its current value and proceeds to the next instruction.
     */
    Tpl {
      @Override
      protected void execute(State state, int register, int offset) {
        state.registers[register] *= 3;
        ++state.currentInstruction;
      }
    },
    /**
     * Increments the value in the given register index and proceeds to the next instruction.
     */
    Inc {
      @Override
      protected void execute(State state, int register, int offset) {
        ++state.registers[register];
        ++state.currentInstruction;
      }
    },
    /**
     * Proceeds to the instruction offset number of instructions away, ignoring the register.
     */
    Jmp {
      @Override
      protected void execute(State state, int register, int offset) {
        state.currentInstruction += offset;
      }
    },
    /**
     * If the value in the register index is even, jumps to the instruction offset number of instructions away.
     */
    Jie {
      @Override
      protected void execute(State state, int register, int offset) {
        state.currentInstruction += (state.registers[register] % 2 == 0) ? offset : 1;
      }
    },
    /**
     * If the value in the register index is odd, jumps to the instruction offset number of instrcutions away.
     */
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

    /**
     * Executes this instruction's logic on the given state based on the given register and offset.
     */
    protected abstract void execute(State state, int register, int offset);
  }
}
