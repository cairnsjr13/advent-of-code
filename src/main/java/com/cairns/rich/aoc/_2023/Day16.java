package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import java.util.List;
import java.util.stream.IntStream;

/**
 * We need to simulate a beam of light bouncing off of mirrors and through splitters.
 * The beam of light will energize locations and help us maximize the energy.
 */
class Day16 extends Base2023 {
  private static final Table<ReadDir, Character, List<ReadDir>> nextDirs = computeNextDirsLookup();

  /**
   * Computes the number of energized locations when a beam of light enters at (0, 0) traveling {@link ReadDir#Right}.
   */
  @Override
  protected Object part1(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    return getNumEnergized(grid, 0, 0, ReadDir.Right);
  }

  /**
   * Computes the maximum number of energized locations possible if a beam is allowed to start at any of the edge
   * locations traveling away from that edge.  Corners will end up having both directions computed as initial headings.
   */
  @Override
  protected Object part2(Loader loader) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    return Math.max(
        IntStream.range(0, grid.length).map((y) -> Math.max(
            getNumEnergized(grid, 0, y, ReadDir.Right),
            getNumEnergized(grid, grid[0].length - 1, y, ReadDir.Left)
        )).max().getAsInt(),
        IntStream.range(0, grid[0].length).map((x) -> Math.max(
            getNumEnergized(grid, x, 0, ReadDir.Down),
            getNumEnergized(grid, x, grid.length - 1, ReadDir.Up)
        )).max().getAsInt()
    );
  }

  /**
   * Uses breadth first search to simulate a beam of light bouncing around the object grid.
   * Will return the total number of unique locations that had a beam of light pass through.
   */
  private int getNumEnergized(char[][] grid, int startX, int startY, ReadDir startDir) {
    Multimap<ImmutablePoint, ReadDir> visited = HashMultimap.create();
    bfs(
        new LocationAndDir(new ImmutablePoint(startX, startY), startDir),
        (ss) -> false,
        SearchState::getNumSteps,
        (cur, registrar) -> {
          if (cur.valid(grid)) {
            visited.put(cur.location, cur.dir);
            char ch = grid[cur.location.y()][cur.location.x()];
            if (!nextDirs.contains(cur.dir, ch)) {
              throw fail(cur.location + " on " + ch + " going " + cur.dir);
            }
            nextDirs.get(cur.dir, ch).forEach((nextDir) -> registrar.accept(cur.move(nextDir)));
          }
        }
    );
    return visited.keySet().size();
  }

  /**
   * Computes the lookup map for light heading in a certain direction and encountering various objects.
   *   - Empty spaces ('.') will cause a light to pass through in the same direction it entered.
   *   - Light encountering mirrors ('/' and '\') will reflect according to the 90 degree bounce.
   *     For example, {@link ReadDir#Right} hitting a '/' will change direction to travel {@link ReadDir#Up}.
   *   - Splitters ('-' and '|') act as empty spaces if the light encounters the pointy end.
   *     For example, {@link ReadDir#Up} hitting a '|' will continue to travel {@link ReadDir#Up}.
   *   - Splitters will cause two beams of light to be created if the light encounters the flat end.
   *     For example, {@link ReadDir#Left} hitting a '|' will cause two beams of light to travel
   *     {@link ReadDir#Up} and {@link ReadDir#Down} at the same time.
   *
   * Because splitters can cause one beam of light to create two, the value of the lookup map will be a list of all output dirs.
   */
  private static Table<ReadDir, Character, List<ReadDir>> computeNextDirsLookup() {
    Table<ReadDir, Character, List<ReadDir>> nextDirs = HashBasedTable.create();
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      nextDirs.put(dir, '.', List.of(dir));
    }

    nextDirs.put(ReadDir.Up, '/', List.of(ReadDir.Right));
    nextDirs.put(ReadDir.Right, '/', List.of(ReadDir.Up));
    nextDirs.put(ReadDir.Down, '/', List.of(ReadDir.Left));
    nextDirs.put(ReadDir.Left, '/', List.of(ReadDir.Down));

    nextDirs.put(ReadDir.Up, '\\', List.of(ReadDir.Left));
    nextDirs.put(ReadDir.Left, '\\', List.of(ReadDir.Up));
    nextDirs.put(ReadDir.Down, '\\', List.of(ReadDir.Right));
    nextDirs.put(ReadDir.Right, '\\', List.of(ReadDir.Down));

    nextDirs.put(ReadDir.Up, '|', List.of(ReadDir.Up));
    nextDirs.put(ReadDir.Down, '|', List.of(ReadDir.Down));
    nextDirs.put(ReadDir.Left, '|', List.of(ReadDir.Up, ReadDir.Down));
    nextDirs.put(ReadDir.Right, '|', List.of(ReadDir.Up, ReadDir.Down));

    nextDirs.put(ReadDir.Up, '-', List.of(ReadDir.Left, ReadDir.Right));
    nextDirs.put(ReadDir.Down, '-', List.of(ReadDir.Left, ReadDir.Right));
    nextDirs.put(ReadDir.Left, '-', List.of(ReadDir.Left));
    nextDirs.put(ReadDir.Right, '-', List.of(ReadDir.Right));
    return nextDirs;
  }

  /**
   * Container class used to describe the current location and direction of a beam of light.
   */
  private static class LocationAndDir {
    private final ImmutablePoint location;
    private final ReadDir dir;

    private LocationAndDir(ImmutablePoint location, ReadDir dir) {
      this.location = location;
      this.dir = dir;
    }

    /**
     * Helper method to determine if this location is valid on the given grid.
     */
    private boolean valid(char[][] grid) {
      return (0 <= location.x()) && (location.x() < grid[0].length) && (0 <= location.y()) && (location.y() < grid.length);
    }

    /**
     * Helper method to construct a new {@link LocationAndDir} with this location moved in the given direction.
     */
    private LocationAndDir move(ReadDir moveDir) {
      return new LocationAndDir(location.move(moveDir), moveDir);
    }

    @Override
    public boolean equals(Object other) {
      return location.equals(((LocationAndDir) other).location)
          && dir.equals(((LocationAndDir) other).dir);
    }

    @Override
    public int hashCode() {
      return 31 * location.hashCode() + dir.hashCode();
    }
  }
}
