package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Day10 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    String input = loader.sl();
    result.part1(getLengthOfAnswer(input, 40));
    result.part2(getLengthOfAnswer(input, 50));
  }

  private int getLengthOfAnswer(String input, int numItrs) {
    for (int i = 0; i < numItrs; ++i) {
      input = process(input);
    }
    return input.length();
  }

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
