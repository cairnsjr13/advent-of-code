package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * There is an animal scurrying through some metallic pipes and we need to figure out how large the loop is as well
 * as the area contained within the loop.  The connections are represented by various characters indicating direction
 * change and there are lots of unrelated pipes on the map.  We can follow the actual path by starting at 'S'.
 */
class Day10 extends Base2023 {
  private static final Map<ReadDir, Character> extensionChars =
      Map.of(ReadDir.Left, '-', ReadDir.Right, '-', ReadDir.Up, '|', ReadDir.Down, '|');
  private static final Table<ReadDir, Character, ReadDir> dirGos = HashBasedTable.create();
  static {  // (beforeDir, pipeEncountered, afterDir)
    dirGos.put(ReadDir.Up, '|', ReadDir.Up);
    dirGos.put(ReadDir.Up, '7', ReadDir.Left);
    dirGos.put(ReadDir.Up, 'F', ReadDir.Right);
    dirGos.put(ReadDir.Down, '|', ReadDir.Down);
    dirGos.put(ReadDir.Down, 'J', ReadDir.Left);
    dirGos.put(ReadDir.Down, 'L', ReadDir.Right);
    dirGos.put(ReadDir.Left, '-', ReadDir.Left);
    dirGos.put(ReadDir.Left, 'F', ReadDir.Down);
    dirGos.put(ReadDir.Left, 'L', ReadDir.Up);
    dirGos.put(ReadDir.Right, '-', ReadDir.Right);
    dirGos.put(ReadDir.Right, 'J', ReadDir.Up);
    dirGos.put(ReadDir.Right, '7', ReadDir.Down);
  }

  /**
   * Computes the farthest number of steps the animal can get by simply finding the length of the loop and dividing by 2.
   */
  @Override
  protected Object part1(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    return findLoop(grid).size() / 2;
  }

  /**
   * Computes the total number of map spaces that are bounded by the main loop.  Because "inside" is defined in a way
   * that allows escape between touching pipes, we have to double and extend the grid to allow for a traditional graph
   * search.  By constructing a second, larger grid, we can then determine if a spot is "outside" by finding the
   * connected component it belongs to, and determining if any of the spaces in that component touch an exterior edge
   * of the map.  We can then map the original map to the larger map and determine which of the spaces count as inside.
   */
  @Override
  protected Object part2(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    Set<Point<?>> dGridInsides = findDoubleGridInsides(grid);
    int totalInside = 0;
    for (int y = 0; y < grid.length; ++y) {
      for (int x = 0; x < grid[0].length; ++x) {
        if (dGridInsides.contains(new ImmutablePoint(x * 2, y * 2))) {
          ++totalInside;
        }
      }
    }
    return totalInside;
  }

  /**
   * Doubles the given grid and computes the locations (of the doubled grid) that are considered to be "inside".
   * This is done by doubling the width/height of the grid and appropriately extending all of the pipes.  This
   * ensures there is a discrete space between pipes that are next to each other.  From here, we just do a graph
   * search to find all locations that can touch an edge of the graph.  Any components that can't, are "inside".
   */
  private Set<Point<?>> findDoubleGridInsides(char[][] grid) {
    Set<Point<?>> loop = findLoop(grid);
    char[][] dGrid = buildDoubledGrid(grid, loop);
    Set<Point<?>> insides = new HashSet<>();
    Set<ImmutablePoint> visited = new HashSet<>();
    for (int y = 0; y < dGrid.length; ++y) {
      for (int x = 0; x < dGrid[0].length; ++x) {
        ImmutablePoint from = new ImmutablePoint(x, y);
        if (!visited.contains(from) && spot(dGrid, from) == '.') {
          Set<ImmutablePoint> component = connectAll(dGrid, from);
          visited.addAll(component);
          if (!isOutside(component, dGrid)) {
            insides.addAll(component);
          }
        }
      }
    }
    return insides;
  }

  /**
   * Constructs a grid that is twice the width and height of the given original grid.  This will allow space
   * between pipes to have at least one discrete grid space between them, making it possible to use bfs to
   * find inside/ouside components.  We need to extend pipe spaces to ensure the loop remains closed.  This
   * is done by adding either '-' or '|' to both sides of each real pipe, depending which direction it connects.
   */
  private char[][] buildDoubledGrid(char[][] grid, Set<Point<?>> loop) {
    char[][] dGrid = new char[grid.length * 2][grid[0].length * 2];
    Arrays.stream(dGrid).forEach((r) -> Arrays.fill(r, '.'));
    for (MutablePoint gridPoint = MutablePoint.origin(); gridPoint.y() < grid.length; gridPoint.move(ReadDir.Down)) {
      for (gridPoint.x(0); gridPoint.x() < grid[0].length; gridPoint.move(ReadDir.Right)) {
        if (loop.contains(gridPoint)) {
          char orig = spot(grid, gridPoint);
          dGrid[2 * gridPoint.y()][2 * gridPoint.x()] = orig;
          dirGos.column(orig).values().forEach((eDir) -> extendIfOnBoard(dGrid, 2 * gridPoint.x(), 2 * gridPoint.y(), eDir));
        }
      }
    }
    return dGrid;
  }

  /**
   * Writes the correct extension character to the grid (if it is on the grid) based on the directions the pipe connects.
   */
  private void extendIfOnBoard(char[][] dGrid, int nx, int ny, ReadDir extensionDir) {
    int ex = nx + extensionDir.dx();
    int ey = ny + extensionDir.dy();
    if ((0 <= ex) && (ex < dGrid[0].length) && (0 <= ey) && (ey < dGrid.length)) {
      dGrid[ey][ex] = extensionChars.get(extensionDir);
    }
  }

  /**
   * Returns all of the locations that are open ('.') and reachable from the given from location.
   */
  private Set<ImmutablePoint> connectAll(char[][] dGrid, ImmutablePoint from) {
    Set<ImmutablePoint> component = new HashSet<>();
    bfs(from, (ss) -> false, SearchState::getNumSteps, (at, registrar) -> {
      for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
        ImmutablePoint next = at.move(dir);
        if (spot(dGrid, next) == '.') {
          registrar.accept(next);
          component.add(next);
        }
      }
    });
    return component;
  }

  /**
   * Returns true if the given component is "outside", meaning that any location in the component is on the edge of the grid.
   */
  private boolean isOutside(Set<ImmutablePoint> component, char[][] dGrid) {
    return component.stream().anyMatch(
        (point) -> (point.x() == 0) || (point.y() == 0) || (point.x() == dGrid[0].length - 1) || (point.y() == dGrid.length - 1)
    );
  }

  /**
   * Helper method to return the char value of the given location on the grid.  Will return ' ' if the space is off the grid.
   */
  private char spot(char[][] grid, Point<?> location) {
    return ((0 <= location.x()) && (location.x() < grid[0].length) && (0 <= location.y()) && (location.y() < grid.length))
         ? grid[location.y()][location.x()]
         : ' ';
  }

  /**
   * Returns the main loop that starts/ends at the 'S' location in the given grid.
   * Only pipe locations in the main loop will be included, all others will be ignored.
   */
  private Set<Point<?>> findLoop(char[][] grid) {
    Point<?> start = findS(grid);
    ReadDir dir = findStartDir(grid, start);
    Set<Point<?>> loop = new HashSet<>();
    MutablePoint location = new MutablePoint(start);
    while (true) {
      loop.add(new ImmutablePoint(location));
      location.move(dir);
      if (start.equals(location)) {
        return loop;
      }
      dir = dirGos.get(dir, spot(grid, location));
    }
  }

  /**
   * Searches the grid to find the location where the animal starts scurrying ('S' on the grid).
   */
  private ImmutablePoint findS(char[][] grid) {
    for (int y = 0; y < grid.length; ++y) {
      for (int x = 0; x < grid[0].length; ++x) {
        if (grid[y][x] == 'S') {
          return new ImmutablePoint(x, y);
        }
      }
    }
    throw fail();
  }

  /**
   * Finds either of the directions that can be moved from the start location.
   */
  private ReadDir findStartDir(char[][] grid, Point<?> start) {
    for (ReadDir dir : dirGos.rowKeySet()) {
      if (dirGos.contains(dir, spot(grid, start.move(dir)))) {
        return dir;
      }
    }
    throw fail();
  }
}
