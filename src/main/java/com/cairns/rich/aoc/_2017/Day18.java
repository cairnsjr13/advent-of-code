package com.cairns.rich.aoc._2017;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import com.cairns.rich.aoc._2017.TabletCode.State;

class Day18 extends Base2017 {
  @Override
  protected void run() {
    List<ToIntFunction<State>> insts = fullLoader.ml(TabletCode::parse);
    System.out.println(getSoundBased(insts));
    System.out.println(getTwoProgramBased(insts));
  }
  
  private long getSoundBased(List<ToIntFunction<State>> insts) {
    State state = new State(true);
    state.otherState = state;
    TabletCode.executeState(insts, state);
    return state.recv.pollLast();
  }
  
  private long getTwoProgramBased(List<ToIntFunction<State>> insts) {
    Function<State, Thread> startThread = (state) -> {
      Thread t = new Thread(() -> TabletCode.executeState(insts, state));
      t.setDaemon(true);
      t.start();
      return t;
    };
    State state0 = new State(Map.of('p', 0));
    State state1 = new State(Map.of('p', 1));
    state0.otherState = state1;
    state1.otherState = state0;
    Thread t0 = startThread.apply(state0);
    Thread t1 = startThread.apply(state1);
    while ((t0.getState() != Thread.State.WAITING) || (t1.getState() != Thread.State.WAITING)) {
      Thread.onSpinWait();
    }
    t0.interrupt();
    t1.interrupt();
    quietly(() -> t0.join());
    quietly(() -> t1.join());
    return state1.instCalls.count("snd");
  }
}
