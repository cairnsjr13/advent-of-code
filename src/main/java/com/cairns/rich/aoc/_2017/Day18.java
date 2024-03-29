package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2017.TabletCode.State;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.ToIntFunction;

/**
 * We have another assembly language simulation problem.  We can use {@link TabletCode} to do this.
 */
class Day18 extends Base2017 {
  /**
   * Returns the first non-zero value passed to the rcv instruction.
   */
  @Override
  protected Object part1(Loader loader) {
    List<ToIntFunction<State>> insts = loader.ml(TabletCode::parse);
    State state = new State(true);
    state.otherState = state;
    TabletCode.executeState(insts, state);
    return state.recv.pollLast();
  }

  /**
   * Returns the number of time program 1 (0 and 1) sends (snd) a value after both have terminated.
   */
  @Override
  protected Object part2(Loader loader) throws InterruptedException, ExecutionException {
    List<ToIntFunction<State>> insts = loader.ml(TabletCode::parse);
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
