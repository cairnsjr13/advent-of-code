package com.cairns.rich.aoc._2019;

import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

class Day04 extends Base2019 {
  @Override
  protected void run() {
    int minPassword = 353096;
    int maxPassword = 843212;
    System.out.println(numValidPasswords(minPassword, maxPassword, (c) -> c >= 2));
    System.out.println(numValidPasswords(minPassword, maxPassword, (c) -> c == 2));
  }

  private long numValidPasswords(int min, int max, IntPredicate anyCountTest) {
    return IntStream.rangeClosed(min, max).filter((password) -> isValid(password, anyCountTest)).count();
  }

  private boolean isValid(int password, IntPredicate anyCountTest) {
    int[] places = new int[6];
    int[] valueCount = new int[10];
    for (int i = 0, m = 1; i < places.length; ++i, m *= 10) {
      places[places.length - 1 - i] = (password / m) % 10;
      ++valueCount[places[places.length - 1 - i]];
    }
    for (int i = 0; i < places.length - 1; ++i) {
      if (places[i + 1] < places[i]) {
        return false;
      }
    }
    return Arrays.stream(valueCount).anyMatch(anyCountTest);
  }
}
