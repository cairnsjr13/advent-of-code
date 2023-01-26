package com.cairns.rich.aoc._2021;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;

public class Day25 extends Base2021 {
  @Override
  protected void run() throws Throwable {
    char[][] current = loadInitial(fullLoader);
    char[][] next = new char[current.length][current[0].length];
    int steps = 1;
    while (0 != step(current, next)) {
      ++steps;
    }
    System.out.println("Stopped after: " + steps);
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
          int ny = (y + herd.dy) % current.length;
          int nx = (x + herd.dx) % current[0].length;
          if (current[ny][nx] == '.') {
            next[ny][nx] = herd.indicator;
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
  
  private char[][] loadInitial(Loader2 loader) {
    List<String> input = loader.ml();
    char[][] initial = new char[input.size()][input.get(0).length()];
    for (int y = 0; y < initial.length; ++y) {
      for (int x = 0; x < initial[0].length; ++x) {
        initial[y][x] = input.get(y).charAt(x);
      }
    }
    return initial;
  }
  
  private enum Herd {
    East('>', 0, 1),
    South('v', 1, 0);
    
    private final char indicator;
    private final int dy;
    private final int dx;
    
    private Herd(char indicator, int dy, int dx) {
      this.indicator = indicator;
      this.dy = dy;
      this.dx = dx;
    }
  }
}
