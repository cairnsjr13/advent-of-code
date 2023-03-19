package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2019.IntCode.State;
import com.google.common.collect.Range;
import java.util.LinkedList;
import java.util.List;
import java.util.function.LongBinaryOperator;
import org.apache.commons.lang3.tuple.Pair;

class Day19 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    LongBinaryOperator isAffected = generateIsAffected(loader);
    long maxReach = 50;
    long numAffected = 0;
    Range<Long> above = null;
    for (long y = 0; y < maxReach; ++y) {
      above = getXRangeForY(isAffected, y, above);
      numAffected += (Math.min(maxReach, above.upperEndpoint()) - Math.min(maxReach, above.lowerEndpoint()));
    }
    return numAffected;
  }

  @Override
  protected Object part2(Loader2 loader) {
    LongBinaryOperator isAffected = generateIsAffected(loader);
    int size = 100;
    LinkedList<Pair<Long, Range<Long>>> yAndXRanges = new LinkedList<>();
    Range<Long> above = null;
    for (long y = 0; y < size; ++y) {
      above = getXRangeForY(isAffected, y, above);
      yAndXRanges.add(Pair.of(y, above));
    }
    while (true) {
      Pair<Long, Range<Long>> min = yAndXRanges.removeFirst();
      Pair<Long, Range<Long>> max = yAndXRanges.getLast();
      if (max.getRight().isConnected(min.getRight())) {
        Range<Long> intersection = min.getRight().intersection(max.getRight());
        if (intersection.upperEndpoint() - intersection.lowerEndpoint() == size) {
          return intersection.lowerEndpoint() * 10_000 + min.getLeft();
        }
      }
      long y = max.getLeft() + 1;
      yAndXRanges.add(Pair.of(y, getXRangeForY(isAffected, y, max.getRight())));
    }
  }

  private LongBinaryOperator generateIsAffected(Loader2 loader) {
    List<Long> program = IntCode.parseProgram(loader);
    return (x, y) -> {
      State state = IntCode.run(program);
      state.programInput.put(x);
      state.programInput.put(y);
      long affected = state.programOutput.take();
      state.blockUntilHalt();
      return affected;
    };
  }

  private Range<Long> getXRangeForY(LongBinaryOperator isAffected, long y, Range<Long> above) {
    if (y <= 2) {
      return Range.closedOpen(y, (y == 0) ? 1L : y);
    }
    else if (y == 3) {
      return Range.closedOpen(4L, 5L);
    }
    long minInc = above.lowerEndpoint() + ((isAffected.applyAsLong(above.lowerEndpoint() + 1, y) == 1) ? 1L : 2L);
    long maxExc = above.upperEndpoint() + ((isAffected.applyAsLong(above.upperEndpoint() + 1, y) == 1) ? 2L : 1L);
    return Range.closedOpen(minInc, maxExc);
  }
}
