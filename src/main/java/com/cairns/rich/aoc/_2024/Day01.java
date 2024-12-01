package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntBiFunction;
import java.util.stream.IntStream;

/**
 * We need to compare lists of location ids.  Simple list iteration will be efficient.
 */
class Day01 extends Base2024 {
  /**
   * We can compute the total distance by sorting each list and pairing them off.
   * We need to make sure we use absolute distance correctly.
   */
  @Override
  protected Object part1(Loader loader) {
    return handlePart(loader, (lefts, rights) -> {
      lefts.sort(Comparator.naturalOrder());
      rights.sort(Comparator.naturalOrder());
      return IntStream.range(0, lefts.size()).map((i) -> Math.abs(lefts.get(i) - rights.get(i))).sum();
    });
  }

  /**
   * We can compute the similarity of the lists by multiplying each number in the left list by
   * the total number of times it appears in the right list.  We can use a multiset to count.
   */
  @Override
  protected Object part2(Loader loader) throws Throwable {
    return handlePart(loader, (lefts, rights) -> {
      Multiset<Integer> rightCounts = HashMultiset.create(rights);
      return lefts.stream().mapToInt(Integer::intValue).map((left) -> left * rightCounts.count(left)).sum();
    });
  }

  /**
   * Helper function to parse out both lists and get the answer by applying the given handler function.
   */
  private int handlePart(
      Loader loader,
      ToIntBiFunction<List<Integer>, List<Integer>> handler
  ) {
    List<Integer> lefts = new ArrayList<>();
    List<Integer> rights = new ArrayList<>();
    for (String[] linePieces : loader.ml((line) -> line.split(" +"))) {
      lefts.add(Integer.parseInt(linePieces[0]));
      rights.add(Integer.parseInt(linePieces[1]));
    }
    return handler.applyAsInt(lefts, rights);
  }
}
