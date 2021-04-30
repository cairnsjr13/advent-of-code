package com.cairns.rich.aoc._2020;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

class Day15 extends Base2020 {
  @Override
  protected void run() {
    int[] inputs = { 6, 4, 12, 1, 20, 0, 16 };
    
    System.out.println(findValueAfterTurns(2020, inputs));
    System.out.println(findValueAfterTurns(30_000_000, inputs));
  }
  
  private int findValueAfterTurns(int numTurns, int[] inputs) {
    State state = new State();
    IntStream.of(inputs).forEach(state::ingest);
    while (state.ingested < numTurns) {
      state.ingest(state.nextNumber);
    }
    return state.lastNumber;
  }
  
  private static class State {
    private int ingested = 0;
    private int lastNumber;
    private int nextNumber;
    private final Map<Integer, Integer> numbersByIndex = new HashMap<>();
    
    private void ingest(int number) {
      ++ingested;
      lastNumber = number;
      if (numbersByIndex.containsKey(number)) {
        nextNumber = ingested - numbersByIndex.get(number);
      }
      else {
        nextNumber = 0;
      }
      numbersByIndex.put(number, ingested);
    }
  }
}
