package com.cairns.rich.aoc._2018;

import java.util.stream.IntStream;

class Day05 extends Base2018 {
  @Override
  protected void run() {
    String input = fullLoader.ml().get(0);
    System.out.println(lengthWhenIgnore(input, '#'));
    System.out.println(getMinLengthWhenIgnoringOneChar(input));
  }
  
  private int getMinLengthWhenIgnoringOneChar(String input) {
    return IntStream.range('a', 'z' + 1).map((ignore) -> lengthWhenIgnore(input, (char) ignore)).min().getAsInt();
  }
  
  private int lengthWhenIgnore(String input, char ignore) {
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < input.length(); ++i) {
      char nextCh = input.charAt(i);
      if (Character.toLowerCase(ignore) != Character.toLowerCase(nextCh)) {
        if (output.length() > 0) {
          char lastCh = output.charAt(output.length() - 1);
          if ((lastCh != nextCh) && (Character.toLowerCase(lastCh) == Character.toLowerCase(nextCh))) {
            output.deleteCharAt(output.length() - 1);
            continue;
          }
        }
        output.append(nextCh);
      }
    }
    return output.length();
  }
}
