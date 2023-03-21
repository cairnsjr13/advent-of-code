package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.HashMap;
import java.util.Map;

class Day15 extends Base2020 {
  @Override
  protected Object part1(Loader loader) {
    return findValueAfterTurns(loader, 2020);
  }

  @Override
  protected Object part2(Loader loader) {
    return findValueAfterTurns(loader, 30_000_000);
  }

  private int findValueAfterTurns(Loader loader, int numTurns) {
    State state = new State();
    loader.sl(",", Integer::parseInt).forEach(state::ingest);
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
