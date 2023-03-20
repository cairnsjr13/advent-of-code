package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;

class Day06 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return getNumFishAfter(loader, 80);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getNumFishAfter(loader, 256);
  }

  private long getNumFishAfter(Loader2 loader, int days) {
    long[] fishByAge = new long[9];
    loader.sl(",", Integer::parseInt).forEach((age) -> ++fishByAge[age]);
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
