package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Day11 extends Base2020 {
  @Override
  protected void run() {
    System.out.println(runSimulation(fullLoader, 1, 4));
    System.out.println(runSimulation(fullLoader, Integer.MAX_VALUE, 5));
  }
  
  private long runSimulation(Loader2 loader, int maxFactor, int bailThreshold) {
    List<List<Boolean>> grid = loader.ml(this::parseLine);
    State state = new State(maxFactor, bailThreshold, grid);
    while (step(state)) ;
    return state.count();
  }
  
  private boolean step(State state) {
    boolean hasChange = false;
    for (int row = 0; row < state.grid.size(); ++row) {
      for (int col = 0; col < state.grid.get(0).size(); ++col) {
        Boolean currentState = state.grid.get(row).get(col);
        Boolean newState = currentState;
        int currentNeighbors = countNeighbors(state, row, col);
        if ((currentState == Boolean.TRUE) && (currentNeighbors >= state.bailThreshold)) {
          newState = false;
        }
        else if ((currentState == Boolean.FALSE) && (currentNeighbors == 0)) {
          newState = true;
        }
        state.temp.get(row).set(col, newState);
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
            Boolean isSet = isSet(state.grid, row + (dr * factor), col + (dc * factor));
            if (isSet != null) {
              if (isSet) {
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
  
  private Boolean isSet(List<List<Boolean>> grid, int row, int col) {
    return ((0 <= row) && (row < grid.size()) && (0 <= col) && (col < grid.get(0).size()))
        ? grid.get(row).get(col)
        : Boolean.FALSE;
  } 
  
  private List<Boolean> parseLine(String line) {
    return line.chars().mapToObj((ch) -> (ch == 'L') ? Boolean.FALSE : ((ch == '#') ? Boolean.TRUE : null)).collect(Collectors.toList());
  }
  
  private static class State {
    private final int maxFactor;
    private final int bailThreshold;
    private List<List<Boolean>> grid;
    private List<List<Boolean>> temp;
    
    private State(int maxFactor, int bailThreshold, List<List<Boolean>> grid) {
      this.maxFactor = maxFactor;
      this.bailThreshold = bailThreshold;
      this.grid = grid;
      this.temp = new ArrayList<>();
      for (List<Boolean> row : grid) {
        temp.add(new ArrayList<>(row));
      }
    }
    
    private void flip() {
      List<List<Boolean>> flip = grid;
      grid = temp;
      temp = flip;
    }
    
    private long count() {
      return grid.stream().flatMap(List::stream).filter((obj) -> obj == Boolean.TRUE).count();
    }
  }
}
