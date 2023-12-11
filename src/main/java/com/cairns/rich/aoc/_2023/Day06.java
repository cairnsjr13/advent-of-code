package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * We need to win some boat races and optimize the amount of time charging vs sailing.
 */
class Day06 extends Base2023 {
  private static final MathContext mc = MathContext.DECIMAL128;
  private static final BigDecimal TWO = BigDecimal.valueOf(2);
  private static final BigDecimal FOUR = BigDecimal.valueOf(4);

  /**
   * Returns the product of the number of wins possible for each race in the spec.
   */
  @Override
  protected Object part1(Loader loader) {
    List<long[]> lenAndRecords = loader.ml((l) -> Arrays.stream(l.split(" +")).skip(1).mapToLong(Long::parseLong).toArray());
    return IntStream.range(0, lenAndRecords.get(0).length).mapToLong((i) -> {
      long length = lenAndRecords.get(0)[i];
      long record = lenAndRecords.get(1)[i];
      return numWins(length, record);
    }).reduce(Math::multiplyExact).getAsLong();
  }

  /**
   * Returns the number of wins possible if the spec is interpreted as a single race.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Long> ins = loader.ml((l) -> Long.parseLong(Arrays.stream(l.split(" +")).skip(1).collect(Collectors.joining())));
    long length = ins.get(0);
    long record = ins.get(1);
    return numWins(length, record);
  }

  /**
   * Computes the number of wins possible for a race of given length with given record.
   * This is simply the longest amount of time we can charge and win minus the shortest
   * amount of time we can charge and win plus one (due to the fence post problem).
   */
  private long numWins(long length, long record) {
    long lowest = findExtremePoint(length, record, -1, RoundingMode.CEILING);
    long highest = findExtremePoint(length, record, +1, RoundingMode.FLOOR);
    return highest - lowest + 1;
  }

  /**
   * In order to determine the amount of charging needed to beat the given record, we need to solve the inequality:
   *    record < charge * (length - charge), which is equivalent to record < length * charge - charge ^ 2
   * We can think of this as an optimization problem where the interesting point is where they are actually equal.
   * Rearranging the equation leads us to a quadratic formula which we can solve using {@link BigDecimal} math.
   *    charge ^ 2 - length * charge + record = 0, can be solved with:
   *    charge = (-(-length) +/- sqrt((-length)^2 - (4 * 1 * record))) / (2 * 1)
   *    charge = (length +/- sqrt(length^2 - 4*record)) / 2
   * It is important to note that when the quadratic is exactly equal, we have to reduce the answer because a tie doesnt count.
   */
  private long findExtremePoint(long length, long record, long plusMinus, RoundingMode round) {
    BigDecimal len = BigDecimal.valueOf(length);
    BigDecimal determinant = len.pow(2).subtract(BigDecimal.valueOf(record).multiply(FOUR));
    BigDecimal extremePoint = len.add(BigDecimal.valueOf(plusMinus).multiply(determinant.sqrt(mc))).divide(TWO);
    long charge = extremePoint.setScale(0, round).longValue();
    if (record == charge * (length - charge)) { // a tie does not count as a win
      charge -= plusMinus;
    }
    return charge;
  }
}
