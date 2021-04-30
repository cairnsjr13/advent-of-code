package com.cairns.rich.aoc._2018;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day11 extends Base2018 {
  @Override
  protected void run() {
    int serialNum = 4455;
    int[][] cells = buildCells(serialNum);
    System.out.println(findIdOfLargest(cells, List.of(3)));
    System.out.println(findIdOfLargest(cells, IntStream.range(1, 301).boxed().collect(Collectors.toList())));
  }
  
  private String findIdOfLargest(int[][] cells, Iterable<Integer> sizes) {
    int[][] sums = new int[cells.length][cells[0].length];
    for (int y = 0; y < cells.length; ++y) {
      for (int x = 0; x < cells[0].length; ++x) {
        sums[y][x] = cells[y][x]
                   + zeroAccess(sums, y, x - 1)
                   + zeroAccess(sums, y - 1, x)
                   - zeroAccess(sums, y - 1, x - 1);
      }
    }
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
    return bestX + "," + bestY + "," + bestSize;
  }
  
  private int zeroAccess(int[][] cells, int y, int x) {
    return ((0 <= y) && (y < cells.length) && (0 <= x) && (x < cells[0].length)) ? cells[y][x] : 0;
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
