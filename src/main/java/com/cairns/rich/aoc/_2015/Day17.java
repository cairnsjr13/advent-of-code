package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day17 extends Base2015 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Integer> buckets = loader.ml(Integer::parseInt);
    return computeCorrectCapacity(buckets, 150).size();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Integer> buckets = loader.ml(Integer::parseInt);
    Set<Integer> correctCapacities = computeCorrectCapacity(buckets, 150);
    int[] numBucketsCount = new int[buckets.size()];
    correctCapacities.forEach((corr) -> ++numBucketsCount[Integer.bitCount(corr)]);
    return Arrays.stream(numBucketsCount).filter((i) -> i > 0).findFirst().getAsInt();
  }

  private Set<Integer> computeCorrectCapacity(List<Integer> buckets, int target) {
    Set<Integer> correctCapacityBuckets = new HashSet<>();
    int numCombos = 1 << buckets.size();
    for (int i = 0; i < numCombos; ++i) {
      if (target == computeCap(buckets, i)) {
        correctCapacityBuckets.add(i);
      }
    }
    return correctCapacityBuckets;
  }

  private int computeCap(List<Integer> buckets, int spec) {
    int capacity = 0;
    for (int i = 0; i < buckets.size(); ++i) {
      if (0 != (spec & (1 << i))) {
        capacity += buckets.get(i);
      }
    }
    return capacity;
  }
}
