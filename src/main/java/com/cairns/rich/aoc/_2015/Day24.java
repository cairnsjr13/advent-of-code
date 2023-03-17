package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

class Day24 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<Integer> pkgs = loader.ml(Integer::parseInt);
    int totalSum = pkgs.stream().mapToInt(Integer::intValue).sum();
    result.part1(getBestQe3(pkgs, totalSum / 3));
    result.part2(getBestQe4(pkgs, totalSum / 4));
  }

  private long getBestQe3(List<Integer> pkgs, int correctSize) {
    return getBestQe(pkgs, correctSize, (mask) -> canEvenlyPartition(pkgs, correctSize, 2, ~mask));
  }

  private long getBestQe4(List<Integer> pkgs, int correctSize) {
    return getBestQe(
        pkgs,
        correctSize,
        (mask) -> -1 != getBestQe3(buildGroupFromMask(pkgs, ~mask), correctSize)
    );
  }

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

  private List<Integer> buildGroupFromMask(List<Integer> pkgs, int mask) {
    List<Integer> filteredPkgs = new ArrayList<>();
    for (int i = 0; i < pkgs.size(); ++i) {
      if (0 != (mask & (1 << i))) {
        filteredPkgs.add(pkgs.get(i));
      }
    }
    return filteredPkgs;
  }

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

  private int groupSize(List<Integer> pkgs, int mask) {
    int total = 0;
    for (int i = 0; i < pkgs.size(); ++i) {
      if (0 != (mask & (1 << i))) {
        total += pkgs.get(i);
      }
    }
    return total;
  }

  private long computeProduct(List<Integer> pkgs, int mask) {
    long product = 1;
    for (int i = 0; i < pkgs.size(); ++i) {
      if (0 != (mask & (1 << i))) {
        product *= pkgs.get(i);
      }
    }
    return product;
  }
}
