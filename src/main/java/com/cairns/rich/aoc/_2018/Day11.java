package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day11 extends Base2018 {
  @Override
  protected Object part1(Loader loader) {
    int[][] cells = buildCells(Integer.parseInt(loader.sl()));
    return findIdOfLargest(cells, List.of(3), false);
  }

  @Override
  protected Object part2(Loader loader) {
    int[][] cells = buildCells(Integer.parseInt(loader.sl()));
    return findIdOfLargest(cells, IntStream.range(1, 301).boxed().collect(Collectors.toList()), true);
  }

  private String findIdOfLargest(int[][] cells, Iterable<Integer> sizes, boolean includeSize) {
    int[][] sums = buildSums(cells);
    int bestX = -1;
    int bestY = -1;
    int bestSize = -1;
    int maxSum = Integer.MIN_VALUE;
    for (int y = 0; y < cells.length; ++y) {
      for (int x = 0; x < cells[0].length; ++x) {
        for (int size : sizes) {
          if ((y + size > cells.length) || (x + size > cells[0].length)) {
            break;
          }
          int sum = sums[y + size - 1][x + size - 1]
                  - zeroAccess(sums, y - 1, x + size - 1)
                  - zeroAccess(sums, y + size - 1, x - 1)
                  + zeroAccess(sums, y - 1, x - 1);
          if (maxSum < sum) {
            bestX = x + 1;
            bestY = y + 1;
            bestSize = size;
            maxSum = sum;
          }
        }
      }
    }
    return bestX + "," + bestY + ((includeSize) ? "," + bestSize : "");
  }

  private int zeroAccess(int[][] cells, int y, int x) {
    return ((0 <= y) && (y < cells.length) && (0 <= x) && (x < cells[0].length)) ? cells[y][x] : 0;
  }

  private int[][] buildSums(int[][] cells) {
    int[][] sums = new int[cells.length][cells[0].length];
    for (int y = 0; y < cells.length; ++y) {
      for (int x = 0; x < cells[0].length; ++x) {
        sums[y][x] = cells[y][x]
                   + zeroAccess(sums, y, x - 1)
                   + zeroAccess(sums, y - 1, x)
                   - zeroAccess(sums, y - 1, x - 1);
      }
    }
    return sums;
  }

  private int[][] buildCells(int serialNum) {
    int[][] cells = new int[300][300];
    for (int y = 0; y < cells.length; ++y) {
      for (int x = 0; x < cells[0].length; ++x) {
        int rackId = (x + 1) + 10;
        int powerLevel = (rackId * (rackId * (y + 1) + serialNum) % 1000) / 100 - 5;
        cells[y][x] = powerLevel;
      }
    }
    return cells;
  }
}
