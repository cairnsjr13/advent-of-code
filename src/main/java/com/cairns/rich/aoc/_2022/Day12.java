package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.List;

class Day12 extends Base2022 {
  @Override
  protected Object part1(Loader loader) {
    Init init = new Init(loader.ml());
    return minStepsFrom(init, init.part1Start);
  }

  @Override
  protected Object part2(Loader loader) {
    Init init = new Init(loader.ml());
    long min = Integer.MAX_VALUE;
    for (int y = 0; y < init.grid.length; ++y) {
      for (int x = 0; x < init.grid[0].length; ++x) {
        ImmutablePoint from = new ImmutablePoint(x, y);
        if (height(init, from) == 'a') {
          min = Math.min(min, minStepsFrom(init, from));
        }
      }
    }
    return min;
  }

  private long minStepsFrom(Init init, ImmutablePoint from) {
    return bfs(from, init.end::equals, SearchState::getNumSteps, (current, registrar) -> {
      for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
        ImmutablePoint next = current.move(dir);
        if (isValid(init, next) && (height(init, next) <= 1 + height(init, current))) {
          registrar.accept(next);
        }
      }
    }).map(SearchState::getNumSteps).orElse(Long.MAX_VALUE);
  }

  private char height(Init init, ImmutablePoint location) {
    return init.grid[location.y()][location.x()];
  }

  private boolean isValid(Init init, ImmutablePoint location) {
    return (0 <= location.x()) && (location.x() < init.grid[0].length)
        && (0 <= location.y()) && (location.y() < init.grid.length);
  }

  private static class Init {
    private final char[][] grid;
    private final ImmutablePoint part1Start;
    private final ImmutablePoint end;

    private Init(List<String> lines) {
      this.grid = lines.stream().map(String::toCharArray).toArray(char[][]::new);
      this.part1Start = findAndReplace('S', 'a');
      this.end = findAndReplace('E', 'z');
    }

    private ImmutablePoint findAndReplace(char find, char replace) {
      for (int row = 0; row < grid.length; ++row) {
        for (int col = 0; col < grid[0].length; ++col) {
          if (grid[row][col] == find) {
            grid[row][col] = replace;
            return new ImmutablePoint(col, row);
          }
        }
      }
      throw fail("Couldnt find " + find);
    }
  }
}
