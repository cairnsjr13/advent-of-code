package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.HashSet;
import java.util.Set;
import java.util.function.ToIntBiFunction;

class Day10 extends Base2024 {
  /**
   * A trailhead's score is the number of unique summits ('9') that can be reached regardless of path.
   */
  @Override
  protected Object part1(Loader loader) {
    return findTotal(loader, this::findScore);
  }

  /**
   * A trailhead's rating is the number of unique paths that can be taken to a summit ('9').
   */
  @Override
  protected Object part2(Loader loader) {
    return findTotal(loader, this::findRating);
  }

  /**
   * Helper method that finds the total value of all trailheads found in the given loaders input grid.
   */
  private int findTotal(Loader loader, ToIntBiFunction<int[][], ImmutablePoint> toTrailheadValue) {
    int[][] grid = Grid.parseInts(loader);
    int total = 0;
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        if (grid[row][col] == 0) {
          total += toTrailheadValue.applyAsInt(grid, new ImmutablePoint(col, row));
        }
      }
    }
    return total;
  }

  /**
   * A trailhead's score is defined as all of the unique summits ('9') that can be reached starting at the
   * trailhead and increasing exactly 1 elevation each step.  This is computed using a bread-first-first search
   * starting at the trailhead and preventing repeats globally, registering a summit on each '9' encountered.
   */
  private int findScore(int[][] grid, ImmutablePoint trailhead) {
    Set<ImmutablePoint> nines = new HashSet<>();
    bfs(
        trailhead,
        (cur) -> false,
        (cur, registrar) -> {
          int curElevation = grid[cur.y()][cur.x()];
          if (curElevation == 9) {
            nines.add(cur);
          }
          for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
            ImmutablePoint next = cur.move(dir);
            if (Grid.isValid(grid, next)) {
              int nextElevation = grid[next.y()][next.x()];
              if (nextElevation == curElevation + 1) {
                registrar.accept(next);
              }
            }
          }
        }
    );
    return nines.size();
  }

  /**
   * A trailhead's rating is defined as all of the unique paths to any summit ('9') that can
   * be reached starting at the trailhead and increasing exactly 1 elevation each step.  This
   * is computed using a depth-first-search starting at the trailhead and preventing repeats
   * along the current path, registering a unique path any time a summit is encountered.
   */
  private int findRating(int[][] grid, ImmutablePoint trailhead) {
    return findRating(grid, new HashSet<>(), trailhead);
  }

  /**
   * Recursive depth-first-search method to find all of the unique paths that end in a summit from the given point.
   */
  private int findRating(int[][] grid, Set<ImmutablePoint> visited, ImmutablePoint cur) {
    int curElevation = grid[cur.y()][cur.x()];
    if (curElevation == 9) {
      return 1;
    }
    int ratingFromHere = 0;
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      ImmutablePoint next = cur.move(dir);
      if (!visited.contains(next) && Grid.isValid(grid, next)) {
        int nextElevation = grid[next.y()][next.x()];
        if (nextElevation == curElevation + 1) {
          visited.add(next);
          ratingFromHere += findRating(grid, visited, next);
          visited.remove(next);
        }
      }
    }
    return ratingFromHere;
  }
}
