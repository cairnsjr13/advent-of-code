package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Instruction memory has become "corrupted" and we need to parse through
 * an instruction string to find valid multiply and conditional operators.
 */
public class Day03 extends Base2024 {
  private static final Pattern pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");

  /**
   * All we need to find is properly formatted mul(x,y) operators and add up their results.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeSumOfMuls(loader.ml());
  }

  /**
   * We need to consider do() and don't() operators in the string as well.
   * The state is assumed to be in "do()" state at the beginning.  Any mul()
   * operators between don't() and do() states will be ignored and become noops.
   */
  @Override
  protected Object part2(Loader loader) {
    return computeSumOfMuls(tokenizeWithConditionals(loader.ml()));
  }

  /**
   * Helper function to simplify adding up the results of valid mul() operators.
   */
  private long computeSumOfMuls(List<String> tokens) {
    return tokens.stream().mapToLong(this::getResult).sum();
  }

  /**
   * By using the {@link #pattern} regex, we can find valid mul() operators to compute.
   * The given line token will have ALL of its mul() operators evaluated and summed.
   * Operands to the mul() operator must have [1,3] digits and no invalid chars.
   */
  private long getResult(String line) {
    long sum = 0;
    for (Matcher matcher = pattern.matcher(line); matcher.find(); ) {
      sum += (lnum(matcher, 1) * lnum(matcher, 2));
    }
    return sum;
  }

  /**
   * The don't() and do() conditionals allow mul() operators to be ignored as noops in ranges of
   * the input.  We can do this by alternating substring search between don't() and do() until we
   * reach the end of the input.  Only tokens between do()...don't() conditionals will be returned.
   * There is an implicit do() at the beginning of the input and an implicit don't() at the end.
   */
  private List<String> tokenizeWithConditionals(List<String> lines) {
    String input = lines.stream().collect(Collectors.joining(" "));
    String dontStr = "don't()";
    List<String> tokens = new ArrayList<>();
    for (String conditional : Iterables.cycle(dontStr, "do()")) {
      int indexOf = input.indexOf(conditional);
      if (indexOf == -1) {
        if (dontStr.equals(conditional)) {
          tokens.add(input);
        }
        return tokens;
      }
      else if (dontStr.equals(conditional)) {
        tokens.add(input.substring(0, indexOf));
      }
      input = input.substring(indexOf + conditional.length());
    }
    throw fail("Impossible");
  }
}
