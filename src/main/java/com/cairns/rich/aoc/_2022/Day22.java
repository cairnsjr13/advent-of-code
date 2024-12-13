package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.commons.lang3.tuple.Pair;

class Day22 extends Base2022 {
  private static final Map<ReadDir, Integer> facingValue = Map.of(
      ReadDir.Right, 0,
      ReadDir.Down, 1,
      ReadDir.Left, 2,
      ReadDir.Up, 3
  );

  @Override
  protected Object part1(Loader loader) {
    return getPassword(loader, this::wirePart1);
  }

  @Override
  protected Object part2(Loader loader) {
    return getPassword(loader, (graph, map) -> wirePart2Full(graph));
  }

  private int getPassword(
      Loader loader,
      BiConsumer<Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>>, char[][]> wire
  ) {
    List<String> input = loader.ml();
    char[][] map = buildMap(input.subList(0, input.size() - 2));
    String movements = input.get(input.size() - 1);
    Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph = buildGraph(map);
    wire.accept(graph, map);
    return getPassword(map, graph, movements);
  }

  private int getPassword(char[][] map, Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph, String movements) {
    int x = 0;
    while (map[0][x] == ' ') {
      ++x;
    }
    ImmutablePoint current = new ImmutablePoint(x, 0);
    ReadDir facing = ReadDir.Right;
    for (int i = 0; i < movements.length(); ) {
      char ch = movements.charAt(i);
      if (Character.isDigit(ch)) {
        int nextTurnIndex = nextTurnIndex(movements, i);
        int moves = Integer.parseInt(movements.substring(i, nextTurnIndex));
        for (int m = 0; m < moves; ++m) {
          Pair<ImmutablePoint, ReadDir> next = graph.get(current, facing);
          if (spot(map, next.getLeft()) == '#') {
            break;
          }
          current = next.getLeft();
          facing = next.getRight();
        }
        i = nextTurnIndex;
      }
      else if (ch == 'L') {
        facing = facing.turnLeft();
        ++i;
      }
      else if (ch == 'R') {
        facing = facing.turnRight();
        ++i;
      }
      else {
        throw fail(i + " - " + ch);
      }
    }
    return 1000 * (current.y() + 1)
         +    4 * (current.x() + 1)
         +    1 * facingValue.get(facing);
  }

  private int nextTurnIndex(String movements, int i) {
    int ofL = movements.indexOf('L', i);
    int ofR = movements.indexOf('R', i);
    ofL = (ofL == -1) ? movements.length() : ofL;
    ofR = (ofR == -1) ? movements.length() : ofR;
    return Math.min(ofL, ofR);
  }

  private static char spot(char[][] map, Point<?> location) {
    return (Grid.isValid(map, location))
        ? map[location.y()][location.x()]
        : ' ';
  }

  private char[][] buildMap(List<String> mapInput) {
    char[][] map = new char[mapInput.size()][getMax(mapInput, String::length).length()];
    Arrays.stream(map).forEach((r) -> Arrays.fill(r, ' '));
    for (int y = 0; y < mapInput.size(); ++y) {
      String row = mapInput.get(y);
      for (int x = 0; x < row.length(); ++x) {
        map[y][x] = row.charAt(x);
      }
    }
    return map;
  }

  private Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> buildGraph(char[][] map) {
    Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph = HashBasedTable.create();
    for (int y = 0; y < map.length; ++y) {
      for (int x = 0; x < map[y].length; ++x) {
        if (map[y][x] != ' ') {
          ImmutablePoint location = new ImmutablePoint(x, y);
          for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
            ImmutablePoint move = location.move(dir);
            if (spot(map, move) != ' ') {
              graph.put(location, dir, Pair.of(move, dir));
            }
          }
        }
      }
    }
    return graph;
  }

  private void wirePart1(Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph, char[][] map) {
    for (int y = 0; y < map.length; ++y) {
      int minX = 0;
      while (map[y][minX] == ' ') {
        ++minX;
      }
      int maxX = map[0].length - 1;
      while (map[y][maxX] == ' ') {
        --maxX;
      }
      part1Wire(graph, minX, y, ReadDir.Left, maxX, y);
    }
    for (int x = 0; x < map[0].length; ++x) {
      int minY = 0;
      while (map[minY][x] == ' ') {
        ++minY;
      }
      int maxY = map.length - 1;
      while (map[maxY][x] == ' ') {
        --maxY;
      }
      part1Wire(graph, x, minY, ReadDir.Up, x, maxY);
    }
  }

  private void part1Wire(
      Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph,
      int fromX, int fromY,
      ReadDir dir,
      int toX, int toY
  ) {
    wire(graph, fromX, fromY, dir, toX, toY, dir);
  }

  @SuppressWarnings("unused") // TODO: only because genericizing it is a pain. Probably want to add something to testLoaders
  private void wirePart2Test(Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph) {
    for (int i = 0; i < 4; ++i) {
      wire(graph,  0 + i,     7, ReadDir.Down, 11 - i,     11, ReadDir.Up);
      wire(graph,  4 + i,     4, ReadDir.Up,        8,  i + 0, ReadDir.Right);
      wire(graph,  4 + i,     7, ReadDir.Down,      8, 11 - i, ReadDir.Right);
      wire(graph,  8 + i,     0, ReadDir.Up,    3 - i,      4, ReadDir.Down);
      wire(graph, 15 - i,     8, ReadDir.Up,       11,  i + 4, ReadDir.Left);
      wire(graph, 15 - i,    11, ReadDir.Down,      0,  i + 4, ReadDir.Right);
      wire(graph,     11, i + 0, ReadDir.Right,    15, 11 - i, ReadDir.Left);
    }
  }

  private void wirePart2Full(Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph) {
    for (int i = 0; i < 50; ++i) {
      wire(graph,     0, 149 - i, ReadDir.Left,       50, 0 + i, ReadDir.Right);
      wire(graph,     0, 150 + i, ReadDir.Left,   50 + i,     0, ReadDir.Down);
      wire(graph,    49, 150 + i, ReadDir.Right,  50 + i,   149, ReadDir.Up);
      wire(graph,    50,  50 + i, ReadDir.Left,    0 + i,   100, ReadDir.Down);
      wire(graph,    99,  50 + i, ReadDir.Right, 100 + i,    49, ReadDir.Up);
      wire(graph,    99, 149 - i, ReadDir.Right,     149, 0 + i, ReadDir.Left);
      wire(graph, 0 + i,     199, ReadDir.Down,  100 + i,     0, ReadDir.Down);
    }
  }

  private void wire(
      Table<ImmutablePoint, ReadDir, Pair<ImmutablePoint, ReadDir>> graph,
      int fromX, int fromY,
      ReadDir beforeDir,
      int toX, int toY,
      ReadDir afterDir
  ) {
    ImmutablePoint from = new ImmutablePoint(fromX, fromY);
    ImmutablePoint to = new ImmutablePoint(toX, toY);
    graph.put(from, beforeDir, Pair.of(to, afterDir));
    graph.put(to, afterDir.turnAround(), Pair.of(from, beforeDir.turnAround()));
  }
}
