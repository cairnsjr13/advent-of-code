package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToLongBiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We are caught in a sandstorm and need to navigate a network structure based on our input directions.
 */
class Day08 extends Base2023 {
  private static final Pattern pattern = Pattern.compile("^(.+) = \\((.+), (.+)\\)$");

  /**
   * Computes the number of steps required to move from AAA to ZZZ based on the input network.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeTotalSteps(loader, (instructions, connections) -> stepsToZ(instructions, connections, "AAA", "ZZZ"::equals));
  }

  /**
   * Computes the number of steps required to move all positions ending with an 'A' to any position ending in
   * a 'Z' *at the same time*.  The locations go through cycles independent of each other, and thus will take
   * a very large number of steps to sync up.  We can compute the answer substantially faster by finding the
   * length of each starting position's cycle and finding the least common multiple of all of the cycles.
   */
  @Override
  protected Object part2(Loader loader) {
    return computeTotalSteps(loader, (instructions, connections) -> lcm(
        connections.rowKeySet().stream()
            .filter((location) -> location.endsWith("A"))
            .map((start) -> stepsToZ(instructions, connections, start, (c) -> c.endsWith("Z")))
    ));
  }

  /**
   * Parses the instructions and network from the given loader.
   * Then computes the total number of steps required according to the passed in logic.
   */
  private long computeTotalSteps(Loader loader, ToLongBiFunction<String, Table<String, Character, String>> toAnswer) {
    List<String> lines = loader.ml();
    String instructions = lines.get(0);
    Table<String, Character, String> connections = HashBasedTable.create();
    for (String line : lines.subList(2, lines.size())) {
      Matcher matcher = matcher(pattern, line);
      String start = matcher.group(1);
      connections.put(start, 'L', matcher.group(2));
      connections.put(start, 'R', matcher.group(3));
    }
    return toAnswer.applyAsLong(instructions, connections);
  }

  /**
   * Counts the number of steps required to move from the given start location until the given
   * isDone test passes.  The instructions will be followed cyclically until the test passes.
   */
  private int stepsToZ(
      String instructions,
      Table<String, Character, String> connections,
      String start,
      Predicate<String> isDone
  ) {
    int steps = 0;
    for (; !isDone.test(start); ++steps) {
      start = connections.get(start, safeCharAt(instructions, steps));
    }
    return steps;
  }
}
