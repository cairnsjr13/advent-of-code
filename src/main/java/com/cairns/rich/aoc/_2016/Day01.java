package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

class Day01 extends Base2016 {
  private static final Map<Character, UnaryOperator<CardDir>> turns = Map.of('L', CardDir::turnLeft, 'R', CardDir::turnRight);

  @Override
  protected void run() {
    List<String> instructions = fullLoader.sl(", ");
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
          report("Revist", location);
        }
      }
    }
    report("Final", location);
  }

  private void report(String tag, ImmutablePoint location) {
    System.out.println(location + " " + tag + " - " + (Math.abs(location.x()) + Math.abs(location.y())));
  }
}
