package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Santa is getting drunken flying instructions from an elf.
 * We need to figure out how many houses get at least one present.
 */
class Day03 extends Base2015 {
  private static final Map<Character, RelDir> lookup = Map.of(  // TODO: use diff lookup key
      '^', RelDir.Up,
      '>', RelDir.Right,
      'v', RelDir.Down,
      '<', RelDir.Left
  );

  /**
   * With one santa, how many houses get at least one present?
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    return numUniqueHouses(loader.sl(), 1);
  }

  /**
   * With two santas, how many houses get at least one present?
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    return numUniqueHouses(loader.sl(), 2);
  }

  /**
   * Each santa takes turns moving in the next direction until all of the directions are exhausted.
   * We return the number of unique locations seen to determine how many hosues get at least one.
   */
  private int numUniqueHouses(String input, int numSantas) {
    Set<ImmutablePoint> seen = new HashSet<>();
    seen.add(ImmutablePoint.origin);
    List<MutablePoint> santas =
        IntStream.range(0, numSantas).mapToObj((i) -> MutablePoint.origin()).collect(Collectors.toList());
    for (int i = 0; i < input.length();) {
      for (MutablePoint santa : santas) {
        santa.move(lookup.get(input.charAt(i)));
        seen.add(new ImmutablePoint(santa));
        ++i;
      }
    }
    return seen.size();
  }
}
