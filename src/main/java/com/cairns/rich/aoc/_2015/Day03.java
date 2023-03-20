package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day03 extends Base2015 {
  private static final Map<Character, RelDir> lookup = Map.of(  // TODO: use diff lookup key
      '^', RelDir.Up,
      '>', RelDir.Right,
      'v', RelDir.Down,
      '<', RelDir.Left
  );

  @Override
  protected Object part1(Loader2 loader) {
    return numUniqueHouses(loader.sl(), 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return numUniqueHouses(loader.sl(), 2);
  }

  private int numUniqueHouses(String input, int numSantas) {
    Set<ImmutablePoint> seen = new HashSet<>();
    seen.add(ImmutablePoint.origin);
    List<MutablePoint> santas =
        IntStream.range(0, numSantas).mapToObj((i) -> MutablePoint.origin()).collect(Collectors.toList());
    for (int i = 0; i < input.length();) {
      for (MutablePoint santa : santas) {
        santa.move(lookup.get(input.charAt(i)));
        seen.add(new ImmutablePoint(santa.x(), santa.y()));
        ++i;
      }
    }
    return seen.size();
  }
}
