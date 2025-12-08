package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Beams of light need to be split and combined as they move down a grid.
 */
class Day07 extends Base2025 {
  /**
   * To compute the number of splits overall, we combine split beams at each level using a {@link Set}.
   * Each split on the current level counts, however if two beams collide, only one appears on the next level.
   */
  @Override
  protected Object part1(Loader loader) {
    char[][] grid = Grid.parseChars(loader);
    Set<Integer> beamXs = new HashSet<>();
    beamXs.add(Grid.find(grid, 'S').x());
    int splits = 0;
    for (int r = 1; r < grid.length; ++r) {
      char[] row = grid[r];
      Set<Integer> nextBeamXs = new HashSet<>();
      for (Integer beamX : beamXs) {
        if (row[beamX] == '^') {
          ++splits;
          nextBeamXs.add(beamX - 1);
          nextBeamXs.add(beamX + 1);
        }
        else {
          nextBeamXs.add(beamX);
        }
      }
      beamXs = nextBeamXs;
    }
    return splits;
  }

  /**
   * Uses a cached dfs approach to count all possible unique paths a proton can take through the grid.
   */
  @Override
  protected Object part2(Loader loader) {
    char[][] grid = Grid.parseChars(loader);
    return walkPaths(HashBasedTable.create(), grid, 0, Grid.find(grid, 'S').x());
  }

  /**
   * Recursively walks paths through the grid using the given cache to keep track of traveled paths..
   * A proton continues downward if unimpeded.  If it hits a ^ it takes both the left and right paths.
   */
  private long walkPaths(Table<Integer, Integer, Long> cache, char[][] grid, Integer row, Integer col) {
    if (row == grid.length - 1) {
      return 1;
    }
    if (!cache.contains(row, col)) {
      Integer nextRow = row + 1;
      long paths = (grid[nextRow][col] == '.')
          ? walkPaths(cache, grid, nextRow, col)
          : walkPaths(cache, grid, nextRow, col - 1) + walkPaths(cache, grid, nextRow, col + 1);
      cache.put(row, col, paths);
    }
    return cache.get(row, col);
  }
}
