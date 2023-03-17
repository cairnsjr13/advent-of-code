package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
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

class Day17 extends Base2016 {
  private static final ImmutablePoint target = new ImmutablePoint(3, 3);
  private static final Set<Character> openChars = Set.of('b', 'c', 'd', 'e', 'f');
  private static final EnumMap<ReadDir, Integer> dirToIndex = new EnumMap<>(Map.of(
      ReadDir.Up, 0,
      ReadDir.Down, 1,
      ReadDir.Left, 2,
      ReadDir.Right, 3
  ));

  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    String prefix = loader.sl();
    result.part1(getShortestPath(prefix));
    result.part2(getLengthOfLongestPath(prefix));
  }

  private String getShortestPath(String prefix) {
    return bfs(
        new State(prefix, new ImmutablePoint(0, 0), new ArrayList<>()),
        (s) -> s.location.equals(target),
        (ss) -> ss.state.path.size(),
        this::explore
    ).get().state.path.stream().map(Object::toString).collect(Collectors.joining());
  }

  private int getLengthOfLongestPath(String prefix) {
    MutableInt maxLength = new MutableInt(0);
    bfs(
        new State(prefix, new ImmutablePoint(0, 0), new ArrayList<>()),
        (s) -> false,
        (ss) -> ss.state.path.size(),
        (candidate, registrar) -> {
          if (candidate.location.equals(target)) {
            if (maxLength.getValue() < candidate.path.size()) {
              maxLength.setValue(candidate.path.size());
            }
            return;
          }
          explore(candidate, registrar);
        }
    );
    return maxLength.getValue();
  }

  private void explore(State candidate, Consumer<State> registrar) {
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      if (canGo(candidate, dir)) {
        registrar.accept(candidate.move(dir));
      }
    }
  }

  private boolean canGo(State current, ReadDir dir) {
    int nx = current.location.x() + dir.dx();
    int ny = current.location.y() + dir.dy();
    return (0 <= nx) && (nx < 4)
        && (0 <= ny) && (ny < 4)
        && current.doorsOpen[dirToIndex.get(dir)];
  }

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

    private State move(ReadDir go) {
      List<Character> newPath = new ArrayList<>(path);
      newPath.add(go.name().charAt(0));
      return new State(prefix, location.move(go), newPath);
    }

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
      return location.equals(((State) other).location)
          && path.equals(((State) other).path);
    }

    @Override
    public int hashCode() {
      return location.hashCode() ^ path.hashCode();
    }
  }
}
