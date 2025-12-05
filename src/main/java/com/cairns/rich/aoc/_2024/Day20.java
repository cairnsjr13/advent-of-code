package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.HashMap;
import java.util.Map;

class Day20 extends Base2024 {
  private static final ConfigToken<Integer> cheatThresholdToken = ConfigToken.of("cheatThreshold", Integer::parseInt);

  /**
   * Cheats are allowed to move through exactly one '#' space (or a cheat distance of 2).
   */
  @Override
  protected Object part1(Loader loader) {
    return numImprovements(loader, 2);
  }

  /**
   * Cheats are allowed to be at most a distance of 20.
   */
  @Override
  protected Object part2(Loader loader) {
    return numImprovements(loader, 20);
  }

  /**
   * Returns the number of cheats that have an improvement at least as large as the configured threshold.
   *
   * At its core, a "cheat" is simply two open spaces ('.') that a racer can take a direct path to without worrying
   * about blocked/open status.  The algorithm here considers every open space as a "start" position for a cheat
   * and tries every open space within maxCheatDist as the "end" position.  The distance required using a cheat
   * can be computed by adding together the best distance to the start of the cheat, the length of the cheat, and
   * the best distance from the end of the cheat to the goal.
   *
   * We can dramatically reduce the runtime of this algorithm by limiting the end position search to having at
   * least one of its directions in the threshold range.  While this is still larger than required because of the
   * angled edges, it reduces the search range to dist*dist instead of the entire n*n grid.
   */
  private int numImprovements(Loader loader, int maxCheatDist) {
    int cheatThreshold = loader.getConfig(cheatThresholdToken);
    char[][] grid = Grid.parseChars(loader);
    ImmutablePoint start = Grid.findAndReplace(grid, 'S', '.');
    ImmutablePoint end = Grid.findAndReplace(grid, 'E', '.');
    Map<ImmutablePoint, Long> bestNoCheatFrom = computeBestNoCheatStartingAt(grid, end);
    Map<ImmutablePoint, Long> bestNoCheatTo = computeBestNoCheatStartingAt(grid, start);
    long bestNoCheat = bestNoCheatFrom.get(start);

    int numCheatsAtOrAboveThreshold = 0;
    for (int startRow = 1; startRow < grid.length - 1; ++startRow) {
      for (int startCol = 1; startCol < grid[0].length - 1; ++startCol) {
        ImmutablePoint startCheat = new ImmutablePoint(startCol, startRow);
        if (isExpected(grid, startCheat, '.')) {
          int minEndRow = Math.max(1, startRow - maxCheatDist);
          int maxEndRow = Math.min(grid.length - 2, startRow + maxCheatDist);
          int minEndCol = Math.max(1, startCol - maxCheatDist);
          int maxEndCol = Math.min(grid[0].length - 2, startCol + maxCheatDist);
          for (int endRow = minEndRow; endRow <= maxEndRow; ++endRow) {
            for (int endCol = minEndCol; endCol <= maxEndCol; ++endCol) {
              ImmutablePoint endCheat = new ImmutablePoint(endCol, endRow);
              long cheatDist = Grid.manhattanDist(startCheat, endCheat);
              if (cheatDist <= maxCheatDist) {
                if (isExpected(grid, endCheat, '.')) {
                  long timeWithCheat = bestNoCheatTo.get(startCheat)
                                     + cheatDist
                                     + bestNoCheatFrom.get(endCheat);
                  if (bestNoCheat - timeWithCheat >= cheatThreshold) {
                    ++numCheatsAtOrAboveThreshold;
                  }
                }
              }
            }
          }
        }
      }
    }
    return numCheatsAtOrAboveThreshold;
  }

  /**
   * Using the given start point, computes a map of every reachable location from that point and the distance required.
   */
  private Map<ImmutablePoint, Long> computeBestNoCheatStartingAt(char[][] grid, ImmutablePoint from) {
    Map<ImmutablePoint, Long> bestNoCheat = new HashMap<>();
    bfs(
        from,
        (cur) -> false,
        SearchState::getNumSteps,
        (cur, numSteps, registrar) -> {
          bestNoCheat.put(cur, numSteps);
          for (ReadDir move : EnumUtils.enumValues(ReadDir.class)) {
            ImmutablePoint next = cur.move(move);
            if (Grid.isValid(grid, next) && isExpected(grid, next, '.')) {
              registrar.accept(next);
            }
          }
        }
    );
    return bestNoCheat;
  }

  /**
   * Helper method to return if the given point in the grid has the given expected value.
   */
  private boolean isExpected(char[][] grid, ImmutablePoint at, char expect) {
    return grid[at.y()][at.x()] == expect;
  }
}
