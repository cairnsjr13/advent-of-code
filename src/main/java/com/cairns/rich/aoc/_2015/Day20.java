package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;

/**
 * The elves are bored and Santa is going to have them deliver presents by hand.  Lets see which houses get lucky.
 */
class Day20 extends Base2015 {
  private static final int MAX_HOUSES = 1_000_000;  // TODO: Exponential search up, binary search down?  The 1_000_000 is a hack

  /**
   * Computes the lowest house number that receives at least the number of presents in the input.
   * Each elf delivers 10 presents each to an infinite number of houses.
   */
  @Override
  protected Object part1(Loader loader) {
    return runSimulation(loader, 10, Integer.MAX_VALUE);
  }

  /**
   * Computes the lowest house number that receives at least the number of presents in the input.
   * Each elf delivers 11 presents each to a maximum of 50 houses.
   */
  @Override
  protected Object part2(Loader loader) {
    return runSimulation(loader, 11, 50);
  }

  /**
   * Computes the result of the present delivery and returns the index of the first house that is >= the target.
   *
   * @implNote This will throw with the largest house if we fail.  MAX_HOUSES should be increased in this situation.
   */
  private int runSimulation(Loader loader, int elfMultiplier, int stopAfter) {
    int targetPresents = Integer.parseInt(loader.sl());
    int[] houses = getHouses(MAX_HOUSES, elfMultiplier, stopAfter);
    for (int i = 1; i < houses.length; ++i) {
      if (houses[i] >= targetPresents) {
        return i;
      }
    }
    throw fail(Arrays.stream(houses).max().getAsInt());
  }

  /**
   * Creates an array representing the number of presents each house gets (up to the passed number of houses exclusive).
   * Each elf delivers presents at each house equal to its number times the passed multiplier.  An elf will stop after
   * delivering the given number of presents.  Please note, index 0 is not used as the first house is at index 1.
   */
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
