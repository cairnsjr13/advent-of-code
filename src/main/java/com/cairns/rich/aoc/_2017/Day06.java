package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Day06 extends Base2017 {
  @Override
  protected void run() {
    doParts(fullLoader.ml(State::new));
    doParts(fullLoader.ml(State::new));
  }
  
  private void doParts(List<State> input) {
    State state = input.get(0);
    System.out.println(state);
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
    System.out.println("\t" + seenStates.size());
    System.out.println("\t" + (seenStates.size() - seenStates.get(state)));
  }
  
  private int getIndexOfMax(State state) {
    int maxI = 0;
    for (int i = 1; i < state.banks.length; ++i) {
      if (state.banks[maxI] < state.banks[i]) {
        maxI = i;
      }
    }
    return maxI;
  }
  
  private static class State {
    private final int[] banks;
    
    private State(String spec) {
      this.banks = Arrays.stream(spec.split(" +")).mapToInt(Integer::parseInt).toArray();
    }
    
    private State(int[] banks) {
      this.banks = new int[banks.length];
      System.arraycopy(banks, 0, this.banks, 0, banks.length);
    }
    
    @Override
    public boolean equals(Object other) {
      return Arrays.equals(banks, ((State) other).banks);
    }
    
    @Override
    public int hashCode() {
      return Arrays.hashCode(banks);
    }
    
    @Override
    public String toString() {
      return Arrays.toString(banks);
    }
  }
}
