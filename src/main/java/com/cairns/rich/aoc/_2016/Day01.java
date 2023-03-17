package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

class Day01 extends Base2016 {
  private static final Map<Character, UnaryOperator<CardDir>> turns = Map.of('L', CardDir::turnLeft, 'R', CardDir::turnRight);
  private static final ToIntFunction<ImmutablePoint> distance = (location) -> Math.abs(location.x()) + Math.abs(location.y());

  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<String> instructions = loader.sl(", ");
    Set<ImmutablePoint> seen = new HashSet<>();

    CardDir dir = CardDir.North;
    ImmutablePoint location = new ImmutablePoint(0, 0);
    for (String instruction : instructions) {
      dir = turns.get(instruction.charAt(0)).apply(dir);
      int steps = Integer.parseInt(instruction.substring(1));
      for (int i = 0; i < steps; ++i) {
        location = location.move(dir);
        if ((seen != null) && !seen.add(location)) {
          seen = null;
          result.part2(distance.applyAsInt(location));
        }
      }
    }
    result.part1(distance.applyAsInt(location));
  }
}
