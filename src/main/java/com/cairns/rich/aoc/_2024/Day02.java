package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * We need to count the number of safe reports by ensuring all levels are in the same direction and not too steep.
 */
public class Day02 extends Base2024 {
  /**
   * Applying the basic rules is all that is necessary for part 1.  No discarding.
   */
  @Override
  protected Object part1(Loader loader) {
    return countSafeReports(loader, false);
  }

  /**
   * In part 2, we are allowed to discard at most one level.
   */
  @Override
  protected Object part2(Loader loader) {
    return countSafeReports(loader, true);
  }

  /**
   * Helper method to compute the number of safe reports by filtering each with the given allowDiscard flag.
   */
  private long countSafeReports(Loader loader, boolean allowDiscard) {
    return loader.ml(this::parse).stream().filter((levels) -> isSafe(levels, allowDiscard)).count();
  }

  /**
   * Helper method to determine if a report is safe.  We can do this by any of the following:
   *   - checking from index 0 without discarding any.
   *   - if discarding is allowed:
   *     - checking from index 0 and discarding index 1
   *     - checking from index 1 after discarding index 0
   */
  private boolean isSafe(List<Integer> levels, boolean allowDiscard) {
    return isSafe(levels, allowDiscard, 0, 1)
        || (allowDiscard && (isSafe(levels, false, 0, 2) || isSafe(levels, false, 1, 2)));
  }

  /**
   * Helper method to simplify determining initial direction.
   */
  private boolean isSafe(List<Integer> levels, boolean allowDiscard, int prevIndex, int nextIndex) {
    return isSafe(levels, allowDiscard, prevIndex, nextIndex, levels.get(prevIndex) < levels.get(nextIndex));
  }

  /**
   * Recursive method to determine if a route is safe from a given point.  A route is considered safe
   * if all levels move in the same (+/-) direction and have [1,3] absolute magnitude change.  The
   * allowDiscard flag can be used to allow the check to discard up to one level from consideration.
   *
   * While the tail-recursion approach is a bit sloppy, the iterative approach was borderline unreadable.
   */
  private boolean isSafe(
      List<Integer> levels,
      boolean allowDiscard,
      int prevIndex,
      int nextIndex,
      boolean expectedDir
  ) {
    if (nextIndex >= levels.size()) {
      return true;
    }
    if (allowDiscard && isSafe(levels, false, prevIndex, nextIndex + 1, expectedDir)) {
      return true;
    }
    int previousValue = levels.get(prevIndex);
    int nextValue = levels.get(nextIndex);
    int diff = Math.abs(previousValue - nextValue);
    boolean thisDir = (previousValue < nextValue);
    return (1 <= diff) && (diff <= 3) && (expectedDir == thisDir)
        && isSafe(levels, allowDiscard, nextIndex, nextIndex + 1, expectedDir);
  }

  /**
   * Input parse method to convert a line of numbers in text form into a list of integers.
   */
  private List<Integer> parse(String line) {
    return Stream.of(line.split(" +")).map(Integer::parseInt).collect(Collectors.toList());
  }
}
