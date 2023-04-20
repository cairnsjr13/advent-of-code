package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * We need to get into the bathroom.  Quickly.  Various keypads and instructions will result in the code.
 */
class Day02 extends Base2016 {
  private static final String moves = "UDLR";

  // 123
  // 456
  // 789
  private static final Map<Character, char[]> imagineButts = buildButtons(
      "1-1412",
      "2-2513",
      "3-3623",
      "4-1745",
      "5-2846",
      "6-3956",
      "7-4778",
      "8-5879",
      "9-6989"
  );

  //   1
  //  234
  // 56789
  //  ABC
  //   D
  private static final Map<Character, char[]> actualButts = buildButtons(
      "1-1311",
      "2-2623",
      "3-1724",
      "4-4834",
      "5-5556",
      "6-2A57",
      "7-3B68",
      "8-4C79",
      "9-9989",
      "A-6AAB",
      "B-7DAC",
      "C-8CBC",
      "D-BDDD"
  );

  /**
   * Helper method to convert the above button spec strings into a lookup map.
   * Specs should be of the form "c-udlr" where c is the phone character and the following
   * four characters are the direction when moving Up, Down, Left, and Right.
   */
  private static Map<Character, char[]> buildButtons(String... specs) {
    return Arrays.stream(specs).collect(Collectors.toMap(
        (spec) -> spec.charAt(0),
        (spec) -> spec.substring(2).toCharArray()
    ));
  }

  /**
   * Computes the bathroom code assuming a standard rectangle grid.
   */
  @Override
  protected Object part1(Loader loader) {
    return getCode(loader, imagineButts);
  }

  /**
   * Computes the bathroom code assuming a modified diamond grid.
   */
  @Override
  protected Object part2(Loader loader) {
    return getCode(loader, actualButts);
  }

  /**
   * Uses the given button lookup to compute the code described in the input.
   * The char[]s in the mapping correspond to the direction we need to move,
   * based on the index of the char move in the {@link #moves}.
   */
  private String getCode(Loader loader, Map<Character, char[]> buttons) {
    List<char[]> specs = loader.ml(String::toCharArray);
    char current = '5';
    StringBuilder code = new StringBuilder();
    for (char[] spec : specs) {
      for (char move : spec) {
        current = buttons.get(current)[moves.indexOf(move)];
      }
      code.append(current);
    }
    return code.toString();
  }
}
