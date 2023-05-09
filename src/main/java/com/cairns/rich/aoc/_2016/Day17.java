package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * We need to access a vault ina relatively small building footprint.  However, all of the doors are locked/unlocked
 * based on the path we took to get there.  Path searching will let us find the shortest and longest paths.
 */
class Day17 extends Base2016 {
  private static final ImmutablePoint target = new ImmutablePoint(3, 3);
  private static final Set<Character> openChars = Set.of('b', 'c', 'd', 'e', 'f');
  private static final EnumMap<ReadDir, Integer> dirToIndex = new EnumMap<>(Map.of(
      ReadDir.Up, 0,
      ReadDir.Down, 1,
      ReadDir.Left, 2,
      ReadDir.Right, 3
  ));

  /**
   * Returns the shortest path that will get to the vault.
   * The path is represented with the first char of each move.
   */
  @Override
  protected Object part1(Loader loader) {
    String prefix = loader.sl();
    return bfs(
        new State(prefix, ImmutablePoint.origin, new ArrayList<>()),
        (s) -> s.location.equals(target),
        (ss) -> ss.state.path.size(),
        this::explore
    ).get().state.path.stream().map(Object::toString).collect(Collectors.joining());
  }

  /**
   * Returns the maximum length of a path that can reach the vault.
   * We can find this by never terminating a bfs and detecting when we have reached the vault.
   */
  @Override
  protected Object part2(Loader loader) {
    String prefix = loader.sl();
    MutableInt maxLength = new MutableInt(0);
    bfs(
        new State(prefix, ImmutablePoint.origin, new ArrayList<>()),
        (s) -> false,
        (ss) -> ss.state.path.size(),
        (candidate, registrar) -> {
          if (!candidate.location.equals(target)) {
            explore(candidate, registrar);
          }
          else if (maxLength.getValue() < candidate.path.size()) {
            maxLength.setValue(candidate.path.size());
          }
        }
    );
    return maxLength;
  }

  /**
   * Registers all of the valid moves from the current location.
   */
  private void explore(State candidate, Consumer<State> registrar) {
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      if (candidate.canGo(dir)) {
        registrar.accept(candidate.move(dir));
      }
    }
  }

  /**
   * A container class describing the current state of a path.  Doors open and close based on
   * the previous path, so simply being in a location does not fully describe a unique state.
   */
  private static class State {
    private final String prefix;
    private final ImmutablePoint location;
    private final List<Character> path;
    private final boolean[] doorsOpen;

    private State(String prefix, ImmutablePoint location, List<Character> path) {
      this.prefix = prefix;
      this.location = location;
      this.path = path;
      this.doorsOpen = getDoorsOpen();
    }

    /**
     * Returns true if the given move is in bounds and that door is unlocked.
     */
    private boolean canGo(ReadDir dir) {
      int nx = location.x() + dir.dx();
      int ny = location.y() + dir.dy();
      return (0 <= nx) && (nx < 4)
          && (0 <= ny) && (ny < 4)
          && doorsOpen[dirToIndex.get(dir)];
    }

    /**
     * Creates a new {@link State} with the given move appended to this {@link State}.
     */
    private State move(ReadDir go) {
      List<Character> newPath = new ArrayList<>(path);
      newPath.add(go.name().charAt(0));
      return new State(prefix, location.move(go), newPath);
    }

    /**
     * Computes the doors that are open based upon the existing path taken.
     * A door is considered open if its corresponding index in the md5 hash is in {@link Day17#openChars}.
     */
    private boolean[] getDoorsOpen() {
      boolean[] doorsOpen = new boolean[4];
      String hash = md5(prefix + path.stream().map(Object::toString).collect(Collectors.joining()));
      for (int i = 0; i < doorsOpen.length; ++i) {
        doorsOpen[i] = openChars.contains(hash.charAt(i));
      }
      return doorsOpen;
    }

    @Override
    public boolean equals(Object other) {
      return path.equals(((State) other).path);
    }

    @Override
    public int hashCode() {
      return path.hashCode();
    }
  }
}
