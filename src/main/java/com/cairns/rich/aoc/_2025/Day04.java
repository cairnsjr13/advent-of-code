package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;

/**
 * Rolls of paper are laid out in a grid and need to be analyzed for accessibility and removed.
 */
class Day04 extends Base2025 {
  /**
   * We need to figure out how many rolls are accessible in the first generation.
   * This can be computed by seeing how many are removed from the input grid.
   */
  @Override
  protected Object part1(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    char[][] ignored = new char[grid.length][grid[0].length];
    return removeAccessible(grid, ignored);
  }

  /**
   * We need to figure out how many rolls can be removed if we continuously check
   * which are accessible until the grid is in a stable/stuck state.  This can be
   * done by repeatedly running the removal algorithm until none are removed.
   */
  @Override
  protected Object part2(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    char[][] nextGrid = new char[grid.length][grid[0].length];
    int totalRemoved = 0;
    while (true) {
      int numRemoved = removeAccessible(grid, nextGrid);
      if (numRemoved == 0) {
        return totalRemoved;
      }
      totalRemoved += numRemoved;
      char[][] swapGrid = grid;
      grid = nextGrid;
      nextGrid = swapGrid;
    }
  }

  /**
   * Removes all accessible paper rolls from the given grid by writing the
   * next generation to the given nextGrid.  Returns the number removed.
   */
  private int removeAccessible(char[][] grid, char[][] nextGrid) {
    int numRemoved = 0;
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        nextGrid[row][col] = grid[row][col];
        if ((grid[row][col] == '@') && isAccessible(grid, row, col)) {
          nextGrid[row][col] = '.';
          ++numRemoved;
        }
      }
    }
    return numRemoved;
  }

  /**
   * Returns true if the number of neighbors for the given cell is less than 4.
   */
  private boolean isAccessible(char[][] grid, int row, int col) {
    int neighbors = 0;
    for (int dr = -1; dr <= 1; ++dr) {
      for (int dc = -1; dc <= 1; ++dc) {
        if ((dr != 0) || (dc != 0)) {
          int nRow = row + dr;
          int nCol = col + dc;
          if (Grid.isValid(grid, nRow, nCol) && (grid[nRow][nCol] == '@')) {
            ++neighbors;
          }
        }
      }
    }
    return neighbors < 4;
  }
}
