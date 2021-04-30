package com.cairns.rich.aoc._2017;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.RelDir;

class Day19 extends Base2017 {
  private static final List<Function<RelDir, RelDir>> moveOptions = List.of(
      Function.identity(),
      RelDir::turnLeft,
      RelDir::turnRight
  ); 
  
  @Override
  protected void run() {
    List<List<Integer>> grid = fullLoader.ml((line) -> line.chars().boxed().collect(Collectors.toList()));
    MutablePoint location = new MutablePoint(grid.get(0).indexOf((int) '|'), 0);
    StringBuilder seen = new StringBuilder();
    int steps = 0;
    for (RelDir dir = RelDir.Down; dir != null; ++steps) {
      char ch = charAt(grid, location);
      if (Character.isAlphabetic(ch)) {
        seen.append(ch);
      }
      dir = getDirToContinue(grid, location, dir);
    }
    System.out.println(seen);
    System.out.println(steps);
  }
  
  private RelDir getDirToContinue(List<List<Integer>> grid, MutablePoint location, RelDir curDir) {
    for (Function<RelDir, RelDir> moveOption : moveOptions) {
      RelDir tryDir = moveOption.apply(curDir);
      location.move(tryDir);
      if (   (0 <= -location.y()) && (-location.y() < grid.size())
          && (0 <= location.x()) && (location.x() < grid.get(0).size())
          && (charAt(grid, location) != ' '))
      {
        return tryDir;
      }
      location.move(tryDir.turnLeft().turnLeft());
    }
    return null;
  }
  
  private char charAt(List<List<Integer>> grid, MutablePoint location) {
    return (char) grid.get(-location.y()).get(location.x()).intValue();
  }
}
