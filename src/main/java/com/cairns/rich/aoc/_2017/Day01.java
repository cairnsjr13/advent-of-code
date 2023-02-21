package com.cairns.rich.aoc._2017;

import java.util.function.Function;
import java.util.function.ToIntFunction;

class Day01 extends Base2017 {
  @Override
  protected void run() {
    String fullInput = fullLoader.sl();

    System.out.println("PART 1");
    ToIntFunction<String> part1 = (input) -> getSumOfMatches(input, (i) -> i + 1);
    System.out.println(part1.applyAsInt("1122"));
    System.out.println(part1.applyAsInt("1111"));
    System.out.println(part1.applyAsInt("1234"));
    System.out.println(part1.applyAsInt("91212129"));
    System.out.println(part1.applyAsInt(fullInput));

    System.out.println();

    System.out.println("PART 2");
    ToIntFunction<String> part2 = (input) -> getSumOfMatches(input, (i) -> i + input.length() / 2);
    System.out.println(part2.applyAsInt("1212"));
    System.out.println(part2.applyAsInt("1221"));
    System.out.println(part2.applyAsInt("123425"));
    System.out.println(part2.applyAsInt("123123"));
    System.out.println(part2.applyAsInt("12131415"));
    System.out.println(part2.applyAsInt(fullInput));
  }

  private int getSumOfMatches(String input, Function<Integer, Integer> nextIFn) {
    int sum = 0;
    for (int i = 0; i < input.length(); ++i) {
      char ch = input.charAt(i);
      int nextI = nextIFn.apply(i);
      if (ch == input.charAt(nextI % input.length())) {
        sum += (ch - '0');
      }
    }
    return sum;
  }
}
