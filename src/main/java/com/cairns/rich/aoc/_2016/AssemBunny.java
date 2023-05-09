package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Base.HasId;
import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.QuietCapable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simulator of the assem bunny computer used in 2016.
 * TODO: Note: Because of the {@link Action#Tgl} action, {@link #execute(List, Map)} and
 *       {@link #execute(List, Map, Supplier, Consumer)} canNOT be run in parallel using the same instructions.
 */
class AssemBunny extends QuietCapable {
  static Supplier<Boolean> never = () -> false;
  static Consumer<ArrayBlockingQueue<Integer>> ignore = (output) -> quietly(() -> {
    while (output.take() != Integer.MIN_VALUE) ;
  });

  /**
   * Returns the value in register a after running the given instructions with the initial register values.
   * Will never preemptively kill and will ignore all output.
   */
  static int execute(List<Inst> insts, Map<Character, Integer> inits) {
    return execute(insts, inits, never, ignore);
  }

  /**
   * Returns the value in register a after running the given instructions with the initial register values.
   * Will terminate after fully executing an instruction if the given killer supplier ever returns false.
   * The given transmitConsumer will be provided the output queue from the running machine after it is started.
   */
  static int execute(
      List<Inst> insts,
      Map<Character, Integer> inits,
      Supplier<Boolean> killer,
      Consumer<ArrayBlockingQueue<Integer>> transmitConsumer
  ) {
    State state = new State(insts, inits);
    Thread t = new Thread(() -> {
      while (state.instructionIndex < insts.size()) {
        Inst inst = state.instructions.get(state.instructionIndex);
        inst.action.logic.accept(state, inst.args);
        if (killer.get()) {
          break;
        }
      }
      quietly(() -> state.output.put(Integer.MIN_VALUE));
    });
    t.start();
    transmitConsumer.accept(state.output);
    return state.registers[0];
  }

  /**
   * Represents an instruction runnable by our computer.
   */
  static class Inst {
    private static Map<String, Action> lookup = EnumUtils.getLookup(Action.class);

    private Action action;
    private final List<Object> args;

    Inst(String spec) {
      String[] parts = spec.split(" ");
      this.action = lookup.get(parts[0]);
      this.args = IntStream.range(1, parts.length).mapToObj((i) -> getArg(parts[i])).collect(Collectors.toList());
    }

    /**
     * Shallow copy constructor.  Only necessary because {@link Action#Tgl} modifies an {@link Inst#action}.
     */
    Inst(Inst copy) {
      this.action = copy.action;
      this.args = copy.args;
    }

    /**
     * Returns the appropriate arg Object based on the type of code reference.
     * Integers will be returned in a boxed manner while [abcd] will be returned as boxed Characters.
     */
    private static Object getArg(String part) {
      char ch = part.charAt(0);
      if (('a' <= ch) && (ch <= 'd')) {
        return Character.valueOf(ch);
      }
      return Integer.valueOf(Integer.parseInt(part));
    }
  }

  private enum Action implements HasId<String> {
    /**
     * Copies the value of the first argument into the register of the second argument.
     * Will be a no op if the second argument is not a register.
     * Increments the instruction pointer by one.
     */
    Cpy((state, args) -> {
      if (args.get(1) instanceof Character) {
        state.registers[state.getRegisterIndex(args.get(1))] = state.getValue(args.get(0));
      }
      ++state.instructionIndex;
    }),
    /**
     * Increments the register by 1.  Will be a no op if the reference is not a register.
     * Increments the instruction pointer by one.
     */
    Inc(incDec(1)),
    /**
     * Decrements the register by 1.  Will be a no op if the reference is not a register.
     * Increments the instruction pointer by one.
     */
    Dec(incDec(-1)),
    /**
     * If the first argument is non-zero, the instruction pointer will be incremented (can be negative) by the second argument.
     * Otherwise, increments the instruction pointer by one.
     */
    Jnz((state, args) -> {
      state.instructionIndex += (state.getValue(args.get(0)) != 0)
          ? state.getValue(args.get(1))
          : 1;
    }),
    /**
     * Toggles the instruction found the number of instructions away by the first argument.
     * All one arg actions become {@link #Inc}, except {@link #Inc} which becomes {@link #Dec}.
     * All two arg actions become {@link #Jnz}, except {@link #Jnz} which becomes {@link #Cpy}.
     * Nothing happens if a toggle is attempted out of bounds.
     * Nothing happens on execution of the instruction if a toggle results in an invalid action (ie cpy 1 2).
     * An instruction that is toggled by itself is NOT rerun.
     * Increments the instruction pointer by one.
     */
    Tgl((state, args) -> {
      int modifyIndex = state.instructionIndex + state.getValue(args.get(0));
      if ((0 <= modifyIndex) && (modifyIndex < state.instructions.size())) {
        Inst modifyInst = state.instructions.get(modifyIndex);
        if (modifyInst.args.size() == 1) {
          modifyInst.action = (modifyInst.action == Action.Inc)
              ? Action.Dec
              : Action.Inc;
        }
        else {
          modifyInst.action = (modifyInst.action == Action.Jnz)
              ? Action.Cpy
              : Action.Jnz;
        }
      }
      ++state.instructionIndex;
    }),
    /**
     * Transmits the argument to the output queue.  Increments the instruction pointer by one.
     */
    Out((state, args) -> {
      quietly(() -> state.output.put(state.getValue(args.get(0))));
      ++state.instructionIndex;
    });

    private final BiConsumer<State, List<Object>> logic;

    private Action(BiConsumer<State, List<Object>> logic) {
      this.logic = logic;
    }

    @Override
    public String getId() {
      return name().toLowerCase();
    }

    /**
     * Helper method to generate an action for inc/dec actions based on the given delta.
     */
    private static BiConsumer<State, List<Object>> incDec(int delta) {
      return (state, args) -> {
        if (args.get(0) instanceof Character) {
          state.registers[state.getRegisterIndex(args.get(0))] += delta;
        }
        ++state.instructionIndex;
      };
    }
  }

  /**
   * Container class holding all of the relevant state of a computer running.
   */
  private static class State {
    private final int[] registers = new int[4];
    private int instructionIndex = 0;
    private final List<Inst> instructions;
    private final ArrayBlockingQueue<Integer> output = new ArrayBlockingQueue<>(1);

    private State(List<Inst> instructions, Map<Character, Integer> inits) {
      this.instructions = instructions;
      inits.forEach((reg, value) -> registers[getRegisterIndex(reg)] = value);
    }

    /**
     * Returns the usable value given a code reference.  Can be either an integer (+/-) or a register ref (abcd).
     */
    private int getValue(Object valOrRegister) {
      return (valOrRegister instanceof Integer)
          ? ((Integer) valOrRegister).intValue()
          : registers[getRegisterIndex(valOrRegister)];
    }

    /**
     * Returns the 0 based index of the register referenced by the given argument.  Should be a {@link Character}.
     */
    private int getRegisterIndex(Object arg) {
      return ((Character) arg).charValue() - 'a';
    }
  }
}
