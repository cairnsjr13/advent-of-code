package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

class Day04 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    return numValidPasswords(loader, (c) -> c >= 2);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return numValidPasswords(loader, (c) -> c == 2);
  }

  private long numValidPasswords(Loader2 loader, IntPredicate anyCountTest) {
    List<Integer> minMax = loader.sl("-", Integer::parseInt);
    return IntStream.rangeClosed(minMax.get(0), minMax.get(1)).filter((password) -> isValid(password, anyCountTest)).count();
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
