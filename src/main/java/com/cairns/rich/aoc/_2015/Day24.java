package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;

/**
 * Santa's sleigh needs balancing.  There are different compartments that all need to weigh the same.
 * We also need to break ties based on the number of packages in the passenger compartment and quantum entanglement.
 *
 * TODO: all of the package sizes are prime numbers.... i'm guessing there is something we can do there to make this faster
 */
class Day24 extends Base2015 {
  /**
   * Returns the smallest quantum entanglement of the passenger compartment of an ideal 3 compartment loading scheme.
   */
  @Override
  protected Object part1(Loader loader) {
    return getBestQe(loader, 3, this::getBestQe3);
  }

  /**
   * Returns the smallest quantum entanglement of the passenger compartment of an ideal 4 compartment loading scheme.
   */
  @Override
  protected Object part2(Loader loader) {
    return getBestQe(loader, 4, (pkgs, correctSize) -> getBestQe(
        pkgs,
        correctSize,
        (mask) -> -1 != getBestQe3(buildGroupFromMask(pkgs, ~mask), correctSize)
    ));
  }

  /**
   * Finds the best quantum entanglement with the given number of groups.  First finds the size of each group by finding
   * the total sum and dividing it evenly by the number of groups.  Then passes these to the given compute method.
   */
  private Object getBestQe(Loader loader, int numGroups, BiFunction<List<Integer>, Integer, Long> compute) {
    List<Integer> pkgs = loader.ml(Integer::parseInt);
    pkgs.sort(Comparator.<Integer>naturalOrder().reversed());
    int totalSum = pkgs.stream().mapToInt(Integer::intValue).sum();
    return compute.apply(pkgs, totalSum / numGroups);
  }

  /**
   * Computes the best quantum engtanglement for 3 groups with the given target size.
   */
  private long getBestQe3(List<Integer> pkgs, int correctSize) {
    return getBestQe(pkgs, correctSize, (mask) -> canEvenlyPartition(pkgs, correctSize, 2, ~mask));
  }

  /**
   * Iterates through the possible passenger size compartments and returns the best one from the smallest possible size.
   */
  private long getBestQe(List<Integer> pkgs, int correctSize, IntPredicate isPartitionableCorrect) {
    for (int passengerSize = 1; passengerSize < pkgs.size(); ++passengerSize) {
      List<Integer> correctGroupMasks = computeMasksForCorrectPassengerGroupWithSize(pkgs, correctSize, passengerSize);
      if (!correctGroupMasks.isEmpty()) {
        List<Integer> partitionableCorrectMasks = new ArrayList<>();
        for (int mask : correctGroupMasks) {
          if (isPartitionableCorrect.test(mask)) {
            partitionableCorrectMasks.add(mask);
          }
        }
        if (!partitionableCorrectMasks.isEmpty()) {
          return partitionableCorrectMasks.stream().mapToLong((mask) -> computeProduct(pkgs, mask)).min().getAsLong();
        }
      }
    }
    throw fail();
  }

  /**
   * Returns true if the remaining packages can be divided into the appropriate number of groups with correct weights.
   * Does so recursively until there are only two groups and they are equivalent.
   */
  private boolean canEvenlyPartition(List<Integer> pkgs, int correctSize, int numGroups, int leftOverMask) {
    List<Integer> pkgsLeft = buildGroupFromMask(pkgs, leftOverMask);
    for (int partitionMask = 0; partitionMask < 1 << pkgsLeft.size(); ++partitionMask) {
      if (correctSize == groupSize(pkgsLeft, partitionMask)) {
        return (numGroups == 2)
            || canEvenlyPartition(pkgsLeft, correctSize, numGroups - 1, ~leftOverMask);
      }
    }
    return false;
  }

  /**
   * Creates a new {@link List} including the correct packages based on the mask.
   */
  private List<Integer> buildGroupFromMask(List<Integer> pkgs, int mask) {
    List<Integer> filteredPkgs = new ArrayList<>();
    for (int i = 0; i < pkgs.size(); ++i) {
      if (isPositionSet(mask, i)) {
        filteredPkgs.add(pkgs.get(i));
      }
    }
    return filteredPkgs;
  }

  /**
   * A pretty inefficient way of computing all of the masks that have the desired count and size.
   *
   * TODO: There may be a way to get the proper bit counts quicker than iterating through all options.
   * TODO: There may be a way to short circuit on weights we know are too big
   */
  private List<Integer> computeMasksForCorrectPassengerGroupWithSize(
      List<Integer> pkgs,
      int correctSize,
      int passengerSize
  ) {
    List<Integer> correctGroupMasks = new ArrayList<>();
    for (int mask = 0; mask < 1 << pkgs.size(); ++mask) {
      if (Integer.bitCount(mask) == passengerSize) {
        if (groupSize(pkgs, mask) == correctSize) {
          correctGroupMasks.add(mask);
        }
      }
    }
    return correctGroupMasks;
  }

  /**
   * Returns the sum of the weights of all pkgs present in the given mask.
   */
  private int groupSize(List<Integer> pkgs, int mask) {
    int total = 0;
    for (int i = 0; i < pkgs.size(); ++i) {
      if (isPositionSet(mask, i)) {
        total += pkgs.get(i);
      }
    }
    return total;
  }

  /**
   * Returns the product from multiplying all pkgs present in the given mask.
   */
  private long computeProduct(List<Integer> pkgs, int mask) {
    long product = 1;
    for (int i = 0; i < pkgs.size(); ++i) {
      if (isPositionSet(mask, i)) {
        product *= pkgs.get(i);
      }
    }
    return product;
  }

  /**
   * Helper method to determine if the mask has the given position set.
   */
  private boolean isPositionSet(int mask, int position) {
    return 0 != (mask & (1 << position));
  }
}
