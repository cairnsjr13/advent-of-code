package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * We need to help a packet follow a path and pick letters up.  This is pretty straightforward line walking.
 */
class Day19 extends Base2017 {
  private static final List<Function<ReadDir, ReadDir>> moveOptions = List.of(
      Function.identity(),
      ReadDir::turnLeft,
      ReadDir::turnRight
  );

  /**
   * Returns the order of letters the packet encounters while walking the path.
   */
  @Override
  protected Object part1(Loader loader) {
    return doSteps(loader, (state) -> state.seen);
  }

  /**
   * Returns the number of steps the packet takes while walking the path.
   */
  @Override
  protected Object part2(Loader loader) {
    return doSteps(loader, (state) -> state.steps);
  }

  /**
   * Walks the path described by the input and keeps track of the letters seen and steps taken.
   * Will return the answer based on the given toAnswer function.
   */
  private <T> T doSteps(Loader loader, Function<State, T> toAnswer) {
    State state = new State(loader);
    for (ReadDir dir = ReadDir.Down; dir != null; ++state.steps) {
      char ch = state.currentChar();
      if (Character.isAlphabetic(ch)) {
        state.seen.append(ch);
      }
      dir = getDirToContinue(state, dir);
    }
    return toAnswer.apply(state);
  }

  /**
   * Finds the direction that the packet should make its next step.  It will try to go
   * forward (the same direction), then left, then right.  Returns null when its at the end.
   */
  private ReadDir getDirToContinue(State state, ReadDir curDir) {
    for (Function<ReadDir, ReadDir> moveOption : moveOptions) {
      ReadDir tryDir = moveOption.apply(curDir);
      state.location.move(tryDir);
      if (state.isLocationValid() && (state.currentChar() != ' ')) {
        return tryDir;
      }
      state.location.move(tryDir.turnAround());
    }
    return null;
  }

  /**
   * State object to keep track of the letters we have seen along with the number of steps taken.
   */
  private static class State {
    private final List<List<Integer>> grid;
    private final MutablePoint location;
    private final StringBuilder seen = new StringBuilder();
    private int steps;

    private State(Loader loader) {
      this.grid = loader.ml((line) -> line.chars().boxed().collect(Collectors.toList()));
      this.location = new MutablePoint(grid.get(0).indexOf((int) '|'), 0);
    }

    /**
     * Helper method to fetch the character in the grid at the given location.
     */
    private char currentChar() {
      return (char) grid.get(location.y()).get(location.x()).intValue();
    }

    /**
     * Helper method to determine if the current location is on the grid and on the path.
     */
    private boolean isLocationValid() {
      return (0 <= location.y()) && (location.y() < grid.size())
          && (0 <= location.x()) && (location.x() < grid.get(0).size());
    }
  }
}
