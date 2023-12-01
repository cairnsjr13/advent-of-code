package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * We need to calibrate our device.  By looking at the reported frequency changes we can get answers.
 */
class Day01 extends Base2018 {
  /**
   * Returns the final frequency after all of the input changes occur.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Integer::parseInt).stream().mapToInt(Integer::intValue).sum();
  }

  /**
   * Returns the first frequency that is repeated when applying the input changes.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Integer> changes = loader.ml(Integer::parseInt);
    Set<Integer> seen = new HashSet<>();
    int current = 0;
    seen.add(current);
    while (true) {
      for (int change : changes) {
        current += change;
        if (!seen.add(current)) {
          return current;
        }
      }
    }
  }
}
