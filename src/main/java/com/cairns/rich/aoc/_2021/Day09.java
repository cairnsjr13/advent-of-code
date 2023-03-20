package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.PriorityQueue;
import java.util.function.ToIntFunction;

class Day09 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return getAnswer(loader, (heights) -> {
      int totalRisks = 0;
      for (int r = 0; r < heights.length; ++r) {
        for (int c = 0; c < heights[0].length; ++c) {
          if (isLowPoint(heights, r, c)) {
            totalRisks += 1 + heights[r][c];
          }
        }
      }
      return totalRisks;
    });
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getAnswer(loader, (heights) -> {
      boolean[][] visited = new boolean[heights.length + 2][heights[0].length + 2];
      PriorityQueue<Integer> largestBasinSizes = new PriorityQueue<>();
      for (int r = 0; r < heights.length; ++r) {
        for (int c = 0; c < heights[0].length; ++c) {
          if (!visited[c + 1][r + 1]) {
            largestBasinSizes.offer(numLocationsInBasin(heights, visited, r, c));
            if (largestBasinSizes.size() > 3) {
              largestBasinSizes.poll();
            }
          }
        }
      }
      return largestBasinSizes.stream().mapToInt(Integer::intValue).reduce(1, Math::multiplyExact);
    });
  }

  private int getAnswer(Loader2 loader, ToIntFunction<int[][]> toAnswer) {
    return toAnswer.applyAsInt(loader.ml((line) -> line.chars().map((c) -> c - '0').toArray()).stream().toArray(int[][]::new));
  }

  private int numLocationsInBasin(int[][] heights, boolean[][] visited, int r, int c) {
    int locationsInBasin = 0;
    if (!visited[c + 1][r + 1] && (getHeight(heights, r, c) < 9)) {
      visited[c + 1][r + 1] = true;
      ++locationsInBasin;
      for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
        locationsInBasin += numLocationsInBasin(heights, visited, r + dir.dy(), c + dir.dx());
      }
    }
    return locationsInBasin;
  }

  private boolean isLowPoint(int[][] heights, int r, int c) {
    int heightToInspect = getHeight(heights, r, c);
    for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
      if (heightToInspect >= getHeight(heights, r + dir.dy(), c + dir.dx())) {
        return false;
      }
    }
    return true;
  }

  private int getHeight(int[][] heights, int r, int c) {
    return ((0 <= r) && (r < heights.length) && (0 <= c) && (c < heights[0].length))
         ? heights[r][c]
         : 9;
  }
}
