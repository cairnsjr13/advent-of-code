package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.HashMap;
import java.util.Map;

class Day15 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return getLowestRisk(loader, 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getLowestRisk(loader, 5);
  }

  private int getLowestRisk(Loader2 loader, int repeat) {
    int[][] risks = loader.ml((line) -> line.chars().map((c) -> c - '0').toArray()).toArray(int[][]::new);
    Cave cave = new Cave(repeat, risks);
    Map<ImmutablePoint, Integer> shortestPaths = new HashMap<>();
    ImmutablePoint start = new ImmutablePoint(0, 0);
    shortestPaths.put(start, 0);

    ImmutablePoint best = bfs(
        start,
        cave.destination::equals,
        (ss) -> shortestPaths.get(ss.state),
        (current, registrar) -> {
          int shortestPathToCurrent = shortestPaths.get(current);
          for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
            ImmutablePoint next = current.move(dir);
            if (cave.isValid(next)) {
              int costThroughCurrent = shortestPathToCurrent + cave.get(next);
              if (costThroughCurrent < shortestPaths.getOrDefault(next, Integer.MAX_VALUE)) {
                shortestPaths.put(next, costThroughCurrent);
                registrar.accept(next);
              }
            }
          }
        }
    ).get().state;
    return shortestPaths.get(best);
  }

  private static class Cave {
    private final int repeat;
    private final int[][] risks;
    private final ImmutablePoint destination;

    private Cave(int repeat, int[][] risks) {
      this.repeat = repeat;
      this.risks = risks;
      this.destination = new ImmutablePoint(risks.length * repeat - 1, risks[0].length * repeat - 1);
    }

    private int get(ImmutablePoint p) {
      int shiftedRisk = safeGet(safeGet(risks, p.x()), p.y())
                      + (p.x() / risks.length)
                      + (p.y() / risks[0].length);
      return (shiftedRisk - 1) % 9 + 1;
    }

    private boolean isValid(ImmutablePoint p) {
      return (0 <= p.x()) && (p.x() < risks.length * repeat)
          && (0 <= p.y()) && (p.y() < risks[0].length * repeat);
    }
  }
}
