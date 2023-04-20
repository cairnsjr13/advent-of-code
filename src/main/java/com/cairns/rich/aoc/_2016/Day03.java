package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.List;

/**
 * The Easter Bunny hq design department needs help finding valid triangles.
 * Triangles need to have every pair of sides be larger than the remaining side.
 */
class Day03 extends Base2016 {
  /**
   * Computes the number of triangles when grouped by rows.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(this::parseRow).stream().filter(this::isValidTriangle).count();
  }

  /**
   * Computes the number of triangles when grouped by cols.
   */
  @Override
  protected Object part2(Loader loader) {
    List<int[]> rows = loader.ml(this::parseRow);
    int numValidTriangles = 0;
    for (int col = 0; col < 3; ++col) {
      for (int row = 0; row < rows.size(); row += 3) {
        if (isValidTriangle(rows.get(row)[col], rows.get(row + 1)[col], rows.get(row + 2)[col])) {
          ++numValidTriangles;
        }
      }
    }
    return numValidTriangles;
  }

  /**
   * Returns true if every edge of the given triangle is less than the other two's sum.
   */
  private boolean isValidTriangle(int... edges) {
    int sum = Arrays.stream(edges).sum();
    for (int i = 0; i < edges.length; ++i) {
      if (sum - edges[i] <= edges[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns an array of ints that represents the sides of a triange.
   */
  private int[] parseRow(String spec) {
    return Arrays.stream(spec.trim().split(" +")).mapToInt(Integer::parseInt).toArray();
  }
}
