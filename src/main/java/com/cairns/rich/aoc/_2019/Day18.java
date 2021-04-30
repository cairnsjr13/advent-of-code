package com.cairns.rich.aoc._2019;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

class Day18 extends Base2019 {
  private RelDir[] dirs = EnumUtils.enumValues(RelDir.class);
  
  @Override
  protected void run() {
    char[][] grid = fullLoader.ml(String::toCharArray).toArray(char[][]::new);
    List<Table<Character, Character, PathDesc>> pathsByRobot = computePaths(grid);
    int numKeysNeeded = pathsByRobot.get(0).columnKeySet().size();
    ToIntFunction<List<Table<Character, Character, PathDesc>>> getFastestSteps = (ps) -> getFastestPathSteps(
        numKeysNeeded,
        ps,
        HashBasedTable.create(),
        ps.stream().map((i) -> '@').collect(Collectors.toList()),
        new HashSet<>()
    );
    System.out.println(getFastestSteps.applyAsInt(pathsByRobot));
    System.out.println(getFastestSteps.applyAsInt(computePaths(split(grid))));
  }
  
  private int getFastestPathSteps(
      int numKeysNeeded,
      List<Table<Character, Character, PathDesc>> pathsByRobot,
      Table<List<Character>, Set<Character>, Integer> stepsCache,
      List<Character> currentKeysByRobot,
      Set<Character> heldKeys
  ) {
    if (heldKeys.size() == numKeysNeeded) {
      return 0;
    }
    if (!stepsCache.contains(currentKeysByRobot, heldKeys)) {
      int best = Short.MAX_VALUE;
      List<List<Character>> reachables = getReachables(pathsByRobot, currentKeysByRobot, heldKeys);
      for (int i = 0; i < reachables.size(); ++i) {
        Table<Character, Character, PathDesc> paths = pathsByRobot.get(i);
        char current = currentKeysByRobot.get(i);
        for (char next : reachables.get(i)) {
          currentKeysByRobot.set(i, next);
          heldKeys.add(next);
          int cost = paths.get(current, next).numSteps
                   + getFastestPathSteps(numKeysNeeded, pathsByRobot, stepsCache, currentKeysByRobot, heldKeys);
          best = Math.min(best, cost);
          heldKeys.remove(next);
          currentKeysByRobot.set(i, current);
        }
      }
      stepsCache.put(new ArrayList<>(currentKeysByRobot), new HashSet<>(heldKeys), best);
    }
    return stepsCache.get(currentKeysByRobot, heldKeys);
  }
  
  private List<List<Character>> getReachables(
      List<Table<Character, Character, PathDesc>> pathsByRobot,
      List<Character> currentKeysByRobot,
      Set<Character> heldKeys
  ) {
    return IntStream.range(0, pathsByRobot.size()).mapToObj((i) -> {
      Map<Character, PathDesc> fromCurrent = pathsByRobot.get(i).row(currentKeysByRobot.get(i));
      return fromCurrent.keySet().stream()
          .filter((to) -> !heldKeys.contains(to) && heldKeys.containsAll(fromCurrent.get(to).keysNeeded))
          .collect(Collectors.toList());
    }).collect(Collectors.toList());
  }
  
  private List<Table<Character, Character, PathDesc>> computePaths(char[][]... grids) {
    return Arrays.stream(grids)
        .map((grid) -> {
          MapState mapState = new MapState(grid);
          Table<Character, Character, PathDesc> paths = TreeBasedTable.create();
          for (char from : mapState.keyLookup.keySet()) {
            Multimap<Integer, Integer> visited = HashMultimap.create();
            Queue<WalkDesc> queue = new ArrayDeque<>();
            
            WalkDesc init = new WalkDesc(0, mapState.keyLookup.get(from), new HashSet<>());
            visited.put(init.location.x(), init.location.y());
            queue.add(init);
            
            while (!queue.isEmpty()) {
              WalkDesc current = queue.poll();
              for (RelDir dir : dirs) {
                if (mapState.canMove(visited, current.location, dir)) {
                  ImmutablePoint move = current.location.move(dir);
                  char moveSymbol = mapState.map.get(move.x(), move.y());
                  Set<Character> keysNeededOnMovePath = current.keysNeededOnThisPath;
                  if (Character.isUpperCase(moveSymbol)) {
                    keysNeededOnMovePath = new HashSet<>(keysNeededOnMovePath);
                    keysNeededOnMovePath.add(Character.toLowerCase(moveSymbol));
                  }
                  else if (Character.isLowerCase(moveSymbol)) {
                    paths.put(from, moveSymbol, new PathDesc(current.numSteps + 1, keysNeededOnMovePath));
                  }
                  visited.put(move.x(), move.y());
                  queue.add(new WalkDesc(current.numSteps + 1, move, keysNeededOnMovePath));
                }
              }
            }
          }
          return paths;
        })
        .collect(Collectors.toList());
  }
  
  private static char[][][] split(char[][] orig) {
    int splitHeight = orig.length / 2 + 1;
    int splitWidth = orig[0].length / 2 + 1;
    BiFunction<Integer, Integer, char[][]> split = (heightFactor, widthFactor) -> {
      char[][] section = new char[splitHeight][splitWidth];
      for (int y = 0; y < section.length; ++y) {
        for (int x = 0; x < section[0].length; ++x) {
          section[y][x] = orig[y + heightFactor * (splitHeight - 1)][x + widthFactor * (splitWidth - 1)];
        }
      }
      section[(heightFactor == 1) ? 1 : splitHeight - 2][(widthFactor == 1) ? 1 : splitWidth - 2] = '@';
      Arrays.fill(section[0], '#');
      IntStream.range(0, section.length).forEach((y) -> section[y][0] = section[y][section[y].length - 1] = '#');
      Arrays.fill(section[section.length - 1], '#');
      return section;
    };
    return new char[][][] { split.apply(0, 0), split.apply(0, 1), split.apply(1, 0), split.apply(1, 1) };
  }
  
  private static class WalkDesc {
    private final int numSteps;
    private final ImmutablePoint location;
    private final Set<Character> keysNeededOnThisPath;
    
    private WalkDesc(int numSteps, ImmutablePoint location, Set<Character> keysNeededOnThisPath) {
      this.numSteps = numSteps;
      this.location = location;
      this.keysNeededOnThisPath = keysNeededOnThisPath;
    }
  }
  
  private static class PathDesc {
    private final int numSteps;
    private final Set<Character> keysNeeded;
    
    private PathDesc(int numSteps, Set<Character> keysNeeded) {
      this.numSteps = numSteps;
      this.keysNeeded = new HashSet<>(keysNeeded);
    }
    
    @Override
    public String toString() {
      return "{" + numSteps + " " + keysNeeded + "}";
    }
  }
  
  private static class MapState {
    private final Table<Integer, Integer, Character> map = HashBasedTable.create();
    private final Map<Character, ImmutablePoint> keyLookup = new HashMap<>();
    
    private MapState(char[][] grid) {
      for (int y = 0; y < grid.length; ++y) {
        for (int x = 0; x < grid[0].length; ++x) {
          char ch = grid[y][x];
          map.put(x, -y, ch);
          if ((ch == '@') || Character.isLowerCase(ch)) {
            keyLookup.put(ch, new ImmutablePoint(x, -y));
          }
        }
      }
    }
    
    private boolean canMove(Multimap<Integer, Integer> visited, ImmutablePoint location, RelDir dir) {
      int nx = location.x() + dir.dx();
      int ny = location.y() + dir.dy();
      return !visited.containsEntry(nx, ny) && (map.get(nx, ny) != '#');
    }
  }
}
