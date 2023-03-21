package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day24 extends Base2019 {
  private static final Map<ReadDir, List<ImmutablePoint>> recursiveLayerNeighbors = Map.of(
      ReadDir.Up, IntStream.range(0, 5).mapToObj((x) -> new ImmutablePoint(x, 4)).collect(Collectors.toList()),
      ReadDir.Down, IntStream.range(0, 5).mapToObj((x) -> new ImmutablePoint(x, 0)).collect(Collectors.toList()),
      ReadDir.Left, IntStream.range(0, 5).mapToObj((y) -> new ImmutablePoint(4, y)).collect(Collectors.toList()),
      ReadDir.Right, IntStream.range(0, 5).mapToObj((y) -> new ImmutablePoint(0,y)).collect(Collectors.toList())
  );

  @Override
  protected Object part1(Loader loader) {
    State state = new State(loader.ml());
    Set<State> seenStates = new HashSet<>();
    seenStates.add(state);
    while (true) {
      state = state.next(0, 0, this::simpleCountNeighbors);
      if (!seenStates.add(state)) {
        return state.grid.get(0);
      }
    }
  }

  @Override
  protected Object part2(Loader loader) {
    int numMinutes = 200;
    State state = new State(loader.ml());
    for (int i = 0; i < numMinutes; ++i) {
      state = state.next(state.grid.firstKey() - 1, state.grid.lastKey() + 1, this::complexCountNeighbors);
    }
    return state.grid.values().stream().mapToInt(Integer::bitCount).sum();
  }

  private long simpleCountNeighbors(State state, int layer, int x, int y) {
    return Arrays.stream(EnumUtils.enumValues(ReadDir.class))
        .filter((dir) -> state.contains(layer, y + dir.dy(), x + dir.dx()))
        .count();
  }

  private long complexCountNeighbors(State state, int layer, int x, int y) {
    if ((x == 2) && (y == 2)) {
      return 0;
    }
    int numNeighbors = 0;
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      int nLayer = layer;
      int nx = x + dir.dx();
      int ny = y + dir.dy();
      if ((nx == 2) && (ny == 2)) {
        ++nLayer;
        for (ImmutablePoint recursivePoint : recursiveLayerNeighbors.get(dir)) {
          if (state.contains(nLayer, recursivePoint.y(), recursivePoint.x())) {
            ++numNeighbors;
          }
        }
      }
      else {
        if (nx == -1) {
          --nLayer;
          nx = 1;
          ny = 2;
        }
        else if (nx == 5) {
          --nLayer;
          nx = 3;
          ny = 2;
        }
        else if (ny == -1) {
          --nLayer;
          nx = 2;
          ny = 1;
        }
        else if (ny == 5) {
          --nLayer;
          nx = 2;
          ny = 3;
        }
        if (state.contains(nLayer, ny, nx)) {
          ++numNeighbors;
        }
      }
    }
    return numNeighbors;
  }

  private static class State {
    private final TreeMap<Integer, Integer> grid = new TreeMap<>();

    private State() { }

    private State(List<String> lines) {
      grid.put(0, computeCells((x, y) -> '#' == lines.get(y).charAt(x)));
    }

    private boolean contains(int layer, int y, int x) {
      return (0 <= x) && (x < 5) && (0 <= y) && (y < 5)
          && (0 != (grid.getOrDefault(layer, 0) & cell(x, y)));
    }

    private State next(int minLayer, int maxLayer, NeighborCounter neighborCounter ) {
      State nextState = new State();
      IntStream.rangeClosed(minLayer, maxLayer).forEach((layer) -> nextState.grid.put(layer, computeCells((x, y) -> {
        long numNeighbors = neighborCounter.countNeighbors(this, layer, x, y);
        return (numNeighbors == 1) || ((numNeighbors == 2) && !contains(layer, y, x));
      })));
      return nextState;
    }

    private int computeCells(BiPredicate<Integer, Integer> test) {
      int cells = 0;
      for (int x = 0; x < 5; ++x) {
        for (int y = 0; y < 5; ++y) {
          if (test.test(x, y)) {
            cells += cell(x, y);
          }
        }
      }
      return cells;
    }

    private int cell(int x, int y) {
      return 1 << ((y * 5) + x);
    }

    @Override
    public boolean equals(Object other) {
      return grid.equals(((State) other).grid);
    }

    @Override
    public int hashCode() {
      return grid.hashCode();
    }
  }

  private interface NeighborCounter {
    long countNeighbors(State state, int layer, int x, int y);
  }
}
