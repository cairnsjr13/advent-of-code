package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A memory reallocation routine is getting stuck in an infinite loop.  We need to
 * figure out when this happens and various characteristics about those loops.
 */
class Day06 extends Base2017 {
  /**
   * Returns the total number of redistribution cycles that must run before we see a repeat state.
   */
  @Override
  protected Object part1(Loader loader) {
    return runCycles(loader, (seenStates, state) -> seenStates.size());
  }

  /**
   * Returns the number of redistribution cycles that form the infinite loop.
   */
  @Override
  protected Object part2(Loader loader) {
    return runCycles(loader, (seenStates, state) -> seenStates.size() - seenStates.get(state));
  }

  /**
   * Runs redistribution cycles until we find a repeate state.  Once a repeat state is found,
   * the given toAnswer function is used to compute the return.  The Map passed in can be used
   * to find out how many steps it took to get to a particular state.  The state passed in is
   * the first repeated state.
   *
   * A redistribution cycle is run by finding the largest bank, removing all blocks
   * from it, and reallocating them evenly to all of the banks in a circular order.
   */
  private int runCycles(Loader loader, BiFunction<Map<State, Integer>, State, Integer> toAnswer) {
    State state = new State(loader.sl());
    Map<State, Integer> seenStates = new HashMap<>();
    while (!seenStates.containsKey(state)) {
      seenStates.put(state, seenStates.size());
      state = new State(state.banks);
      int index = state.getIndexOfMax();
      int blocks = state.banks[index];
      state.banks[index] = 0;
      for (int i = 0; i < blocks; ++i) {
        index = (index + 1) % state.banks.length;
        ++state.banks[index];
      }
    }
    return toAnswer.apply(seenStates, state);
  }

  /**
   * Rather than deal with the syntactic annoyance of incrementing elements in a list,
   * we can use this class to keep track of registers and handle equality checks naturally.
   */
  private static class State {
    private final int[] banks;

    private State(String spec) {
      this.banks = Arrays.stream(spec.split(" +")).mapToInt(Integer::parseInt).toArray();
    }

    private State(int[] banks) {
      this.banks = Arrays.copyOf(banks, banks.length);
    }

    /**
     * Finds the index of the banks largest value.
     */
    private int getIndexOfMax() {
      int maxIndex = 0;
      for (int i = 1; i < banks.length; ++i) {
        if (banks[i] > banks[maxIndex]) {
          maxIndex = i;
        }
      }
      return maxIndex;
    }

    @Override
    public boolean equals(Object other) {
      return Arrays.equals(banks, ((State) other).banks);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(banks);
    }
  }
}
