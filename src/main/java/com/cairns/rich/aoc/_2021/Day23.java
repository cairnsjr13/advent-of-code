package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day23 extends Base2021 {
  private static final Map<Character, Integer> typeToCost = Map.of('A', 1, 'B', 10, 'C', 100, 'D', 1000);
  private static final Set<ImmutablePoint> cantStop =
      IntStream.of(3, 5, 7, 9).mapToObj((x) -> new ImmutablePoint(x, 1)).collect(Collectors.toSet());

  @Override
  protected Object part1(Loader loader) {
    return findLeastCost(loader, 2);
  }

  @Override
  protected Object part2(Loader loader) {
    return findLeastCost(loader, 4);
  }

  private int findLeastCost(Loader loader, int numEachType) {
    Problem problem = new Problem(numEachType);
    return bfs(
        new State(parse(loader, numEachType), 0),
        (s) -> s.map.equals(problem.allHome),
        (ss) -> ss.state.getWeight(),
        (current, registrar) -> {
          current.map.forEach((location, type) -> {
            if (!isHome(problem, current.map, location, type)) {
              tryAllMoves(registrar, problem, current.map, current.costSoFar, location, location, type);
              problem.visited.clear();
            }
          });
        }
    ).get().state.costSoFar;
  }

  private void tryAllMoves(
      Consumer<State> registrar,
      Problem problem,
      SortedMap<ImmutablePoint, Character> map,
      int cost,
      ImmutablePoint origLocation,
      ImmutablePoint from,
      char type
  ) {
    if (!problem.visited.add(from)) {
      return;
    }
    problem.validMoves.get(type).get(from).forEach((to) -> {
      if (!map.containsKey(to)) {
        int newCost = cost + typeToCost.get(type);
        TreeMap<ImmutablePoint, Character> moveMap = new TreeMap<>(map);
        moveMap.remove(from);
        moveMap.put(to, type);
        if (canStop(problem, moveMap, origLocation, to, type) && problem.seenStates.add(serialize(moveMap))) {
          registrar.accept(new State(moveMap, newCost));
        }
        tryAllMoves(registrar, problem, moveMap, newCost, origLocation, to, type);
      }
    });
  }

  private boolean isHome(Problem problem, SortedMap<ImmutablePoint, Character> map, ImmutablePoint location, Character type) {
    if (location.x() != typeToX(type)) {
      return false;
    }
    for (; location.y() <= 1 + problem.numEachType; location = location.move(ReadDir.Down)) {
      if (map.get(location) != type) {
        return false;
      }
    }
    return true;
  }

  private boolean canStop(
      Problem problem,
      SortedMap<ImmutablePoint, Character> map,
      ImmutablePoint origLocation,
      ImmutablePoint to,
      char type
  ) {
    if (cantStop.contains(to)) {
      return false;
    }
    if (origLocation.y() == 1) {    // originally in hallway
      return isHome(problem, map, to, type);
    }
    return (to.y() == 1) || isHome(problem, map, to, type);
  }

  /**
   * Serializes the given map into a short form "long" version.  This is to save space when tracking states we have
   * already seen.  The first set of bits are a bitmap indicating if there is an amphipods present or not.  After those
   * bits we have a number of 2 bit indicators signifying which of the types are in those spots (assuming sorted order).
   */
  private long serialize(SortedMap<ImmutablePoint, Character> map) {
    long serialized = 0;
    int typeIndex = 2 * map.size();
    for (ImmutablePoint location : map.keySet()) {
      long bitMask = map.get(location) - 'A';
      serialized |= (bitMask << typeIndex);
      serialized |= (1L << locationToOffset(location));
      typeIndex += 2;
    }
    return serialized;
  }

  /**
   * Returns a unique index for each of the valid locations on the map.
   * The hallway locations occupy 0-10 (11 spots.
   * The home spaces occupy the indexes after those with y being most significant, and x being less significant.
   */
  private int locationToOffset(ImmutablePoint location) {
    return (location.y() == 1)
        ? location.x() - 1    // 0 through 10 (total 11)
        : 4 * location.y() + location.x() / 2 + 2;
  }

  private SortedMap<ImmutablePoint, Character> parse(Loader loader, int numEachType) {
    List<String> inputs = loader.ml();
    BiFunction<Integer, Integer, Character> chAt = (y, x) -> inputs.get(y).charAt(x);
    SortedMap<ImmutablePoint, Character> map =
        new TreeMap<>(Comparator.comparing(ImmutablePoint::y).thenComparing(ImmutablePoint::x));
    registerRow(map, 2, chAt.apply(2, 3), chAt.apply(2, 5), chAt.apply(2, 7), chAt.apply(2, 9));
    int lastY = 3;
    if (numEachType > 2) {
      registerRow(map, 3, 'D', 'C', 'B', 'A');
      registerRow(map, 4, 'D', 'B', 'A', 'C');
      lastY += 2;
    }
    registerRow(map, lastY, chAt.apply(3, 3), chAt.apply(3, 5), chAt.apply(3, 7), chAt.apply(3, 9));
    return map;
  }

  private void registerRow(SortedMap<ImmutablePoint, Character> map, int y, char... types) {
    for (int i = 0; i < types.length; ++i) {
      map.put(new ImmutablePoint(3 + 2 * i, y), types[i]);
    }
  }

  private static int typeToX(char type) {
    return 3 + 2 * (type - 'A');
  }

  private static class Problem {
    private final int numEachType;
    private final Map<Character, Multimap<ImmutablePoint, ImmutablePoint>> validMoves = new HashMap<>();
    private final Map<ImmutablePoint, Character> allHome = new HashMap<>();
    private final Set<Long> seenStates = new HashSet<>();
    private final Set<ImmutablePoint> visited = new HashSet<>();

    private Problem(int numEachType) {
      this.numEachType = numEachType;
      initValidMoves();
      initAllHome();
    }

    private void initValidMoves() {
      typeToCost.keySet().forEach((type) -> validMoves.put(type, HashMultimap.create()));
      validMoves.forEach((type, moves) -> moves.put(new ImmutablePoint(1, 1), new ImmutablePoint(2, 1)));
      for (int x = 2; x <= 10; ++x) {
        ImmutablePoint from = new ImmutablePoint(x, 1);
        validMoves.forEach((type, moves) -> moves.put(from, from.move(ReadDir.Left)));
        validMoves.forEach((type, moves) -> moves.put(from, from.move(ReadDir.Right)));
      }
      validMoves.forEach((type, moves) -> moves.put(new ImmutablePoint(11, 1), new ImmutablePoint(10, 1)));
      validMoves.forEach((type, moves) -> {
        ImmutablePoint from = new ImmutablePoint(typeToX(type), 1);
        while (from.y() < 1 + numEachType) {
          ImmutablePoint to = from.move(ReadDir.Down);
          moves.put(from, to);
          from = to;
        }
      });
      validMoves.forEach((type, moves) -> {
        int typeX = typeToX(type);
        for (int x = 3; x <= 9; x += 2) {
          ImmutablePoint from = new ImmutablePoint(x, numEachType + ((typeX != x) ? 1 : 0));
          while (from.y() > 1) {
            ImmutablePoint to = from.move(ReadDir.Up);
            moves.put(from, to);
            from = to;
          }
        }
      });
    }

    private void initAllHome() {
      typeToCost.keySet().forEach((type) -> {
        int x = typeToX(type);
        for (int y = 2; y < 2 + numEachType; ++y) {
          allHome.put(new ImmutablePoint(x, y), type);
        }
      });
    }
  }

  private static class State {
    private final SortedMap<ImmutablePoint, Character> map;
    private final int costSoFar;

    private State(SortedMap<ImmutablePoint, Character> map, int costSoFar) {
      this.map = map;
      this.costSoFar = costSoFar;
    }

    /**
     * Returns the pq weight for this state.  Considers the existing cost to get to this state and the minimum required
     * cost to get to final state.  Technically this over counts the latter because not all amphipods need to get to the
     * deepest home.  However, because all of them are equaly overcounted, this still serves as a valid weight.
     */
    private int getWeight() {
      return costSoFar + map.keySet().stream().mapToInt((location) -> minimumMoveCost(location, map.get(location))).sum();
    }

    /**
     * Calculates the cost to directly move the type at the given location to the deepest home.
     */
    private int minimumMoveCost(ImmutablePoint location, char type) {
      int typeX = typeToX(type);
      int numMoves = (location.x() == typeX)
          ? 3 - location.y()
          : (location.y() - 1) + Math.abs(location.x() - typeX) + 2;
      return numMoves * typeToCost.get(type);
    }
  }
}
