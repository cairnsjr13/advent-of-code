package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

class Day07 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return getMinFuel(loader, IntUnaryOperator.identity());
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getMinFuel(loader, (d) -> (1 + d) * d / 2);
  }

  private int getMinFuel(Loader2 loader, IntUnaryOperator distToFuel) {
    Multiset<Integer> positionCounts = HashMultiset.create();
    loader.sl(",", Integer::parseInt).forEach(positionCounts::add);
    int min = getMin(positionCounts.elementSet(), Function.identity());
    int max = getMax(positionCounts.elementSet(), Function.identity());
    return IntStream.rangeClosed(min, max).map(
        (mp) -> positionCounts.elementSet().stream()
             .mapToInt((p) -> distToFuel.applyAsInt(Math.abs(p - mp)) * positionCounts.count(p))
             .sum()
    ).min().getAsInt();
  }
}
