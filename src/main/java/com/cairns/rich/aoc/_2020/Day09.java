package com.cairns.rich.aoc._2020;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class Day09 extends Base2020 {
  @Override
  protected void run() {
    List<Long> nums = fullLoader.ml(Long::parseLong);
    long firstIncorrect = findFirstIncorrect(nums);
    System.out.println(firstIncorrect);
    System.out.println(sumSmallestAndLargestInExploit(firstIncorrect, nums));
  }

  private long sumSmallestAndLargestInExploit(long target, List<Long> nums) {
    List<Long> sumTo = new ArrayList<>();
    Map<Long, Integer> lookup = new HashMap<>();
    long total = 0;
    sumTo.add(total);
    for (long num : nums) {
      total += num;
      sumTo.add(total);
      lookup.put(total, sumTo.size() - 1);
    }
    for (int i = 0; i < sumTo.size(); ++i) {
      long bottomEnd = sumTo.get(i);
      long search = target + bottomEnd;
      if (lookup.containsKey(search)) {
        int topIndex = lookup.get(search);
        if (i < topIndex) {
          List<Long> subList = nums.subList(i + 1, topIndex + 1);
          return getMin(subList, Function.identity())
               + getMax(subList, Function.identity());
        }
      }
    }
    throw fail();
  }

  private long findFirstIncorrect(List<Long> nums) {
    Set<Long> window = new HashSet<>(nums.subList(0, 25));
    for (int i = window.size(); i < nums.size(); ++i) {
      long candidate = nums.get(i);
      if (!canAddTo(candidate, window)) {
        return candidate;
      }
      window.remove(nums.get(i - window.size()));
      window.add(candidate);
    }
    throw fail();
  }

  private boolean canAddTo(long target, Set<Long> window) {
    return window.stream().anyMatch((left) -> {
      long search = target - left;
      return (search != left) && window.contains(search);
    });
  }
}
