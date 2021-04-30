package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day24 extends Base2019 {
  private static final Multimap<Integer, Integer> emptyLayer = HashMultimap.create();
  private static final Map<ReadDir, List<ImmutablePoint>> recursiveLayerNeighbors = Map.of(
      ReadDir.Up, IntStream.range(0, 5).mapToObj((x) -> new ImmutablePoint(x, 4)).collect(Collectors.toList()),
      ReadDir.Down, IntStream.range(0, 5).mapToObj((x) -> new ImmutablePoint(x, 0)).collect(Collectors.toList()),
      ReadDir.Left, IntStream.range(0, 5).mapToObj((y) -> new ImmutablePoint(4, y)).collect(Collectors.toList()),
      ReadDir.Right, IntStream.range(0, 5).mapToObj((y) -> new ImmutablePoint(0,y)).collect(Collectors.toList())
  );
  
  @Override
  protected void run() {
    TreeMap<Integer, Multimap<Integer, Integer>> state = parse(fullLoader);
    System.out.println(singleLayerBioDiv(state));
    System.out.println(numBugsAfter(state, 200));
  }
  
  private int singleLayerBioDiv(TreeMap<Integer, Multimap<Integer, Integer>> state) {
    Set<TreeMap<Integer, Multimap<Integer, Integer>>> seenStates = new HashSet<>();
    seenStates.add(state);
    while (true) {
      state = nextState(state, 0, 0, this::simpleCountNeighbors);
      if (!seenStates.add(state)) {
        break;
      }
    }
    return computeBioDiv(state);
  }
  
  private int numBugsAfter(TreeMap<Integer, Multimap<Integer, Integer>> state, int numMinutes) {
    for (int i = 0; i < numMinutes; ++i) {
      state = nextState(state, state.firstKey() - 1, state.lastKey() + 1, this::complexCountNeighbors);
    }
    return state.values().stream().mapToInt(Multimap::size).sum();
  }
  
  private TreeMap<Integer, Multimap<Integer, Integer>> nextState(
      TreeMap<Integer, Multimap<Integer, Integer>> state,
      int minLayer,
      int maxLayer,
      NeighborCounter neighborCounter
  ) {
    TreeMap<Integer, Multimap<Integer, Integer>> nextState = new TreeMap<>();
    for (int layer = minLayer; layer <= maxLayer; ++layer) {
      for (int y = 0; y < 5; ++y) {
        for (int x = 0; x < 5; ++x) {
          int numNeighbors = neighborCounter.countNeighbors(state, layer, x, y);
          if ((numNeighbors == 1) || ((numNeighbors == 2) && !state.getOrDefault(layer, emptyLayer).containsEntry(y, x))) {
            nextState.computeIfAbsent(layer, (i) -> HashMultimap.create()).put(y, x);
          }
        }
      }
    }
    return nextState;
  }
  
  private int simpleCountNeighbors(TreeMap<Integer, Multimap<Integer, Integer>> state, int layer, int x, int y) {
    int numNeighbors = 0;
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      if (state.get(layer).containsEntry(y + dir.dy(), x + dir.dx())) {
        ++numNeighbors;
      }
    }
    return numNeighbors;
  }
  
  private int complexCountNeighbors(TreeMap<Integer, Multimap<Integer, Integer>> state, int layer, int x, int y) {
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
          if (state.getOrDefault(nLayer, emptyLayer).containsEntry(recursivePoint.y(), recursivePoint.x())) {
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
        if (state.getOrDefault(nLayer, emptyLayer).containsEntry(ny, nx)) {
          ++numNeighbors;
        }
      }
    }
    return numNeighbors;
  }
  
  private int computeBioDiv(Map<Integer, Multimap<Integer, Integer>>  state) {
    int bioDiv = 0;
    for (int y : state.get(0).keySet()) {
      for (int x : state.get(0).get(y)) {
        bioDiv |= (1 << (y * 5 + x));
      }
    }
    return bioDiv;
  }
  
  private TreeMap<Integer, Multimap<Integer, Integer>> parse(Loader2 loader) {
    TreeMap<Integer, Multimap<Integer, Integer>> state = new TreeMap<>();
    state.put(0, HashMultimap.create());
    List<String> lines = loader.ml();
    for (int y = 0; y < lines.size(); ++y) {
      for (int x = 0; x < lines.get(y).length(); ++x) {
        if (lines.get(y).charAt(x) == '#') {
          state.get(0).put(y, x);
        }
      }
    }
    return state;
  }
  
  private interface NeighborCounter {
    int countNeighbors(TreeMap<Integer, Multimap<Integer, Integer>> state, int layer, int x, int y);
  }
}
