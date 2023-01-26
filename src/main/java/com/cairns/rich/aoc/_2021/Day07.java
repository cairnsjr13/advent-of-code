package com.cairns.rich.aoc._2021;

import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.TreeMultiset;

public class Day07 extends Base2021 {
  @Override
  protected void run() {
    TreeMultiset<Integer> positionCounts = TreeMultiset.create();
    fullLoader.sl(",", Integer::parseInt).forEach(positionCounts::add);
    int min = positionCounts.firstEntry().getElement();
    int max = positionCounts.lastEntry().getElement();
    List<Integer> meetPoints = IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList());
    ToIntFunction<IntUnaryOperator> getMinFuel = (distToFuel) -> meetPoints.stream().mapToInt(
        (mp) -> positionCounts.elementSet().stream().mapToInt(
            (p) -> distToFuel.applyAsInt(Math.abs(p - mp)) * positionCounts.count(p)
        ).sum()
    ).min().getAsInt();
    
    System.out.println(getMinFuel.applyAsInt((d) -> d));
    System.out.println(getMinFuel.applyAsInt((d) -> (1 + d) * d / 2));
  }
}
