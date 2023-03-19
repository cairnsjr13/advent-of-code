package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

class Day20 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    return getMinSteps(loader, false);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getMinSteps(loader, true);
  }

  private long getMinSteps(Loader2 loader, boolean recursive) {
    State state = buildState(loader.ml(String::toCharArray).toArray(char[][]::new));
    WalkDesc init = new WalkDesc(0, state.start, 0);
    PriorityQueue<WalkDesc> candidates = new PriorityQueue<>(Comparator.comparing((ws) -> ws.numSteps));
    Map<WalkDesc, Long> visitedInSteps = new HashMap<>();
    candidates.add(init);
    visitedInSteps.put(init, init.numSteps);
    while (!candidates.isEmpty()) {   // TODO: extended bfs... not sure why i cant get it to work
      WalkDesc current = candidates.poll();
      if (state.end.equals(current.location) && (0 == current.depth)) {
        return current.numSteps - 1;  // dont need to move THROUGH ZZ
      }
      state.paths.row(current.location).forEach((to, path) -> {
        WalkDesc next = new WalkDesc(
            current.depth + ((recursive) ? path.levelDelta : 0),
            to,
            current.numSteps + path.stepsBetween + 1
        );
        if (!visitedInSteps.containsKey(next) || (next.numSteps < visitedInSteps.get(next))) {
          if (0 <= next.depth) {
            visitedInSteps.put(next, next.numSteps);
            candidates.add(next);
          }
        }
      });
    }
    throw fail();
  }

  private State buildState(char[][] grid) {
    Map<String, ImmutablePoint> markers = new HashMap<>();
    Map<ImmutablePoint, ImmutablePoint> jumps = new HashMap<>();
    buildMarkersAndJumps(grid, markers, jumps);
    ImmutablePoint start = markers.get("AA");
    ImmutablePoint end = markers.get("ZZ");
    jumps.put(start, start);
    jumps.put(end, end);
    Table<ImmutablePoint, ImmutablePoint, PathDesc> paths = HashBasedTable.create();
    jumps.keySet().forEach((from) -> addAllPathsFrom(grid, start, end, paths, jumps, from));
    return new State(start, end, paths);
  }

  private void buildMarkersAndJumps(
      char[][] grid,
      Map<String, ImmutablePoint> markers,
      Map<ImmutablePoint, ImmutablePoint> jumps
   ) {
    for (int y = 1; y < grid.length - 1; ++y) {
      for (int x = 1; x < grid[0].length - 1; ++x) {
        if (Character.isUpperCase(grid[y][x])) {
          Optional<ReadDir> dirToPathSpaceOpt = findDirToPathSpace(grid, x, y);
          if (dirToPathSpaceOpt.isPresent()) {
            ReadDir dirToPathSpace = dirToPathSpaceOpt.get();
            String jumpMarker = computeJumpMarker(grid, x, y, dirToPathSpace.turnAround());
            ImmutablePoint jumpFrom = new ImmutablePoint(x + dirToPathSpace.dx(), y + dirToPathSpace.dy());
            ImmutablePoint jumpTo = markers.put(jumpMarker, jumpFrom);
            if (jumpTo != null) {
              jumps.put(jumpFrom, jumpTo);
              jumps.put(jumpTo, jumpFrom);
            }
          }
        }
      }
    }
  }

  private Optional<ReadDir> findDirToPathSpace(char[][] grid, int x, int y) {
    return Arrays.stream(EnumUtils.enumValues(ReadDir.class))
        .filter((dir) -> grid[y + dir.dy()][x + dir.dx()] == '.')
        .findFirst();
  }

  private String computeJumpMarker(char[][] grid, int x, int y, ReadDir otherLetterDir) {
    char nearPathSpace = grid[y][x];
    char awayPathSpace = grid[y + otherLetterDir.dy()][x + otherLetterDir.dx()];
    return new String(((otherLetterDir == ReadDir.Left) || (otherLetterDir == ReadDir.Up))
        ? new char[] { awayPathSpace, nearPathSpace }
        : new char[] { nearPathSpace, awayPathSpace });
  }

  private void addAllPathsFrom(
      char[][] grid,
      ImmutablePoint start,
      ImmutablePoint end,
      Table<ImmutablePoint, ImmutablePoint, PathDesc> paths,
      Map<ImmutablePoint, ImmutablePoint> jumps,
      ImmutablePoint from
  ) {
    Set<ImmutablePoint> visited = new HashSet<>();
    Queue<Pair<ImmutablePoint, Integer>> candidates = new ArrayDeque<>();
    visited.add(from);
    visited.add(start);
    candidates.add(Pair.of(from, 0));
    while (!candidates.isEmpty()) {   // TODO: Extended bfs.  not sure why i cant get it to work
      Pair<ImmutablePoint, Integer> current = candidates.poll();
      ImmutablePoint location = current.getLeft();
      int numSteps = current.getRight();
      for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
        if (grid[location.y() + dir.dy()][location.x() + dir.dx()] == '.') {
          ImmutablePoint next = location.move(dir);
          if (!visited.contains(next)) {
            visited.add(next);
            if (jumps.keySet().contains(next)) {
              paths.put(from, jumps.get(next), new PathDesc(numSteps + 1, getLevelDelta(end, grid, next)));
            }
            else {
              candidates.add(Pair.of(next, numSteps + 1));
            }
          }
        }
      }
    }
  }

  private int getLevelDelta(ImmutablePoint end, char[][] grid, ImmutablePoint to) {
    boolean isOuterJump =
        (to.y() == 2) || (to.y() == grid.length - 3) || (to.x() == 2) || (to.x() == grid[0].length - 3);
    return (end.equals(to)) ? 0 : ((isOuterJump) ? -1 : 1);
  }

  private static class State {
    private final ImmutablePoint start;
    private final ImmutablePoint end;
    private final Table<ImmutablePoint, ImmutablePoint, PathDesc> paths;

    private State(ImmutablePoint start, ImmutablePoint end, Table<ImmutablePoint, ImmutablePoint, PathDesc> paths) {
      this.start = start;
      this.end = end;
      this.paths = paths;
    }
  }

  private static class PathDesc {
    private final long stepsBetween;
    private final int levelDelta;

    private PathDesc(long stepsBetween, int levelDelta) {
      this.stepsBetween = stepsBetween;
      this.levelDelta = levelDelta;
    }
  }

  private static class WalkDesc {
    private final int depth;
    private final ImmutablePoint location;
    private final long numSteps;

    private WalkDesc(int depth, ImmutablePoint location, long numSteps) {
      this.depth = depth;
      this.location = location;
      this.numSteps = numSteps;
    }

    @Override
    public boolean equals(Object other) {
      return (depth == ((WalkDesc) other).depth)
          && location.equals(((WalkDesc) other).location);
    }

    @Override
    public int hashCode() {
      return depth ^ location.hashCode();
    }
  }
}
