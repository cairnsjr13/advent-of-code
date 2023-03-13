package com.cairns.rich.aoc._2021;

import java.util.Arrays;
import java.util.List;

class Day06 extends Base2021 {
  @Override
  protected void run() {
    List<Integer> initial = fullLoader.sl(",", Integer::parseInt);
    long[] fishesByAge = new long[9];
    initial.forEach((age) -> ++fishesByAge[age]);

    System.out.println(getNumFishAfter(fishesByAge, 80));
    System.out.println(getNumFishAfter(fishesByAge, 256));
  }

  private long getNumFishAfter(long[] fishByAge, int days) {
    fishByAge = Arrays.copyOf(fishByAge, fishByAge.length);
    for (int day = 0; day < days; ++day) {
      long newFish = fishByAge[0];
      for (int age = 1; age < 9; ++age) {
        fishByAge[age - 1] = fishByAge[age];
      }
      fishByAge[8] = newFish;
      fishByAge[6] += newFish;
    }
    return Arrays.stream(fishByAge).sum();
  }
}
