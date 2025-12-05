package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.Set;

/**
 * The historians need help avoiding a guard that is patrolling the lab.
 */
class Day06 extends Base2024 {
  /**
   * We need to figure out how many unique locations the guard visits (including the start).
   */
  @Override
  protected Object part1(Loader loader) {
    char[][] grid = Grid.parseChars(loader);
    Multimap<ImmutablePoint, ReadDir> visited = HashMultimap.create();
    findAllPointsAndDirsAndIfLoops(grid, findStart(grid), visited);
    return visited.keySet().size();
  }

  /**
   * We need to find the number of locations that we can add an object to to cause the guard to get
   * caught in a loop.  This can be found by adding an object at each location along the original path
   * and rerunning the simulation to see if the guard returns to a possition/direction (a loop).
   */
  @Override
  protected Object part2(Loader loader) {
    char[][] grid = Grid.parseChars(loader);
    ImmutablePoint startAt = findStart(grid);
    ImmutablePoint inFrontOfGuard = startAt.move(ReadDir.Up);
    Multimap<ImmutablePoint, ReadDir> pathPoints = HashMultimap.create();
    findAllPointsAndDirsAndIfLoops(grid, startAt, pathPoints);

    Set<ImmutablePoint> newLoopObstacles = new HashSet<>();
    for (ImmutablePoint visitedPoint : pathPoints.keySet()) {
      for (ReadDir visitedDir : pathPoints.get(visitedPoint)) {
        ImmutablePoint newObstacleAt = visitedPoint.move(visitedDir);
        if (   Grid.isValid(grid, newObstacleAt)
            && !startAt.equals(newObstacleAt)
            && !inFrontOfGuard.equals(newObstacleAt)
            && !newLoopObstacles.contains(newObstacleAt)
            && (grid[newObstacleAt.y()][newObstacleAt.x()] != '#')
        ) {
          grid[newObstacleAt.y()][newObstacleAt.x()] = '#';
          if (findAllPointsAndDirsAndIfLoops(grid, startAt, HashMultimap.create())) {
            newLoopObstacles.add(newObstacleAt);
          }
          grid[newObstacleAt.y()][newObstacleAt.x()] = '.';
        }
      }
    }
    return newLoopObstacles.size();
  }

  /**
   * Simulates a guard walking around the grid, adding each visited location to the given multimap.
   * Completes when either:
   *   - the guard leaves the grid -> returns false
   *   - the guard loops -> returns true
   */
  private boolean findAllPointsAndDirsAndIfLoops(
      char[][] grid,
      ImmutablePoint guardLocation,
      Multimap<ImmutablePoint, ReadDir> visited
  ) {
    ReadDir guardDir = ReadDir.Up;
    while (true) {
      visited.put(guardLocation, guardDir);
      ImmutablePoint next = guardLocation.move(guardDir);
      if (!Grid.isValid(grid, next)) {
        return false;
      }
      if (visited.containsEntry(next, guardDir)) {
        return true;
      }
      if (grid[next.y()][next.x()] == '.') {
        guardLocation = next;
      }
      else {
        guardDir = guardDir.turnRight();
      }
    }
  }

  /**
   * Returns the {@link ImmutablePoint} that the guard starts at facing {@link ReadDir#Up}.
   * The guard is in the grid indicated with a '^'.  This method will replace the guard with a '.'.
   */
  private ImmutablePoint findStart(char[][] grid) {
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        if (grid[row][col] == '^') {
          grid[row][col] = '.';
          return new ImmutablePoint(col, row);
        }
      }
    }
    throw fail();
  }
}
