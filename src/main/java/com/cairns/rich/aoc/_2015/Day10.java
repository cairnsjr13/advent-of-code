package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The elves want to play look and say.
 * Each round is the previous round but sequences collapsed and described.  For example:
 * 1 becomes 11 ("one 1") which becomes 21 ("two 1s") which becomes 1211 ("one 2 one 1")
 */
class Day10 extends Base2015 {
  private static final ConfigToken<Integer> numItrsConfig = ConfigToken.of("numItrs", Integer::parseInt);

  /**
   * The length of the sequence after the configured number of iterations.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    return getLengthOfAnswer(loader);
  }

  /**
   * The length of the sequence after the configured number of iterations.
   *
   * @implNote - because of the config system, this ends up being the same as {@link #part1(Loader)}.
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    return getLengthOfAnswer(loader);
  }

  /**
   * Computes the length of the text after the configured number of iterations.
   * This is done by iteratively supplying the output from one round back into the input of the next round.
   */
  private int getLengthOfAnswer(Loader loader) {
    String input = loader.sl();
    int numItrs = loader.getConfig(numItrsConfig);
    for (int i = 0; i < numItrs; ++i) {
      input = process(input);
    }
    return input.length();
  }

  /**
   * Performs one look and say iteration on the given output.
   */
  private String process(String input) {
    List<Integer> nums = new ArrayList<>();
    for (int i = 0; i < input.length();) {
      int count = count(input, i);
      nums.add(count);
      nums.add(input.charAt(i) - '0');
      i += count;
    }
    return nums.stream().map(Object::toString).collect(Collectors.joining());
  }

  /**
   * Counts the number of consecutive numbers starting at the given index.
   */
  private int count(String input, int i) {
    char ch = input.charAt(i);
    int j = i;
    for (; j < input.length(); ++j) {
      if (ch != input.charAt(j)) {
        break;
      }
    }
    return j - i;
  }
}
