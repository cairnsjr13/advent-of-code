package com.cairns.rich.aoc._2016;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.cairns.rich.aoc.QuietCapable;

class AssemBunny extends QuietCapable {
  static Supplier<Boolean> never = () -> false;
  static Consumer<ArrayBlockingQueue<Integer>> ignore = (output) -> quietly(() -> {
    while (output.take() != Integer.MIN_VALUE) ;
  });
  private static Map<String, Action> lookup = Arrays.stream(Action.values()).collect(Collectors.toMap(
      (action) -> action.name().toLowerCase(),
      Function.identity()
  ));
  
  static int execute(List<Inst> insts, Map<Character, Integer> inits) {
    return execute(insts, inits, never, ignore);
  }
  
  static int execute(
      List<Inst> insts,
      Map<Character, Integer> inits,
      Supplier<Boolean> killer,
      Consumer<ArrayBlockingQueue<Integer>> transmitConsumer
  ) {
    State state = new State(insts);
    inits.forEach((reg, value) -> state.registers[reg - 'a'] = value);
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
  
  static class Inst {
    private Action action;
    private final List<Object> args;
    
    Inst(String spec) {
      String[] parts = spec.split(" ");
      this.action = lookup.get(parts[0]);
      this.args = new ArrayList<>();
      for (int i = 1; i < parts.length; ++i) {
        args.add(getArg(parts[i]));
      }
    }
    
    Inst(Inst copy) {
      this.action = copy.action;
      this.args = copy.args;
    }
    
    private static Object getArg(String part) {
      char ch = part.charAt(0);
      if (('a' <= ch) && (ch <= 'd')) {
        return Character.valueOf(ch);
      }
      return Integer.valueOf(Integer.parseInt(part));
    }
  }
  
  private static BiConsumer<State, List<Object>> incDec(int delta) {
    return (state, args) -> {
      if (args.get(0) instanceof Character) {
        state.registers[state.getRegisterIndex(args.get(0))] += delta;
      }
      ++state.instructionIndex;
    };
  }
  
  private enum Action {
    Cpy((state, args) -> {
      if (args.get(1) instanceof Character) {
        state.registers[state.getRegisterIndex(args.get(1))] = state.getValue(args.get(0));
      }
      ++state.instructionIndex;
    }),
    Inc(incDec(1)),
    Dec(incDec(-1)),
    Jnz((state, args) -> {
      state.instructionIndex += (state.getValue(args.get(0)) != 0)
          ? state.getValue(args.get(1))
          : 1;
    }),
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
    Out((state, args) -> {
      quietly(() -> state.output.put(state.getValue(args.get(0))));
      ++state.instructionIndex;
    });
    
    private final BiConsumer<State, List<Object>> logic;
    
    private Action(BiConsumer<State, List<Object>> logic) {
      this.logic = logic;
    }
  }
  
  private static class State {
    private final int[] registers = new int[4];
    private int instructionIndex = 0;
    private final List<Inst> instructions;
    public final ArrayBlockingQueue<Integer> output = new ArrayBlockingQueue<>(1);
    
    private State(List<Inst> instructions) {
      this.instructions = instructions;
    }
    
    private int getValue(Object valOrRegister) {
      return (valOrRegister instanceof Integer)
          ? ((Integer) valOrRegister).intValue()
          : registers[((Character) valOrRegister).charValue() - 'a'];
    }
    
    private int getRegisterIndex(Object arg) {
      return ((Character) arg).charValue() - 'a';
    }
  }
}
