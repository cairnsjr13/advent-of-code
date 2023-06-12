package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.function.ToIntFunction;

/**
 * We need to pass a captcha to get started.  This captcha involves
 * a circular list of digits and counting matches that we find.
 */
class Day01 extends Base2017 {
  /**
   * Returns the captcha sum by comparing a digit to the immediate next.
   */
  @Override
  protected Object part1(Loader loader) {
    return getSumOfMatches(loader, (input) -> 1);
  }

  /**
   * Returns the captcha sum by comparing a digit to the one directly across (half way) the circular list.
   */
  @Override
  protected Object part2(Loader loader) {
    return getSumOfMatches(loader, (input) -> input.length() / 2);
  }

  /**
   * Uses a two pointer approach to compare characters for a single loop and sums matches up.
   */
  private int getSumOfMatches(Loader loader, ToIntFunction<String> initialCompareI) {
    int sum = 0;
    String input = loader.sl();
    for (int ii = 0, ci = initialCompareI.applyAsInt(input); ii < input.length(); ++ii, ++ci) {
      char ch = input.charAt(ii);
      if (ch == safeCharAt(input, ci)) {
        sum += (ch - '0');
      }
    }
    return sum;
  }
}
