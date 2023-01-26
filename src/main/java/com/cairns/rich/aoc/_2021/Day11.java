package com.cairns.rich.aoc._2021;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Day11 extends Base2021 {
  @Override
  protected void run()  {
    int[][] grid = fullLoader.ml((line) -> line.chars().map((c) -> c - '0').toArray()).toArray(int[][]::new);

    int totalFlashes = 0;
    for (int step = 1; true; ++step) {
      for (int r = 0; r < grid.length; ++r) {
        for (int c = 0; c < grid[0].length; ++c) {
          grid[r][c] = grid[r][c] + 1;
        }
      }
      
      Multimap<Integer, Integer> hasFlashed = HashMultimap.create();
      for (int r = 0; r < grid.length; ++r) {
        for (int c = 0; c < grid[0].length; ++c) {
          if (grid[r][c] > 9) {
            flash(grid, r, c, hasFlashed);
          }
        }
      }
      
      hasFlashed.forEach((r, c) -> grid[r][c] = 0);
      totalFlashes += hasFlashed.size();
      
      if (step == 100) {
        System.out.println(totalFlashes);
      }
      if (hasFlashed.size() == (grid.length * grid[0].length)) {
        System.out.println(step);
        break;
      }
    }
  }
  
  private void flash(int[][] grid, int r, int c, Multimap<Integer, Integer> hasFlashed) {
    if (!hasFlashed.containsEntry(r, c)) {
      hasFlashed.put(r, c);
      for (int dr = -1; dr <= 1; ++dr) {
        for (int dc = -1; dc <= 1; ++dc) {
          if ((dr != 0) || (dc != 0)) {
            int nr = r + dr;
            int nc = c + dc;
            if ((0 <= nr) && (nr < grid.length) && (0 <= nc) && (nc < grid[0].length)) {
              ++grid[nr][nc];
              if (grid[nr][nc] > 9) {
                flash(grid, nr, nc, hasFlashed);
              }
            }
          }
        }
      }
    }
  }
}
