package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * We have a ton of eggnog and we need to put it in containers.
 */
class Day17 extends Base2015 {
  private static final ConfigToken<Integer> numLiters = ConfigToken.of("numLiters", Integer::parseInt);

  /**
   * Calculates the number of different combinations of containers that perfectly contain the number of liters configured.
   */
  @Override
  protected Object part1(Loader loader) {
    List<Integer> buckets = loader.ml(Integer::parseInt);
    return computeCorrectCapacity(buckets, loader.getConfig(numLiters)).size();
  }

  /**
   * Computes the number of different combinations that can perfectly contain the
   * number of liters configured using the minimum possible number of containers.
   *
   * For example: If there are combinations using 2, 3, 2, 5, and 2: the minimum number would be 2, of which there are 3.
   *              So 3 would be the answer.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Integer> buckets = loader.ml(Integer::parseInt);
    Set<Integer> correctCapacities = computeCorrectCapacity(buckets, loader.getConfig(numLiters));
    int[] numBucketsCount = new int[buckets.size()];
    correctCapacities.forEach((corr) -> ++numBucketsCount[Integer.bitCount(corr)]);
    return Arrays.stream(numBucketsCount).filter((i) -> i > 0).findFirst().getAsInt();
  }

  /**
   * Iterates through every possible combination of containers and checks if each one
   * holds the exact target amount of eggnog.  We can use an integer as a bit set and
   * increment it to represent a new combination (1 means use it, 0 means dont).  The
   * number of lshifts determine the index of bucket in the input list.
   */
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

  /**
   * Computes the amount of eggnog that the given combination spec holds.
   * A 1 in the the ith position indicates the bucket at index i is used (and thus included).
   */
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
