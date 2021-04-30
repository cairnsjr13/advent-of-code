package com.cairns.rich.aoc._2015;

import java.util.Arrays;

class Day20 extends Base2015 {
  private static final int INPUT = 29_000_000;
  
  @Override
  protected void run() {
    runSimulation(1_000_000, 10, Integer.MAX_VALUE);
    runSimulation(1_000_000, 11, 50);
  }
  
  private void runSimulation(int numHouses, int elfMultiplier, int stopAfter) {
    int[] houses = getHouses(numHouses, elfMultiplier, stopAfter);
    for (int i = 1; i < houses.length; ++i) {
      if (houses[i] >= INPUT) {
        System.out.println("winner: " + i);
        return;
      }
    }
    System.out.println(Arrays.stream(houses).max().getAsInt());
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
