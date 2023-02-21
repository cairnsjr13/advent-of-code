package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day06 extends Base2017 {
  @Override
  protected void run() {
    doParts(fullLoader.ml(State::new));
  }

  private void doParts(List<State> input) {
    State state = input.get(0);
    Map<State, Integer> seenStates = new HashMap<>();
    while (!seenStates.containsKey(state)) {
      seenStates.put(state, seenStates.size());
      state = new State(state.banks);
      int index = getIndexOfMax(state);
      int blocks = state.banks[index];
      state.banks[index] = 0;
      for (int i = 0; i < blocks; ++i) {
        index = (index + 1) % state.banks.length;
        ++state.banks[index];
      }
    }
    System.out.println(seenStates.size());
    System.out.println(seenStates.size() - seenStates.get(state));
  }

  private int getIndexOfMax(State state) {
    return getMax(
        IntStream.range(0, state.banks.length).boxed().collect(Collectors.toList()),
        (i) -> state.banks[i]
    );
  }

  private static class State {
    private final int[] banks;

    private State(String spec) {
      this.banks = Arrays.stream(spec.split(" +")).mapToInt(Integer::parseInt).toArray();
    }

    private State(int[] banks) {
      this.banks = Arrays.copyOf(banks, banks.length);
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
