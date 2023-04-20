package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * We are airdropped near Easter Bunny Headquarters and need to follow some instructions to find the actual location.
 */
class Day01 extends Base2016 {
  private static final Map<Character, UnaryOperator<CardDir>> turns = Map.of('L', CardDir::turnLeft, 'R', CardDir::turnRight);
  private static final ToIntFunction<ImmutablePoint> distance = (location) -> Math.abs(location.x()) + Math.abs(location.y());

  /**
   * Walks the input direction instructions and finds the manhattan distance from the final location.
   */
  @Override
  protected Object part1(Loader loader) {
    return getPart(loader, false);
  }

  /**
   * Walks the input direction instructions and finds the first location that is visited twice.
   */
  @Override
  protected Object part2(Loader loader) {
    return getPart(loader, true);
  }

  /**
   * Starts at the origin and follows each of the input instructions.  Will return either the final
   * location or the first revisted location depending on the part boolean parameter.  This is admittedly
   * not a beautiful way of implementing each part, but doubling the code for each part is even uglier.
   */
  private int getPart(Loader loader, boolean part2) {
    List<String> instructions = loader.sl(", ");
    Set<ImmutablePoint> seen = new HashSet<>();

    CardDir dir = CardDir.North;
    ImmutablePoint location = ImmutablePoint.origin;
    for (String instruction : instructions) {
      dir = turns.get(instruction.charAt(0)).apply(dir);
      int steps = Integer.parseInt(instruction.substring(1));
      for (int i = 0; i < steps; ++i) {
        location = location.move(dir);
        if (part2 && !seen.add(location)) {
          return distance.applyAsInt(location);
        }
      }
    }
    return distance.applyAsInt(location);
  }
}
