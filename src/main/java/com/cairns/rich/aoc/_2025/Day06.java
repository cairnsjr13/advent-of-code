package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

/**
 * Cephalopods are doing their math homework.  Their problems are structured in horizontal values grouped
 * in columns and vertical values grouped in chunks.  Addition and multiplication are the only operators.
 *
 */
class Day06 extends Base2025 {
  private static final Map<Character, LongBinaryOperator> operations = Map.of(
      '+', Math::addExact,
      '*', Math::multiplyExact
  );

  /**
   * Numbers are horizontally written (for example 79, 123, and 456) but grouped vertically:
   *   79
   *  123
   *  456
   * The operator is at the bottom of the vertical group in the left most column.
   * Returns the sum of all problem totals.
   */
  @Override
  protected Object part1(Loader loader) {
    List<String[]> rows = loader.ml((line) -> line.trim().split(" +"));
    String[] operators = safeGet(rows, -1);
    long grandTotal = 0;
    for (int i = 0; i < operators.length; ++i) {
      int col = i;
      grandTotal += computeTotal(
          operators[col].charAt(0),
          rows.subList(0, rows.size() - 1).stream().map((row) -> row[col]),
          Long::parseLong
      );
    }
    return grandTotal;
  }

  /**
   * Numbers are vertically written.  For example 79, 123, and 456 are written like this:
   *   14
   *  725
   *  936
   * The operator is at the bottom of the vertical group in the left most column.
   * Returns the sum of all problem totals.
   */
  @Override
  protected Object part2(Loader loader) {
    List<String> rows = loader.ml();
    long grandTotal = 0;
    for (int col = rows.get(0).length() - 1; col >= 0; ) {
      List<Long> values = getVerticalValues(rows, col);
      char operator = safeGet(rows, -1).charAt(col - values.size() + 1);
      grandTotal += computeTotal(operator, values.stream(), Long::longValue);
      col -= (values.size() + 1);
    }
    return grandTotal;
  }

  /**
   * Helper function to compute the total of the given values when applying the given operator.
   */
  private <T> long computeTotal(char operator, Stream<T> valuesStream, ToLongFunction<T> toLong) {
    return valuesStream.mapToLong(toLong).reduce(operations.get(operator)).getAsLong();
  }

  /**
   * Reads all vertically specified values right-to-left starting from the first column.
   * Will stop when a completely blank column is written.
   * Callers can progress the column based on the number of values returned (and the blank column).
   */
  private List<Long> getVerticalValues(List<String> lines, int firstCol) {
    List<Long> values = new ArrayList<>();
    for (int col = firstCol; col >= 0; --col) {
      long value = 0;
      boolean sawValue = false;
      for (int row = 0; row < lines.size() - 1; ++row) {
        char ch = lines.get(row).charAt(col);
        if (Character.isDigit(ch)) {
          value = (value * 10) + (ch - '0');
          sawValue = true;
        }
      }
      if (!sawValue) {
        break;
      }
      values.add(value);
    }
    return values;
  }
}
