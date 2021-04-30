package com.cairns.rich.aoc._2019;

import java.util.Arrays;
import java.util.function.IntPredicate;

class Day04 extends Base2019 {
  @Override
  protected void run() {
    int numSimpleValid = 0;
    int numComplexValid = 0;
    for (int password = 353096; password <= 843212; ++password) {
      if (isValid(password, (c) -> c >= 2)) {
        ++numSimpleValid;
      }
      if (isValid(password, (c) -> c == 2)) {
        ++numComplexValid;
      }
    }
    System.out.println(numSimpleValid);
    System.out.println(numComplexValid);
  }
  
  private boolean isValid(int password, IntPredicate anyCountTest) {
    int[] places = new int[6];
    int[] valueCount = new int[10];
    for (int i = 0; i < places.length; ++i) {
      places[places.length - 1 - i] = (password / (int) Math.pow(10, i)) % 10;
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
