package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  private static Map<Character, char[]> buildButtons(String... specs) {
    return Arrays.stream(specs).collect(Collectors.toMap(
        (spec) -> spec.charAt(0),
        (spec) -> spec.substring(2).toCharArray()
    ));
  }

  @Override
  protected Object part1(Loader loader) {
    return getCode(imagineButts, loader.ml(), '5');
  }

  @Override
  protected Object part2(Loader loader) {
    return getCode(actualButts, loader.ml(), '5');
  }

  private String getCode(Map<Character, char[]> buttons, List<String> specs, char current) {
    StringBuilder code = new StringBuilder();
    for (String spec : specs) {
      for (char move : spec.toCharArray()) {
        current = buttons.get(current)[moves.indexOf(move)];
      }
      code.append(current);
    }
    return code.toString();
  }
}
