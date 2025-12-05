package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An elf wants to walk around and visit gardens with his steps.  We need to calculate the total number he can
 * reach with the number of steps he has left. Because going backwards is allowed, it is important to notice that
 * gardens will "flicker" on odd/even steps as they are revisited.  We can use this to speed up the simulation.
 */
class Day21 extends Base2023 {
  private static final ConfigToken<Long> numStepsToken = ConfigToken.of("numSteps", Long::parseLong);

  /**
   * Returns the number of gardens that are reachable with the configured number of steps.
   */
  @Override
  protected Object part1(Loader loader) {
    long numSteps = loader.getConfig(numStepsToken);
    return countGardensPerStep(false, loader, numSteps).get(numSteps);
  }

  /**
   * Uses the Lagrange Second Order Interpolation Formula to calculate the total number of gardens reachable on the infinite
   * map after the configured number of steps.  This works because the input map is special in that it is diamond shaped and
   * has a cyclic walk nature.  Because of this after 65 initial steps, the result cycles after 131 steps.  Lagrange shows us:
   *
   *              (x - x1) * (x - x2)            (x - x0) * (x - x2)            (x - x0) * (x - x1)
   * f(x) = y0 * ---------------------  +  y1 * ---------------------  +  y2 * ---------------------
   *             (x0 - x1) * (x0 - x2)          (x1 - x0) * (x1 - x2)          (x2 - x0) * (x2 - x1)
   *
   * f(x) here is the final number of gardens when we plug our numSteps in for x.
   */
  @Override
  protected Object part2(Loader loader) {
    long numSteps = loader.getConfig(numStepsToken);
    long cycle = 131;
    long initial = 65 + cycle;
    Map<Long, Long> gardensPerStep = countGardensPerStep(true, loader, initial + cycle);

    long x0 = initial - cycle;
    double y0 = gardensPerStep.get(x0);
    long x1 = initial;
    double y1 = gardensPerStep.get(x1);
    long x2 = initial + cycle;
    double y2 = gardensPerStep.get(x2);

    Double numGardens = y0 * (((numSteps - x1) * (numSteps - x2)) / ((x0 - x1) * (x0 - x2)))
                      + y1 * (((numSteps - x0) * (numSteps - x2)) / ((x1 - x0) * (x1 - x2)))
                      + y2 * (((numSteps - x0) * (numSteps - x1)) / ((x2 - x0) * (x2 - x1)));
    return numGardens.longValue();    // Prevent scientific toString
  }

  /**
   * Computes the number of gardens reachable for each step up to and including the given number of steps.
   * A key insight here is that once a garden is included, it will always be included on alternating steps
   * due to backwards movement.  We can exclude old locations from next loops as they will result in the
   * same thing.  We can use odd/even mods to keep track of the seen gardens at each step.
   */
  private Map<Long, Long> countGardensPerStep(boolean isInfinite, Loader loader, long numSteps) {
    char[][] grid = Grid.parseChars(loader);
    Map<Long, Long> gardensPerStep = new HashMap<>();
    List<Set<ImmutablePoint>> permanents = List.of(new HashSet<>(), new HashSet<>());
    List<Set<ImmutablePoint>> currents = List.of(new HashSet<>(), new HashSet<>());
    ImmutablePoint start = findStart(grid);
    permanents.get(0).add(start);
    currents.get(0).add(start);
    for (long step = 1; step <= numSteps; ++step) {
      safeGet(currents, step).clear();
      for (ImmutablePoint at : safeGet(currents, step - 1)) {
        for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
          if (canGo(isInfinite, grid, at, dir)) {
            ImmutablePoint next = at.move(dir);
            if (safeGet(permanents, step).add(next)) {
              safeGet(currents, step).add(next);
            }
          }
        }
      }
      gardensPerStep.put(step, (long) safeGet(permanents, step).size());
    }
    return gardensPerStep;
  }

  /**
   * Returns true if the given direction is movable from the given location.  A direction is movable
   * if the grid is infinite or the location is on the grid as well as the spot being open ('.').
   */
  private boolean canGo(boolean isInfinite, char[][] grid, ImmutablePoint at, ReadDir dir) {
    int nx = at.x() + dir.dx();
    int ny = at.y() + dir.dy();
    return (isInfinite || Grid.isValid(grid, ny, nx))
        && (safeGet(safeGet(grid, ny), nx) == '.');
  }

  /**
   * Finds the 'S' location in the input grid and reset's the location to be '.'.
   */
  private ImmutablePoint findStart(char[][] grid) {
    for (int y = 0; y < grid.length; ++y) {
      for (int x = 0; x < grid[0].length; ++x) {
        if (grid[y][x] == 'S') {
          grid[y][x] = '.';
          return new ImmutablePoint(x, y);
        }
      }
    }
    throw fail();
  }
}
