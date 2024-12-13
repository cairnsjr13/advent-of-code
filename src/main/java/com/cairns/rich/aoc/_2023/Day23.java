package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * We need to plan a path through a grid which maximizes our travel distance.
 * There are slopes which behave depending on the slipperiness of the part we are working on.
 */
class Day23 extends Base2023 {
  private static final Multimap<Character, ReadDir> slipperyPathOptions = HashMultimap.create();
  private static final Multimap<Character, ReadDir> stablePathOptions = HashMultimap.create();
  static {
    List<ReadDir> allDirs = Arrays.asList(EnumUtils.enumValues(ReadDir.class));
    slipperyPathOptions.putAll('.', allDirs);
    slipperyPathOptions.put('>', ReadDir.Right);
    slipperyPathOptions.put('<', ReadDir.Left);
    slipperyPathOptions.put('^', ReadDir.Up);
    slipperyPathOptions.put('v', ReadDir.Down);

    stablePathOptions.putAll('.', allDirs);
    stablePathOptions.putAll('<', allDirs);
    stablePathOptions.putAll('>', allDirs);
    stablePathOptions.putAll('^', allDirs);
    stablePathOptions.putAll('v', allDirs);
  }

  /**
   * Computes the longest path possible when slopes are slippery and force a direction.
   */
  @Override
  protected Object part1(Loader loader) {
    return maxPath(loader, slipperyPathOptions);
  }

  /**
   * Computes the longest path possible when slopes are stable and do NOT force a direction.
   */
  @Override
  protected Object part2(Loader loader) {
    return maxPath(loader, stablePathOptions);
  }

  /**
   * Computes the longest possible path for the input with the given path options.
   */
  private long maxPath(Loader loader, Multimap<Character, ReadDir> pathOptions) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    Set<ImmutablePoint> inflectionPoints = findInflectionPoints(grid);
    ImmutablePoint start = inflectionPoints.stream().filter((p) -> p.y() == 0).findFirst().get();
    ImmutablePoint end = inflectionPoints.stream().filter((p) -> p.y() == grid.length - 1).findFirst().get();
    Table<ImmutablePoint, ImmutablePoint, Long> distances = findDistances(grid, pathOptions, inflectionPoints);
    return maxPath(end, distances, new HashSet<>(), start, 0);
  }

  /**
   * Uses recursive backtracking to compute the longest path possible from the given at point to the desired end point.
   * This can be done efficiently by jumping straight to inflection points along the way instead of iterating through the grid.
   */
  private long maxPath(
      ImmutablePoint end,
      Table<ImmutablePoint, ImmutablePoint, Long> distances,
      Set<ImmutablePoint> visited,
      ImmutablePoint at,
      long total
  ) {
    if (at.equals(end)) {
      return total;
    }
    long maxPath = 0;
    Map<ImmutablePoint, Long> nexts = distances.row(at);
    for (ImmutablePoint next : nexts.keySet()) {
      if (visited.add(next)) {
        maxPath = Math.max(maxPath, maxPath(end, distances, visited, next, total + nexts.get(next)));
        visited.remove(next);
      }
    }
    return maxPath;
  }

  /**
   * Returns all interesting points in the graph.  A point is considered interesting if it is a slope character
   * or if it is a path character ('.') but has 1 direction (start/end) or 3/4 directions (fork in the road).
   */
  private Set<ImmutablePoint> findInflectionPoints(char[][] grid) {
    Set<ImmutablePoint> inflectionPoints = new HashSet<>();
    for (int y = 0; y < grid.length; ++y) {
      for (int x = 0; x < grid[0].length; ++x) {
        char ch = grid[y][x];
        if (ch == '.') {
          if (countValidDirs(grid, x, y) != 2) {
            inflectionPoints.add(new ImmutablePoint(x, y));
          }
        }
        else if (ch != '#') {
          inflectionPoints.add(new ImmutablePoint(x, y));
        }
      }
    }
    return inflectionPoints;
  }

  /**
   * Computes the minimum distance between each of the given inflection points following the rules of the given path options.
   */
  private Table<ImmutablePoint, ImmutablePoint, Long> findDistances(
      char[][] grid,
      Multimap<Character, ReadDir> pathOptions,
      Set<ImmutablePoint> inflectionPoints
  ) {
    Table<ImmutablePoint, ImmutablePoint, Long> distances = HashBasedTable.create();
    for (ImmutablePoint start : inflectionPoints) {
      bfs(
          start,
          (at) -> false,
          SearchState::getNumSteps,
          (at, numSteps, registrar) -> {
            if (inflectionPoints.contains(at) && (at != start)) {
              distances.put(start, at, numSteps);
            }
            else {
              for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
                if (isValidPath(grid, pathOptions, at.x(), at.y(), dir)) {
                  registrar.accept(at.move(dir));
                }
              }
            }
          }
      );
    }
    return distances;
  }

  /**
   * Returns the number of directions that can be moved in from the given location.  Slipperiness of slope is not considered.
   */
  private int countValidDirs(char[][] grid, int x, int y) {
    int numDirs = 0;
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      if (isValidPath(grid, stablePathOptions, x, y, dir)) {
        ++numDirs;
      }
    }
    return numDirs;
  }

  /**
   * Returns true if it is valid to move in the given direction from the given location according to the given options.
   */
  private boolean isValidPath(char[][] grid, Multimap<Character, ReadDir> pathOptions, int x, int y, ReadDir dir) {
    int nx = x + dir.dx();
    int ny = y + dir.dy();
    return Grid.isValid(grid, ny, nx) && pathOptions.get(grid[ny][nx]).contains(dir);
  }
}
