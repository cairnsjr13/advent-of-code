package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class Day19 extends Base2017 {
  private static final List<Function<ReadDir, ReadDir>> moveOptions = List.of(
      Function.identity(),
      ReadDir::turnLeft,
      ReadDir::turnRight
  );

  @Override
  protected Object part1(Loader2 loader) {
    return doSteps(loader, (state) -> state.seen);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return doSteps(loader, (state) -> state.steps);
  }

  private <T> T doSteps(Loader2 loader, Function<State, T> toAnswer) {
    List<List<Integer>> grid = loader.ml((line) -> line.chars().boxed().collect(Collectors.toList()));
    MutablePoint location = new MutablePoint(grid.get(0).indexOf((int) '|'), 0);
    State state = new State();
    for (ReadDir dir = ReadDir.Down; dir != null; ++state.steps) {
      char ch = charAt(grid, location);
      if (Character.isAlphabetic(ch)) {
        state.seen.append(ch);
      }
      dir = getDirToContinue(grid, location, dir);
    }
    return toAnswer.apply(state);
  }

  private ReadDir getDirToContinue(List<List<Integer>> grid, MutablePoint location, ReadDir curDir) {
    for (Function<ReadDir, ReadDir> moveOption : moveOptions) {
      ReadDir tryDir = moveOption.apply(curDir);
      location.move(tryDir);
      if (   (0 <= location.y()) && (location.y() < grid.size())
          && (0 <= location.x()) && (location.x() < grid.get(0).size())
          && (charAt(grid, location) != ' '))
      {
        return tryDir;
      }
      location.move(tryDir.turnAround());
    }
    return null;
  }

  private char charAt(List<List<Integer>> grid, MutablePoint location) {
    return (char) grid.get(location.y()).get(location.x()).intValue();
  }

  private static class State {
    private final StringBuilder seen = new StringBuilder();
    private int steps;
  }
}
