package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.Objects;

/**
 * We need to find the optimal heat loss path through a grid with various continue and turn restrictions.
 */
class Day17 extends Base2023 {
  /**
   * Computes the minimum heat loss possible if the cart can move a maximum
   * of 3 times in the same direction with no restrictions on turning.
   */
  @Override
  protected Object part1(Loader loader) {
    return findMinimumHeatLoss(loader, 0, 3);
  }

  /**
   * Computes the minimum heat loss possible if the cart can move a maximum of 10 times in the
   * same direction but cannot turn or stop until it has moved 4 times in the same direction.
   */
  @Override
  protected Object part2(Loader loader) {
    return findMinimumHeatLoss(loader, 4, 10);
  }

  /**
   * Computes the minimum heat loss possible given the restrictions on moving in the same direction and turning.
   * Uses a breadth first search only moving when {@link LocationDesc#canGo(int[][], ReadDir, int, int)} passes.
   */
  private int findMinimumHeatLoss(Loader loader, int minBeforeTurnOrStop, int maxStraights) {
    int[][] grid = loader.ml((l) -> l.chars().map((ch) -> ch - '0').toArray()).toArray(int[][]::new);
    ImmutablePoint target = new ImmutablePoint(grid[0].length - 1, grid.length - 1);
    return bfs(
        new LocationDesc(ImmutablePoint.origin, null, 0, 0),
        (cur) -> (target.equals(cur.at)) && (minBeforeTurnOrStop <= cur.count),
        (ss) -> ss.state.heatLost,
        (cur, registrar) -> {
          for (ReadDir to : EnumUtils.enumValues(ReadDir.class)) {
            if (cur.canGo(grid, to, minBeforeTurnOrStop, maxStraights)) {
              registrar.accept(cur.move(grid, to));
            }
          }
        }
    ).get().state.heatLost;
  }

  /**
   * State class to describe where in the search we are.  A state is unique based
   * upon its location, last direction moved, and its count in that moved direction.
   * We  also track the heatLost up until this state in order to prioritize our search.
   */
  private static class LocationDesc {
    private final ImmutablePoint at;
    private final ReadDir from;
    private final int count;
    private final int heatLost;

    private LocationDesc(ImmutablePoint at, ReadDir from, int count, int heatLost) {
      this.at = at;
      this.from = from;
      this.count = count;
      this.heatLost = heatLost;
    }

    /**
     * Returns true if the next spot moved to will be on the grid, not the
     * opposite direction, and continue/turn restrictions are followed.
     */
    private boolean canGo(int[][] grid, ReadDir to, int minBeforeTurnOrStop, int maxStraights) {
      int nx = at.x() + to.dx();
      int ny = at.y() + to.dy();
      return (0 <= nx) && (nx < grid[0].length)
          && (0 <= ny) && (ny < grid.length)
          && (to.turnAround() != from)
          && (   (from == null)
              || ((to == from) && (count < maxStraights))
              || ((to != from) && (minBeforeTurnOrStop <= count))
             );
    }

    /**
     * Helper method to return a new {@link LocationDesc} that results from moving this in the given direction.
     * The direction count and heatLost will be updated accordingly.
     */
    private LocationDesc move(int[][] grid, ReadDir to) {
      ImmutablePoint next = at.move(to);
      return new LocationDesc(next, to, (to == from) ? count + 1 : 1, heatLost + grid[next.y()][next.x()]);
    }

    @Override
    public boolean equals(Object other) {
      return at.equals(((LocationDesc) other).at)
          && Objects.equals(from, ((LocationDesc) other).from)
          && (count == ((LocationDesc) other).count);
    }

    @Override
    public int hashCode() {
      int hashCode = 17;
      hashCode = (hashCode * 31) + count;
      hashCode = (hashCode * 31) + Objects.hashCode(from);
      hashCode = (hashCode * 31) + at.hashCode();
      return hashCode;
    }
  }
}
