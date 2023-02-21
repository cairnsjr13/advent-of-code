package com.cairns.rich.aoc._2017;

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
  protected void run() {
    List<List<Integer>> grid = fullLoader.ml((line) -> line.chars().boxed().collect(Collectors.toList()));
    MutablePoint location = new MutablePoint(grid.get(0).indexOf((int) '|'), 0);
    StringBuilder seen = new StringBuilder();
    int steps = 0;
    for (ReadDir dir = ReadDir.Down; dir != null; ++steps) {
      char ch = charAt(grid, location);
      if (Character.isAlphabetic(ch)) {
        seen.append(ch);
      }
      dir = getDirToContinue(grid, location, dir);
    }
    System.out.println(seen);
    System.out.println(steps);
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
}
