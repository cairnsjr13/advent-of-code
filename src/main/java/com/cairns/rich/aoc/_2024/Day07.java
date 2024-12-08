package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Elephants have stolen our operators and we need to find which equations are possible with different operators.
 */
class Day07 extends Base2024 {
  private static final BinaryOperator<BigInteger> ADD = BigInteger::add;
  private static final BinaryOperator<BigInteger> MULT = BigInteger::multiply;
  private static final BinaryOperator<BigInteger> CONCAT = (l, r) -> new BigInteger(l.toString() + r.toString());

  /**
   * Only addition and multiplication are allowed.
   */
  @Override
  protected Object part1(Loader loader) {
    return sumAllPossibleEquations(loader, List.of(ADD, MULT));
  }

  /**
   * Addition, multiplication and concatenation are allowed.
   * Concatenation is left-to-right string concatenation: 123 || 45 = 12345.
   */
  @Override
  protected Object part2(Loader loader) throws Throwable {
    return sumAllPossibleEquations(loader, List.of(ADD, MULT, CONCAT));
  }

  /**
   * Helper method to sum the answer of all equations who are possible with the given operations.
   */
  private BigInteger sumAllPossibleEquations(Loader loader, List<BinaryOperator<BigInteger>> ops) {
    return loader.ml(Equation::new).stream()
        .filter((eq) -> isPossible(eq, ops, eq.operands.get(0), 1))
        .map((eq) -> eq.answer)
        .reduce(BigInteger.ZERO, BigInteger::add);
  }

  /**
   * Recursive method to test if an equation can have its answer reached
   * from the given index by assigning any of the given operations.
   *
   * Checking if soFar is less than or equal to the answer is an optimization to short circuit for speed.
   */
  private boolean isPossible(Equation eq, List<BinaryOperator<BigInteger>> ops, BigInteger soFar, int index) {
    if (index == eq.operands.size()) {
      return soFar.equals(eq.answer);
    }
    return (soFar.compareTo(eq.answer) <= 0)
        && ops.stream().anyMatch((op) -> isPossible(eq, ops, op.apply(soFar, eq.operands.get(index)), index + 1));
  }

  /**
   * Container class describing an equation with its answer and operands.
   */
  private static class Equation {
    private final BigInteger answer;
    private final List<BigInteger> operands;

    private Equation(String line) {
      String[] pieces = line.split(": ");
      this.answer = BigInteger.valueOf(Long.parseLong(pieces[0]));
      this.operands = Arrays.stream(pieces[1].split(" +"))
          .mapToLong(Long::parseLong)
          .mapToObj(BigInteger::valueOf)
          .collect(Collectors.toList());
    }
  }
}
