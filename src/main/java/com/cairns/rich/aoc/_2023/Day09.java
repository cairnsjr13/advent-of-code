package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.commons.lang3.ArrayUtils;

/**
 * We need to predict future (and past) environmental readings for the oasis.  This can be done
 * by looking at differences-of-differences of the history we have and using them to project.
 */
class Day09 extends Base2023 {
  /**
   * Computes the sum of extrapolated next values.  (No transform required)
   */
  @Override
  protected Object part1(Loader loader) {
    return totalProjections(loader, (values) -> { });
  }

  /**
   * Computes the sum of extrapolated prev values.  (Simple by reversing history)
   */
  @Override
  protected Object part2(Loader loader) {
    return totalProjections(loader, ArrayUtils::reverse);
  }

  /**
   * Computes the sum of all extrapolated values after first applying the given transform.  We can do this
   * in place for each history record by seeing each loop reduces the number of examined readings by 1.
   * The unused spot in the array can be used to hold the values needed.  At the end, all we must do is
   * sum the last few numbers of the array (according  to how many loops it took to get to all zeros).
   */
  private long totalProjections(Loader loader, Consumer<long[]> transform) {
    return loader.ml((l) -> Arrays.stream(l.split(" +")).mapToLong(Long::parseLong).toArray())
        .stream().mapToLong((values) -> {
          transform.accept(values);
          int numLoops = 0;
          for (boolean seenNonZero = true; seenNonZero; ++numLoops) {
            seenNonZero = false;
            for (int i = 0; i + 1 < values.length - numLoops; ++i) {
              values[i] = values[i + 1] - values[i];
              seenNonZero |= (values[i] != 0);
            }
          }
          return Arrays.stream(values).skip(values.length - numLoops - 1).sum();
        }).sum();
  }
}
