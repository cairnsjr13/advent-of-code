package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import java.util.List;

/**
 * The crossword search is a bit different as we are searching for "XMAS" or "MAS" in an X.
 */
class Day04 extends Base2024 {
  /**
   * We need count all of the "XMAS"s we can find in any direction.
   */
  @Override
  protected Object part1(Loader loader) {
    return countFrom(loader, 'X', this::countTotalXmasFrom);
  }

  /**
   * We need to count all of the "MAS"xs we can find in any direction:
   */
  @Override
  protected Object part2(Loader loader) {
    return countFrom(loader, 'A', this::countTotalMasXFrom);
  }

  /**
   * Helper function to count all of the "XMAS"s in the input grid.
   * This is done by checking the given finder against every position with the given anchor.
   */
  private int countFrom(Loader loader, char anchor, XmasPatternFinder finder) {
    List<String> grid = loader.ml();
    int totalXmas = 0;
    for (int row = 0; row < grid.size(); ++row) {
      for (int col = 0; col < grid.get(0).length(); ++col) {
        if (anchor == grid.get(row).charAt(col)) {
          totalXmas += finder.countXmasAnchoredAt(grid, row, col);
        }
      }
    }
    return totalXmas;
  }

  /**
   * {@link XmasPatternFinder} method that checks for "MAS" in any direction from the given position.
   */
  private int countTotalXmasFrom(List<String> input, int row, int col) {
    int numXmas = 0;
    for (int dr = -1; dr <= 1; ++dr) {
      for (int dc = -1; dc <= 1; ++dc) {
        if ((dr != 0) || (dc != 0)) {
          if (hasXmas(input, row, col, dr, dc)) {
            ++numXmas;
          }
        }
      }
    }
    return numXmas;
  }

  /**
   * Returns true if the given anchor point has "MAS" in the given direction.
   */
  private boolean hasXmas(List<String> input, int row, int col, int dr, int dc) {
    return isExpected(input, row + 1 * dr, col + 1 * dc, 'M')
        && isExpected(input, row + 2 * dr, col + 2 * dc, 'A')
        && isExpected(input, row + 3 * dr, col + 3 * dc, 'S');
  }

  /**
   * {@link XmasPatternFinder} that returns 1 if both diagonals have "M" and "S" opposite of each other.
   *
   *   M.M      S.M      S.S      M.S
   *   .A.  or  .A.  or  .A.  or  .A.
   *   S.S      S.M      M.M      M.S
   */
  private int countTotalMasXFrom(List<String> input, int row, int col) {
    boolean hasBothMas = isMasInDir(input, row - 1, col - 1, row + 1, col + 1)
                      && isMasInDir(input, row - 1, col + 1, row + 1, col - 1);
    return (hasBothMas) ? 1 : 0;
  }

  /**
   * Returns true if the the first corner (row1,col1) is "M" or "S" and the opposite corner (row2,col2) is the other.
   */
  private boolean isMasInDir(List<String> input, int row1, int col1, int row2, int col2) {
    return (isExpected(input, row1, col1, 'M') && isExpected(input, row2, col2, 'S'))
        || (isExpected(input, row1, col1, 'S') && isExpected(input, row2, col2, 'M'));
  }

  /**
   * Returns true if the given row/col location is valid and the given expected char.
   */
  private boolean isExpected(List<String> input, int row, int col, char expected) {
    return (0 <= row) && (row < input.size())
        && (0 <= col) && (col < input.get(0).length())
        && (expected == input.get(row).charAt(col));
  }

  /**
   * Functional interface that lets us count the number of xmas's from an anchor point.
   */
  private interface XmasPatternFinder {
    /**
     * Returns the number of xmas's found at the given anchor that has already been verified.
     */
    int countXmasAnchoredAt(List<String> grid, int row, int col);
  }
}
