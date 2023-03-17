package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Day24 extends Base2016 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    State state = new State(loader.ml());
    Table<Integer, Integer, Long> minStepsFromTo = calculateMinStepsFromTo(state);

    List<Integer> marksToBePlaced = new ArrayList<>(minStepsFromTo.columnKeySet());
    marksToBePlaced.remove(0);
    List<Integer> placedMarks = new ArrayList<>(Arrays.asList(0));
    result.part1(findMinSteps(minStepsFromTo, marksToBePlaced, placedMarks, 0, false));
    result.part2(findMinSteps(minStepsFromTo, marksToBePlaced, placedMarks, 0, true));
  }

  private long findMinSteps(
      Table<Integer, Integer, Long> minStepsFromTo,
      List<Integer> marksToBePlaced,
      List<Integer> placedMarks,
      long stepsToHere,
      boolean withReturn
  ) {
    if (marksToBePlaced.isEmpty()) {
      return stepsToHere
           + ((withReturn) ? minStepsFromTo.get(placedMarks.get(placedMarks.size() - 1), 0) : 0);
    }
    long minSteps = Long.MAX_VALUE;
    for (int i = 0; i < marksToBePlaced.size(); ++i) {
      int markToBePlaced = marksToBePlaced.remove(i);
      long stepsToMark = minStepsFromTo.get(placedMarks.get(placedMarks.size() - 1), markToBePlaced);
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

  private Table<Integer, Integer, Long> calculateMinStepsFromTo(State state) {
    Table<Integer, Integer, Long> minStepsFromTo = TreeBasedTable.create();
    for (int mark : state.markToLocation.keySet()) {
      fillInFromMark(state, minStepsFromTo, mark);
    }
    return minStepsFromTo;
  }

  private void fillInFromMark(State state, Table<Integer, Integer, Long> minStepsFromTo, int mark) {
    bfs(
        state.markToLocation.get(mark),
        (s) -> false,   // search everything
        SearchState::getNumSteps,
        (location, stepsToHere, registrar) -> {
          char ch = state.grid[location.y()][location.x()];
          if (Character.isDigit(ch)) {
            minStepsFromTo.put(mark, ch - '0', stepsToHere);
          }
          for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
            ImmutablePoint next = location.move(dir);
            if (state.grid[next.y()][next.x()] != '#') {
              registrar.accept(next);
            }
          }
        }
    );
  }

  private class State {
    private final Map<Integer, ImmutablePoint> markToLocation = new HashMap<>();
    private final char[][] grid;

    private State(List<String> lines) {
      this.grid = new char[lines.size()][lines.get(0).length()];
      for (int y = 0; y < grid.length; ++y) {
        for (int x = 0; x < grid[0].length; ++x) {
          char ch = lines.get(y).charAt(x);
          grid[y][x] = ch;
          if (Character.isDigit(ch)) {
            markToLocation.put(ch - '0', new ImmutablePoint(x, y));
          }
        }
      }
    }
  }
}
