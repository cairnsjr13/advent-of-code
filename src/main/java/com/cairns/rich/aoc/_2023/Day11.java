package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * We have an image of galaxies and want to determine the sum of the distances between all distinct, unordered galaxy
 * pairs.  However, there is universe expansion in the picture any time there is a completely empty row or column.
 */
class Day11 extends Base2023 {
  private static final ConfigToken<Integer> expansionFactorToken = ConfigToken.of("expansionFactor", Integer::parseInt);

  /**
   * Computes the sum total of the distances between distinct, unordered galaxy pairs with the configured universe expansion.
   */
  @Override
  protected Object part1(Loader loader) {
    return findTotalDistanceBetweenGalaxies(loader);
  }

  /**
   * Computes the sum total of the distances between distinct, unordered galaxy pairs with the configured universe expansion.
   */
  @Override
  protected Object part2(Loader loader) {
    return findTotalDistanceBetweenGalaxies(loader);
  }

  /**
   * Loads the universe with configured expansion factor and computes the distance between all unordered
   * pairs of galaxies using their {@link #manhattanDistance(ImmutablePoint, ImmutablePoint)} distances.
   */
  private long findTotalDistanceBetweenGalaxies(Loader loader) {
    int expansionFactor = loader.getConfig(expansionFactorToken);
    List<ImmutablePoint> galaxies = findGalaxies(loader, expansionFactor);
    long total = 0;
    for (int i = 0; i < galaxies.size(); ++i) {
      for (int j = i + 1; j < galaxies.size(); ++j) {
        total += manhattanDistance(galaxies.get(i), galaxies.get(j));
      }
    }
    return total;
  }

  /**
   * Computes the Manhattan distance between the given points.
   */
  private long manhattanDistance(ImmutablePoint left, ImmutablePoint right) {
    return Math.abs(left.x() - right.x())
         + Math.abs(left.y() - right.y());
  }

  /**
   * Returns a list of expansion adjusted locations for all galaxies.  Each empty row/col adds an extra number of rows/cols
   * equivalent to (expansionFactor - 1).  This is because each row/col already adds 1 under normal circumstances.
   */
  private List<ImmutablePoint> findGalaxies(Loader loader, int expansionFactor) {
    char[][] specGrid = loader.ml((line) -> line.toCharArray()).toArray(char[][]::new);
    BiFunction<Integer, IntPredicate, Set<Integer>> emptySequenceFactory =
        (max, filter) -> IntStream.range(0, max).filter(filter).boxed().collect(Collectors.toSet());
    Set<Integer> rowsWithNone = emptySequenceFactory.apply(specGrid.length, (r) -> isSequenceEmpty(specGrid, r, 0, 0, 1));
    Set<Integer> colsWithNone = emptySequenceFactory.apply(specGrid[0].length, (c) -> isSequenceEmpty(specGrid, 0, c, 1, 0));

    List<ImmutablePoint> galaxies = new ArrayList<>();
    int numExtraRows = 0;
    for (int r = 0; r < specGrid.length; ++r) {
      if (rowsWithNone.contains(r)) {
        numExtraRows += (expansionFactor - 1);
      }
      else {
        int numExtraCols = 0;
        for (int c = 0; c < specGrid[0].length; ++c) {
          if (colsWithNone.contains(c)) {
            numExtraCols += (expansionFactor - 1);
          }
          else if (specGrid[r][c] == '#') {
            galaxies.add(new ImmutablePoint(c + numExtraCols, r + numExtraRows));
          }
        }
      }
    }
    return galaxies;
  }

  /**
   * Helper method to determine if the row/col described by input parameters is completely empty of galaxies.
   */
  private boolean isSequenceEmpty(char[][] grid, int startR, int startC, int dr, int dc) {
    for (int r = startR, c = startC; (r < grid.length) && (c < grid[0].length); r += dr, c += dc) {
      if (grid[r][c] == '#') {
        return false;
      }
    }
    return true;
  }
}
