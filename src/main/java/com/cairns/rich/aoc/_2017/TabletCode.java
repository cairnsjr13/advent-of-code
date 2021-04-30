package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.ToIntFunction;

import com.cairns.rich.aoc.SafeAccessor;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

class TabletCode extends SafeAccessor {
  static ToIntFunction<State> parse(String spec) {
    String[] parts = spec.split(" ");
    ToIntFunction<State> action = parseAction(parts);
    return (state) -> {
      state.instCalls.add(parts[0]);
      return action.applyAsInt(state);
    };
  }
  
  static void executeState(List<ToIntFunction<State>> insts, State state) {
    while ((0 <= state.instNum) && (state.instNum < insts.size())) {
      state.instNum += insts.get(state.instNum).applyAsInt(state);
    }
  }
  
  private static ToIntFunction<State> parseAction(String[] parts) {
    switch (parts[0]) {
      case "snd" : return send(parts[1]);
      case "set" : return simpleInc((state) -> state.set(parts[1].charAt(0), state.getValue(parts[2])));
      case "add" : return op(parts, Math::addExact);
      case "sub" : return op(parts, Math::subtractExact);
      case "mul" : return op(parts, Math::multiplyExact);
      case "mod" : return op(parts, (l, r) -> l % r);
      case "rcv" : return receive(parts[1]);
      case "jgz" : return (state) -> (state.getValue(parts[1]) > 0) ? (int) state.getValue(parts[2]) : 1;
      case "jnz" : return (state) -> (state.getValue(parts[1]) != 0) ? (int) state.getValue(parts[2]) : 1;
      default : throw new RuntimeException(Arrays.toString(parts));
    }
  }
  
  private static ToIntFunction<State> send(String register) {
    return simpleInc((state) -> {
      synchronized (State.class) {
        state.otherState.recv.offer(state.getValue(register));
        State.class.notify();
      }
    });
  }
  
  private static ToIntFunction<State> receive(String register) {
    return (state) -> {
      if (state.stopOnRcv) {
        return Integer.MIN_VALUE;
      }
      synchronized (State.class) {
        while (state.recv.isEmpty()) {
          try {
            State.class.wait();
          }
          catch (InterruptedException e) {
            return Integer.MIN_VALUE;
          }
        }
        state.set(register.charAt(0), state.recv.remove());
      }
      return 1;
    };
  }
  
  private static ToIntFunction<State> simpleInc(Consumer<State> incAfter) {
    return (state) -> {
      incAfter.accept(state);
      return 1;
    };
  }
  
  private static ToIntFunction<State> op(String[] parts, LongBinaryOperator op) {
    return simpleInc((state) -> state.set(
        parts[1].charAt(0),
        op.applyAsLong(state.getValue(parts[1]), state.getValue(parts[2]))
    ));
  }
  
  static class State {
    private final boolean stopOnRcv;
    private final long[] registers = new long[26];
    private int instNum = 0;
    State otherState;
    final Multiset<String> instCalls = HashMultiset.create();
    final BlockingDeque<Long> recv = new LinkedBlockingDeque<>();
    
    State(boolean stopOnRcv) {
      this.stopOnRcv = stopOnRcv;
    }
    
    State(Map<Character, Integer> inits) {
      this(false);
      inits.forEach((register, init) -> registers[register - 'a'] = init);
    }
    
    long registerValue(char register) {
      return registers[register - 'a'];
    }
    
    private void set(char register, long value) {
      registers[register - 'a'] = value;
    }
    
    private long getValue(String instPart) {
      return (Character.isAlphabetic(instPart.charAt(0)))
          ? registerValue(instPart.charAt(0))
          : Long.parseLong(instPart);
    }
  }
}
