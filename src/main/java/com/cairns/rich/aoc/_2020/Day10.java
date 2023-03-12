package com.cairns.rich.aoc._2020;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day10 extends Base2020 {
  @Override
  protected void run() {
    List<Integer> adapters = fullLoader.ml(Integer::parseInt);
    adapters.add(0);
    Collections.sort(adapters);
    adapters.add(adapters.get(adapters.size() - 1) + 3);
    System.out.println(computeVoltageJumpProduct(adapters));
    System.out.println(computeNumWalks(adapters));
  }

  private int computeVoltageJumpProduct(List<Integer> adapters) {
    int numOnes = 0;
    int numThrees = 0;
    for (int i = 0; i < adapters.size() - 1; ++i) {
      int current = adapters.get(i);
      int next = adapters.get(i + 1);
      if (next - current == 1) {
        ++numOnes;
      }
      else if (next - current == 3) {
        ++numThrees;
      }
    }
    return numOnes * numThrees;
  }

  private long computeNumWalks(List<Integer> adapters) {
    Map<Integer, Long> walkCache = new HashMap<>();
    List<Integer> jumps = IntStream.range(0, adapters.size() - 1)
        .map((i) -> adapters.get(i + 1) - adapters.get(i))
        .boxed().collect(Collectors.toList());
    long numWalks = 1;
    while (!jumps.isEmpty()) {
      int indexOfThree = jumps.indexOf(3);
      numWalks *= computeWalksInRange(walkCache, indexOfThree + 1);
      jumps = jumps.subList(indexOfThree + 1, jumps.size());
    }
    return numWalks;
  }

  private long computeWalksInRange(Map<Integer, Long> walkCache, int size) {
    if (size <= 2) {
      return (size > 0) ? 1 : 0;
    }
    if (!walkCache.containsKey(size)) {
      long walks = computeWalksInRange(walkCache, size - 1)
                 + computeWalksInRange(walkCache, size - 2)
                 + computeWalksInRange(walkCache, size - 3);
      walkCache.put(size, walks);
    }
    return walkCache.get(size);
  }
}
