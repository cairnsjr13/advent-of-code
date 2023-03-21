package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

class Day18 extends Base2016 {
  private static final BiPredicate<List<Boolean>, Integer> isSet =
      (previousRow, col) -> (0 <= col) && (col < previousRow.size()) && previousRow.get(col);

  @Override
  protected Object part1(Loader loader) {
    return getSafeCount(40, loader.sl());
  }

  @Override
  protected Object part2(Loader loader) {
    return getSafeCount(400_000, loader.sl());
  }

  private long getSafeCount(int numRows, String init) {
    List<Boolean> previousRow = init.chars().mapToObj((ch) -> ch == '^').collect(Collectors.toList());
    List<Boolean> currentRow = new ArrayList<>(previousRow);
    long numSafe = previousRow.stream().filter((isTrap) -> !isTrap).count();
    for (int i = 1; i < numRows; ++i) {
      for (int col = 0; col < previousRow.size(); ++col) {
        boolean isTrap = isCurrentTrap(previousRow, col);
        currentRow.set(col, isTrap);
        if (!isTrap) {
          ++numSafe;
        }
      }
      List<Boolean> swap = previousRow;
      previousRow = currentRow;
      currentRow = swap;
    }
    return numSafe;
  }

  private boolean isCurrentTrap(List<Boolean> previousRow, int col) {
    boolean left = isSet.test(previousRow, col - 1);
    boolean middle = isSet.test(previousRow, col);
    boolean right = isSet.test(previousRow, col + 1);
    return (left && middle && !right)
         | (!left && middle && right)
         | (left && !middle && !right)
         | (!left && !middle && right);
  }
}
