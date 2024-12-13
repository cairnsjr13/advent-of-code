package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * We have a little hvac robot that can access the rooftop.  With our puzzle input we need to find the
 * fastest way to visit all relevant locations.  Instead of doing a step-by-step map traversal, we can
 * greatly speed this up by computing the minimum distances between each pair of points and then finding
 * the shortest distance with those (target based traversal instead of step based).
 */
class Day24 extends Base2016 {
  /**
   * Returns the minimum number of steps required to start at position 0 and visit every location.
   */
  @Override
  protected Object part1(Loader loader) {
    return findMinSteps(loader, false);
  }

  /**
   * Returns the minimum number of steps required to start at position 0, visit every location, and return to position 0.
   */
  @Override
  protected Object part2(Loader loader) {
    return findMinSteps(loader, true);
  }

  private long findMinSteps(Loader loader, boolean withReturn) {
    State state = new State(loader);
    Table<Integer, Integer, Long> minStepsFromTo = calculateMinStepsFromTo(state);
    List<Integer> marksToBePlaced = new ArrayList<>(minStepsFromTo.columnKeySet());
    marksToBePlaced.remove(0);
    List<Integer> placedMarks = new ArrayList<>(Arrays.asList(0));
    return findMinSteps(minStepsFromTo, marksToBePlaced, placedMarks, 0, withReturn);
  }

  /**
   * Uses a depth first search, recursive algorithm to find the ordering of locations that results in the minimum total steps.
   * If the withReturn flag is passed in, the algorithm will add the distance of a return to 0 trip at the end of each path.
   */
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

  /**
   * Computes a lookup table that holds the minimum number of steps to go from one location to another.
   * table[a, b] == table[b, a]
   */
  private Table<Integer, Integer, Long> calculateMinStepsFromTo(State state) {
    Table<Integer, Integer, Long> minStepsFromTo = TreeBasedTable.create();
    for (int mark : state.markToLocation.keySet()) {
      fillInFromMark(state, minStepsFromTo, mark);
    }
    return minStepsFromTo;
  }

  /**
   * Uses a bfs to find the minimum distance every location is from the given location mark.
   * There is no target location as we need to search the whole grid.
   */
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

  /**
   * Container class holding the map as well as the locations of each position we find.
   */
  private class State {
    private final Map<Integer, ImmutablePoint> markToLocation = new HashMap<>();
    private final char[][] grid;

    private State(Loader loader) {
      this.grid = loader.ml(String::toCharArray).toArray(char[][]::new);
      for (int y = 0; y < grid.length; ++y) {
        for (int x = 0; x < grid[0].length; ++x) {
          char ch = grid[y][x];
          if (Character.isDigit(ch)) {
            markToLocation.put(ch - '0', new ImmutablePoint(x, y));
          }
        }
      }
    }
  }
}
