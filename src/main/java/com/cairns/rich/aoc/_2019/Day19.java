package com.cairns.rich.aoc._2019;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

import org.apache.commons.lang3.tuple.Pair;

import com.cairns.rich.aoc._2019.IntCode.State;
import com.google.common.collect.Range;

class Day19 extends Base2019 {
  private AtomicInteger test = new AtomicInteger();
  
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    IntBinaryOperator isAffected = (x, y) -> {
      State state = IntCode.run(program);
      state.programInput.put(x);
      state.programInput.put(y);
      long affected = state.programOutput.take();
      state.blockUntilHalt();
      test.incrementAndGet();
      return (int) affected;
    };
    
    System.out.println(countNumAffected(isAffected, 50));
    System.out.println(findSquare(isAffected, 100));
  }
  
  private int countNumAffected(IntBinaryOperator isAffected, int maxReach) {
    int numAffected = 0;
    Range<Integer> above = null;
    for (int y = 0; y < maxReach; ++y) {
      above = getXRangeForY(isAffected, y, above);
      numAffected += (Math.min(maxReach, above.upperEndpoint()) - Math.min(maxReach, above.lowerEndpoint()));
    }
    return numAffected;
  }
  
  private int findSquare(IntBinaryOperator isAffected, int size) {
    LinkedList<Pair<Integer, Range<Integer>>> yAndXRanges = new LinkedList<>();
    Range<Integer> above = null;
    for (int y = 0; y < size; ++y) {
      above = getXRangeForY(isAffected, y, above);
      yAndXRanges.add(Pair.of(y, above));
    }
    while (true) {
      Pair<Integer, Range<Integer>> min = yAndXRanges.removeFirst();
      Pair<Integer, Range<Integer>> max = yAndXRanges.getLast();
      if (max.getRight().isConnected(min.getRight())) {
        Range<Integer> intersection = min.getRight().intersection(max.getRight());
        if (intersection.upperEndpoint() - intersection.lowerEndpoint() == size) {
          return intersection.lowerEndpoint() * 10_000 + min.getLeft().intValue();
        }
      }
      int y = max.getLeft() + 1;
      yAndXRanges.add(Pair.of(y, getXRangeForY(isAffected, y, max.getRight())));
    }
  }
  
  private Range<Integer> getXRangeForY(IntBinaryOperator isAffected, int y, Range<Integer> above) {
    if (y <= 2) {
      return Range.closedOpen(y, (y == 0) ? 1 : y);
    }
    else if (y == 3) {
      return Range.closedOpen(4, 5);
    }
    int minInc = above.lowerEndpoint() + ((isAffected.applyAsInt(above.lowerEndpoint() + 1, y) == 1) ? 1 : 2);
    int maxExc = above.upperEndpoint() + ((isAffected.applyAsInt(above.upperEndpoint() + 1, y) == 1) ? 2 : 1);
    return Range.closedOpen(minInc, maxExc);
  }
}
