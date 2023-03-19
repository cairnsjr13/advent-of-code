package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import java.util.List;

class Day11 extends Base2020 {
  @Override
  protected Object part1(Loader2 loader) {
    return runSimulation(loader, 1, 4);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return runSimulation(loader, Integer.MAX_VALUE, 5);
  }

  private long runSimulation(Loader2 loader, int maxFactor, int bailThreshold) {
    State state = new State(maxFactor, bailThreshold, loader.ml());
    while (step(state)) ;
    return Arrays.stream(state.grid).mapToLong((r) -> Arrays.stream(r).filter((s) -> s == '#').count()).sum();
  }

  private boolean step(State state) {
    boolean hasChange = false;
    for (int row = 0; row < state.grid.length; ++row) {
      for (int col = 0; col < state.grid[0].length; ++col) {
        int currentState = state.grid[row][col];
        int newState = currentState;
        int currentNeighbors = countNeighbors(state, row, col);
        if ((currentState == '#') && (currentNeighbors >= state.bailThreshold)) {
          newState = 'L';
        }
        else if ((currentState == 'L') && (currentNeighbors == 0)) {
          newState = '#';
        }
        state.temp[row][col] = newState;
        hasChange |= (newState != currentState);
      }
    }
    state.flip();
    return hasChange;
  }

  private int countNeighbors(State state, int row, int col) {
    int neighbors = 0;
    for (int dr = -1; dr <= 1; ++dr) {
      for (int dc = -1; dc <= 1; ++dc) {
        if ((dr != 0) || (dc != 0)) {
          for (int factor = 1; factor <= state.maxFactor; ++factor) {
            int seat = getSeat(state.grid, row + (dr * factor), col + (dc * factor));
            if (seat != '.') {
              if (seat == '#') {
                ++neighbors;
              }
              break;
            }
          }
        }
      }
    }
    return neighbors;
  }

  private int getSeat(int[][] grid, int row, int col) {
    return ((0 <= row) && (row < grid.length) && (0 <= col) && (col < grid[0].length))
         ? grid[row][col]
         : 'L';
  }

  private static class State {
    private final int maxFactor;
    private final int bailThreshold;
    private int[][] grid;
    private int[][] temp;

    private State(int maxFactor, int bailThreshold, List<String> lines) {
      this.maxFactor = maxFactor;
      this.bailThreshold = bailThreshold;
      this.grid = lines.stream().map((r) -> r.chars().toArray()).toArray(int[][]::new);
      this.temp = Arrays.stream(grid).map((r) -> Arrays.copyOf(r, r.length)).toArray(int[][]::new);
    }

    private void flip() {
      int[][] flip = grid;
      grid = temp;
      temp = flip;
    }
  }
}
