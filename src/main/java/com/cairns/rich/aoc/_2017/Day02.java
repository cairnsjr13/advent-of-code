package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * We have a spreadsheet that we need to inspect.  Each part asks us to sum up characteristics of each row.
 */
class Day02 extends Base2017 {
  /**
   * Computes the checksum of the spreadsheet by summing up each rows difference between its max and min.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeResult(loader, this::computeChecksumResultForRow);
  }

  /**
   * Computes the sum of each row's {@link #computeEvenlyDivisibleResultForRow(List)}.
   */
  @Override
  protected Object part2(Loader loader) {
    return computeResult(loader, this::computeEvenlyDivisibleResultForRow);
  }

  /**
   * Computes the sum of each row's given result characteristic.
   */
  private int computeResult(Loader loader, ToIntFunction<List<Integer>> toResult) {
    return loader.ml(this::parse).stream().mapToInt(toResult).sum();
  }

  /**
   * Computes a checksum characterstic of the given row.  This is the difference between the max and min.
   */
  private int computeChecksumResultForRow(List<Integer> row) {
    return getMax(row, Function.identity()) - getMin(row, Function.identity());
  }

  /**
   * Finds two numbers in the row such that the first is evenly divisible by the second. Returns that result.
   */
  private int computeEvenlyDivisibleResultForRow(List<Integer> row) {
    for (int left : row) {
      for (int right : row) {
        if ((left != right) && (left % right == 0)) {
          return left / right;
        }
      }
    }
    throw fail(row);
  }

  /**
   * Helper method to parse a line of input.  Each line is a list of numbers separated by at least one space.
   */
  private List<Integer> parse(String spec) {
    return Arrays.stream(spec.split(" +")).map(Integer::parseInt).collect(Collectors.toList());
  }
}
