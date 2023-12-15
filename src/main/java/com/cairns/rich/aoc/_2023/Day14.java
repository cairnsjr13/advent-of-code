package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.TreeMultimap;

/**
 * We need to simulate rolling stones as far as they will go in various directions.
 */
class Day14 extends Base2023 {
  private static final int CYCLES_NEEDED_TO_PROJECT = 400;

  /**
   * Returns the total load of the final stone configuration after rolling all of them to the north.
   */
  @Override
  protected Object part1(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).stream().toArray(char[][]::new);
    moveAllNorthSouth(grid, ReadDir.Up);
    return computeLoad(grid);
  }

  /**
   * Returns the total load of the final stone configuration after doing 1 billion full roll cycles of
   * the compass  where one cycle is equivalent of (north, west, south, east).  In order to do this, we
   * must run the simulation a good number of times so we can detect a loop.  To do this, we keep track
   * of the cycle number after which we see each total load.  We can detect a loop by finding a total
   * load where the difference between all of the seenAt indexes are the same.  Once we have a candidate
   * total load, we check to see if we can project to 1 billion.  We do this by subtracting the first
   * seen cycle number from 1 billion, and then seeing if that number is divisible by our loop length.
   */
  @Override
  protected Object part2(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).stream().toArray(char[][]::new);
    TreeMultimap<Integer, Integer> loadsSeen = TreeMultimap.create();
    for (int i = 1; i <= CYCLES_NEEDED_TO_PROJECT; ++i) {
      moveAllNorthSouth(grid, ReadDir.Up);
      moveAllEastWest(grid, ReadDir.Left);
      moveAllNorthSouth(grid, ReadDir.Down);
      moveAllEastWest(grid, ReadDir.Right);
      loadsSeen.put(computeLoad(grid), i);
    }
    for (int load : loadsSeen.keySet()) {
      if (loadsSeen.get(load).size() > 5) {
        int[] cycles = loadsSeen.get(load).stream().limit(3).mapToInt(Integer::intValue).toArray();
        int difference = cycles[1] - cycles[0];
        if (difference == (cycles[2] - cycles[1])) {
          if ((1_000_000_000 - cycles[0]) % difference == 0) {
            return load;
          }
        }
      }
    }
    throw fail();
  }

  /**
   * Moves all rolling stones as far north or south as they can go.  This method will
   * iterate row by row starting from the farthest in the direction of roll.
   * This ensure that stones only need to be rolled once and can be done completely.
   */
  private void moveAllNorthSouth(char[][] grid, ReadDir dir) {
    int initRow = (dir == ReadDir.Up) ? 0 : (grid.length - 1);
    for (int row = initRow; (0 <= row) && (row < grid.length); row -= dir.dy()) {
      for (int col = 0; col < grid[0].length; ++col) {
        moveIfRollingStone(grid, row, col, dir);
      }
    }
  }

  /**
   * Moves all rolling stones as far east or west as they can go.  This method will
   * iterate column by column starting from the farthest in the direction of roll.
   * This ensures that stones only need to be rolled once and can be done completely.
   */
  private void moveAllEastWest(char[][] grid, ReadDir dir) {
    int initCol = (dir == ReadDir.Left) ? 0 : (grid[0].length - 1);
    for (int col = initCol; (0 <= col) && (col < grid[0].length); col -= dir.dx()) {
      for (int row = 0; row < grid.length; ++row) {
        moveIfRollingStone(grid, row, col, dir);
      }
    }
  }

  /**
   * If the given spot contains a rolling stone ('O'), move it as far as possible in the given direction.
   * A stone will continue rolling until it hits either the end of the grid, an immobile stone ('#') or a
   * fully rolled (and now suck) rolling stone.  It is important to iterate properly so stones roll completely.
   */
  private void moveIfRollingStone(char[][] grid, int row, int col, ReadDir dir) {
    if (grid[row][col] == 'O') {
      for (
          int nextRow = row + dir.dy(), nextCol = col + dir.dx();
          (0 <= nextRow) && (nextRow < grid.length) && (0 <= nextCol) && (nextCol < grid[0].length);
          nextRow += dir.dy(), nextCol += dir.dx()
      ) {
        if (grid[nextRow][nextCol] == '.') {
          grid[row][col] = '.';
          grid[nextRow][nextCol] = 'O';
          row += dir.dy();
          col += dir.dx();
        }
        else {
          break;
        }
      }
    }
  }

  /**
   * Computes the total load of the given configuration.  Each rolling stone will have its 1 based offset
   * from the bottom of the grid counted as its individual load.  (bottom is worth 1, each row increases 1).
   */
  private int computeLoad(char[][] grid) {
    int load = 0;
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        if (grid[row][col] == 'O') {
          load += (grid.length - row);
        }
      }
    }
    return load;
  }
}
