package com.cairns.rich.aoc.grid;

import java.util.List;
import java.util.function.IntSupplier;

/**
 * Utility class containing methods to check validity of {@link Point}s in a grid.
 */
public final class Grid {
  private Grid() { /* Do not instantiate */ }

  /**
   * Returns the Manhattan distance (horiz + vert) of two points.
   * TODO: find other usages (search for Math.abs)
   */
  public static int manhattanDist(Point<?> first, Point<?> second) {
    return Math.abs(first.x() - second.x())
         + Math.abs(first.y() - second.y());
  }

  /**
   * Returns the location of the given character in the grid.
   * Throws a {@link RuntimeException} if not found.
   * TODO: find other usages
   */
  public static ImmutablePoint find(char[][] grid, char target) {
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        if (grid[row][col] == target) {
          return new ImmutablePoint(col, row);
        }
      }
    }
    throw new RuntimeException("Couldn't find '" + target + "'");
  }

  /**
   * Returns the location of the given character in the grid after replacing it.
   * Throws a {@link RuntimeException} if not found.
   * TODO: find other usages
   */
  public static ImmutablePoint findAndReplace(char[][] grid, char target, char replace) {
    ImmutablePoint found = find(grid, target);
    grid[found.y()][found.x()] = replace;
    return found;
  }

  /**
   * Returns true if the given grid contains the given point.
   */
  public static boolean isValid(List<List<?>> grid, Point<?> at) {
    return isValid(at, grid.size(), () -> grid.get(0).size());
  }

  /**
   * Returns true if the given grid contains the given row/col coordinates.
   */
  public static boolean isValid(List<List<?>> grid, int row, int col) {
    return isValid(row, col, grid.size(), () -> grid.get(0).size());
  }

  /**
   * Returns true if the given grid contains the given point.
   */
  public static boolean isValid(int[][] grid, Point<?> at) {
    return isValid(at, grid.length, () -> grid[0].length);
  }

  /**
   * Returns true if the given grid contains the given row/col coordinates.
   */
  public static boolean isValid(int[][] grid, int row, int col) {
    return isValid(row, col, grid.length, () -> grid[0].length);
  }

  /**
   * Returns true if the given grid contains the given point.
   */
  public static boolean isValid(long[][] grid, Point<?> at) {
    return isValid(at, grid.length, () -> grid[0].length);
  }

  /**
   * Returns true if the given grid contains the given row/col coordinates.
   */
  public static boolean isValid(long[][] grid, int row, int col) {
    return isValid(row, col, grid.length, () -> grid[0].length);
  }

  /**
   * Returns true if the given grid contains the given point.
   */
  public static boolean isValid(boolean[][] grid, Point<?> at) {
    return isValid(at, grid.length, () -> grid[0].length);
  }

  /**
   * Returns true if the given grid contains the given row/col coordinates.
   */
  public static boolean isValid(boolean[][] grid, int row, int col) {
    return isValid(row, col, grid.length, () -> grid[0].length);
  }

  /**
   * Returns true if the given grid contains the given point.
   */
  public static boolean isValid(char[][] grid, Point<?> at) {
    return isValid(at, grid.length, () -> grid[0].length);
  }

  /**
   * Returns true if the given grid contains the given row/col coordinates.
   */
  public static boolean isValid(char[][] grid, int row, int col) {
    return isValid(row, col, grid.length, () -> grid[0].length);
  }

  /**
   * Helper method to determine if the given grid contains the given point.
   */
  private static boolean isValid(Point<?> at, int numRows, IntSupplier numCols) {
    return isValid(at.y, at.x, numRows, numCols);
  }

  /**
   * Helper method to determine if the given grid contains the given point.
   * Note that the order of the if checks is important as the number of
   * columns cannot be calculated until the grid is confirmed to have rows.
   */
  private static boolean isValid(int row, int col, int numRows, IntSupplier numCols) {
    return (0 <= row) && (row < numRows)
        && (0 <= col) && (col < numCols.getAsInt());
  }
}
