package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;

// TODO: Exponential search up, binary search down?  The 1_000_000 is a hack
class Day20 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    int targetPresents = Integer.parseInt(loader.sl());
    result.part1(runSimulation(targetPresents, 1_000_000, 10, Integer.MAX_VALUE));
    result.part2(runSimulation(targetPresents, 1_000_000, 11, 50));
  }

  private int runSimulation(int targetPresents, int numHouses, int elfMultiplier, int stopAfter) {
    int[] houses = getHouses(numHouses, elfMultiplier, stopAfter);
    for (int i = 1; i < houses.length; ++i) {
      if (houses[i] >= targetPresents) {
        return i;
      }
    }
    throw fail(Arrays.stream(houses).max().getAsInt());
  }

  private int[] getHouses(int numHouses, int elfMultiplier, int stopAfter) {
    int[] houses = new int[numHouses];
    for (int elf = 1; elf < numHouses; ++elf) {
      int numPresents = elf * elfMultiplier;
      int housesVisited = 0;
      for (int house = elf; (house < numHouses) && (housesVisited < stopAfter); house += elf, ++housesVisited) {
        houses[house] += numPresents;
      }
    }
    return houses;
  }
}
