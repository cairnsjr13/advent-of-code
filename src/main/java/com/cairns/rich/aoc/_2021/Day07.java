package com.cairns.rich.aoc._2021;

import com.google.common.collect.TreeMultiset;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

class Day07 extends Base2021 {
  @Override
  protected void run() {
    TreeMultiset<Integer> positionCounts = TreeMultiset.create();
    fullLoader.sl(",", Integer::parseInt).forEach(positionCounts::add);
    int min = positionCounts.firstEntry().getElement();
    int max = positionCounts.lastEntry().getElement();
    ToIntFunction<IntUnaryOperator> getMinFuel = (distToFuel) -> IntStream.rangeClosed(min, max).map(
        (mp) -> positionCounts.elementSet().stream()
          .mapToInt((p) -> distToFuel.applyAsInt(Math.abs(p - mp)) * positionCounts.count(p))
          .sum()
    ).min().getAsInt();

    System.out.println(getMinFuel.applyAsInt((d) -> d));
    System.out.println(getMinFuel.applyAsInt((d) -> (1 + d) * d / 2));
  }
}
