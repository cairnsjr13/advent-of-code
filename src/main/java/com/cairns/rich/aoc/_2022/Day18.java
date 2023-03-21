package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;

class Day18 extends Base2022 {
  private static final int[][] dXdYdZs = {
      { -1, 0, 0 }, { 1, 0, 0 },
      { 0, -1, 0 }, { 0, 1, 0 },
      { 0, 0, -1 }, { 0, 0, 1 }
  };
  private static final int AIR = 0;
  private static final int LAVA = 1;
  private static final int WATER = 2;

  @Override
  protected Object part1(Loader loader) {
    return totalSurfaceArea(loader, (s) -> s != LAVA);
  }

  @Override
  protected Object part2(Loader loader) {
    return totalSurfaceArea(loader, (s) -> s == WATER);
  }

  private int totalSurfaceArea(Loader loader, IntPredicate test) {
    List<int[]> droplets = loader.ml((l) -> Arrays.stream(l.split(",")).mapToInt(Integer::parseInt).toArray());
    Grid grid = new Grid(droplets);
    fillWater(grid);
    return droplets.stream().mapToInt((d) -> numSaEdges(grid, test, d)).sum();
  }

  private void fillWater(Grid grid) {
    ArrayDeque<int[]> coords = new ArrayDeque<>();
    coords.offerLast(new int[] { 0, 0, 0 });
    while (!coords.isEmpty()) {
      int[] current = coords.pollFirst();
      if (grid.inBounds(current) && (AIR == grid.get(current))) {
        grid.set(current, WATER);
        for (int[] dXdYdZ : dXdYdZs) {
          coords.offerLast(new int[] { current[0] + dXdYdZ[0], current[1] + dXdYdZ[1], current[2] + dXdYdZ[2] });
        }
      }
    }
  }

  private int numSaEdges(Grid grid, IntPredicate test, int[] coord) {
    int saEdges = 0;
    int[] offsetCoord = new int[3];
    for (int[] dXdYdZ : dXdYdZs) {
      offsetCoord[0] = coord[0] + dXdYdZ[0];
      offsetCoord[1] = coord[1] + dXdYdZ[1];
      offsetCoord[2] = coord[2] + dXdYdZ[2];
      if (grid.inBounds(offsetCoord) && test.test(grid.get(offsetCoord))) {
        ++saEdges;
      }
    }
    return saEdges;
  }

  private static class Grid {
    private static final int BUFFER = 2;

    private final int[][][] states;

    private Grid(List<int[]> droplets) {
      int maxX = getMax(droplets, (d) -> d[0])[0];
      int maxY = getMax(droplets, (d) -> d[1])[1];
      int maxZ = getMax(droplets, (d) -> d[2])[2];
      this.states = new int[maxX + 2 * BUFFER][maxY + 2 * BUFFER][maxZ + 2 * BUFFER];
      droplets.forEach((droplet) -> set(droplet, LAVA));
    }

    private int get(int[] coord) {
      return states[coord[0] + BUFFER][coord[1] + BUFFER][coord[2] + BUFFER];
    }

    private void set(int[] coord, int state) {
      states[coord[0] + BUFFER][coord[1] + BUFFER][coord[2] + BUFFER] = state;
    }

    private boolean inBounds(int[] coord) {
      return (0 <= coord[0] + BUFFER) && (coord[0] + BUFFER < states.length)
          && (0 <= coord[1] + BUFFER) && (coord[1] + BUFFER < states[0].length)
          && (0 <= coord[2] + BUFFER) && (coord[2] + BUFFER < states[0][0].length);
    }
  }
}
