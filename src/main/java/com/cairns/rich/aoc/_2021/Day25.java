package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class Day25 extends Base2021 {
  @Override
  protected Object part1(Loader loader) {
    char[][] current = loadInitial(loader.ml());
    char[][] next = new char[current.length][current[0].length];
    int steps = 1;
    while (0 != step(current, next)) {
      ++steps;
    }
    return steps;
  }

  // only works if there are an even number of herds
  private int step(char[][] current, char[][] next) {
    int numMoves = 0;
    for (Herd herd : EnumUtils.enumValues(Herd.class)) {
      numMoves += herdStep(current, next, herd);
      char[][] swap = current;
      current = next;
      next = swap;
    }
    return numMoves;
  }

  private int herdStep(char[][] current, char[][] next, Herd herd) {
    int numMoves = 0;
    Stream.of(next).forEach((row) -> Arrays.fill(row, '.'));
    for (int y = 0; y < current.length; ++y) {
      for (int x = 0; x < current[0].length; ++x) {
        if (current[y][x] == herd.indicator) {
          int ny = y + herd.dir.dy();
          int nx = x + herd.dir.dx();
          if ('.' == safeGet(safeGet(current, ny), nx)) {
            safeSet(safeGet(next, ny), nx, herd.indicator);
            ++numMoves;
          }
          else {
            next[y][x] = herd.indicator;
          }
        }
        else if (current[y][x] != '.') {
          next[y][x] = current[y][x];
        }
      }
    }
    return numMoves;
  }

  private char[][] loadInitial(List<String> input) {
    char[][] initial = new char[input.size()][input.get(0).length()];
    for (int y = 0; y < initial.length; ++y) {
      for (int x = 0; x < initial[0].length; ++x) {
        initial[y][x] = input.get(y).charAt(x);
      }
    }
    return initial;
  }

  private enum Herd {
    East('>', ReadDir.Right),
    South('v', ReadDir.Down);

    private final char indicator;
    private final ReadDir dir;

    private Herd(char indicator, ReadDir dir) {
      this.indicator = indicator;
      this.dir = dir;
    }
  }
}
