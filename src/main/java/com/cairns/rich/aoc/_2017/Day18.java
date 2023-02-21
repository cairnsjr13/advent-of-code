package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc._2017.TabletCode.State;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.ToIntFunction;

class Day18 extends Base2017 {
  @Override
  protected void run() throws Throwable {
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

  private long getTwoProgramBased(List<ToIntFunction<State>> insts) throws InterruptedException, ExecutionException {
    Lock rcvWaitingLock = new ReentrantLock();
    State state0 = new State(rcvWaitingLock, Map.of('p', 0));
    State state1 = new State(rcvWaitingLock, Map.of('p', 1));
    state0.otherState = state1;
    state1.otherState = state0;

    Future<?> future0 = startDaemon(() -> TabletCode.executeState(insts, state0));
    Future<?> future1 = startDaemon(() -> TabletCode.executeState(insts, state1));
    future0.get();
    future1.get();

    return state1.instCalls.count("snd");
  }
}
