package com.cairns.rich.aoc._2016;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

class Day24 extends Base2016 {
  @Override
  protected void run() {
    State state = new State(fullLoader.ml());
    Table<Integer, Integer, Integer> minStepsFromTo = calculateMinStepsFromTo(state);
    
    List<Integer> marksToBePlaced = new ArrayList<>(minStepsFromTo.columnKeySet());
    marksToBePlaced.remove(0);
    List<Integer> placedMarks = new ArrayList<>(Arrays.asList(0));
    System.out.println(findMinSteps(minStepsFromTo, marksToBePlaced, placedMarks, 0, false));
    System.out.println(findMinSteps(minStepsFromTo, marksToBePlaced, placedMarks, 0, true));
  }
  
  private int findMinSteps(
      Table<Integer, Integer, Integer> minStepsFromTo,
      List<Integer> marksToBePlaced,
      List<Integer> placedMarks,
      int stepsToHere,
      boolean withReturn
  ) {
    if (marksToBePlaced.isEmpty()) {
      return stepsToHere
           + ((withReturn) ? minStepsFromTo.get(placedMarks.get(placedMarks.size() - 1), 0) : 0);
    }
    int minSteps = Integer.MAX_VALUE;
    for (int i = 0; i < marksToBePlaced.size(); ++i) {
      int markToBePlaced = marksToBePlaced.remove(i);
      int stepsToMark = minStepsFromTo.get(placedMarks.get(placedMarks.size() - 1), markToBePlaced);
      placedMarks.add(markToBePlaced);
      minSteps = Math.min(
          minSteps,
          findMinSteps(minStepsFromTo, marksToBePlaced, placedMarks, stepsToHere + stepsToMark, withReturn)
      );
      placedMarks.remove(placedMarks.size() - 1);
      marksToBePlaced.add(i, markToBePlaced);
    }
    return minSteps;
  }
  
  private Table<Integer, Integer, Integer> calculateMinStepsFromTo(State state) {
    Table<Integer, Integer, Integer> minStepsFromTo = TreeBasedTable.create();
    for (int mark : state.markToLocation.keySet()) {
      fillInFromMark(state, minStepsFromTo, mark);
    }
    return minStepsFromTo;
  }
  
  private void fillInFromMark(State state, Table<Integer, Integer, Integer> minStepsFromTo, int mark) {
    int[][] minStepsFromMark = new int[state.grid.length][state.grid[0].length];
    Arrays.stream(minStepsFromMark).forEach((row) -> Arrays.fill(row, -1));
    Queue<Integer> locationsToConsider = new ArrayDeque<>();
    int markLocation = state.markToLocation.get(mark);
    minStepsFromMark[row(markLocation)][col(markLocation)] = 0;
    locationsToConsider.add(markLocation);
    while (!locationsToConsider.isEmpty()) {
      int locationToConsider = locationsToConsider.poll();
      int row = row(locationToConsider);
      int col = col(locationToConsider);
      int stepsToHere = minStepsFromMark[row][col];
      char ch = state.grid[row][col];
      if (Character.isDigit(ch)) {
        minStepsFromTo.put(mark, ch - '0', stepsToHere);
      }
      for (int dr = -1; dr <= 1; ++dr) {
        for (int dc = -1; dc <= 1; ++dc) {
          if ((dr == 0) ^ (dc == 0)) {
            int newRow = row + dr;
            int newCol = col + dc;
            if (minStepsFromMark[newRow][newCol] == -1) {
              int newSteps = stepsToHere + 1;
              char newCh = state.grid[newRow][newCol];
              minStepsFromMark[newRow][newCol] = newSteps;
              if (newCh != '#') {
                locationsToConsider.add(encodedLocation(newRow, newCol));
              }
            }
          }
        }
      }
    }
  }

  private int encodedLocation(int row, int col) {
    return (row << 0) | (col << 8);
  }
  
  private int row(int encodedLocation) {
    return (encodedLocation >> 0) & 0xff;
  }
  
  private int col(int encodedLocation) {
    return (encodedLocation >> 8) & 0xff;
  }
  
  private class State {
    private final Map<Integer, Integer> markToLocation = new HashMap<>();
    private final char[][] grid;
    
    private State(List<String> lines) {
      this.grid = new char[lines.size()][lines.get(0).length()];
      for (int row = 0; row < grid.length; ++row) {
        for (int col = 0; col < grid[0].length; ++col) {
          char ch = lines.get(row).charAt(col);
          grid[row][col] = ch;
          if (Character.isDigit(ch)) {
            markToLocation.put(ch - '0', encodedLocation(row, col));
          }
        }
      }
    }
  }
}
