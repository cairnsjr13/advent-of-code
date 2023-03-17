package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import java.util.List;

class Day03 extends Base2016 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<int[]> rows = loader.ml(this::parseRow);
    result.part1(getNumValidTrianglesRowBased(rows));
    result.part2(getNumValidTrianglesColBased(rows));
  }

  private long getNumValidTrianglesRowBased(List<int[]> rows) {
    return rows.stream().filter(this::isValidTriangle).count();
  }

  private long getNumValidTrianglesColBased(List<int[]> rows) {
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

  private boolean isValidTriangle(int... edges) {
    int sum = Arrays.stream(edges).sum();
    for (int i = 0; i < edges.length; ++i) {
      if (sum - edges[i] <= edges[i]) {
        return false;
      }
    }
    return true;
  }

  private int[] parseRow(String spec) {
    return Arrays.stream(spec.trim().split(" +")).mapToInt(Integer::parseInt).toArray();
  }
}
