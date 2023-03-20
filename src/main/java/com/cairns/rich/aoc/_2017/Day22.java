package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

class Day22 extends Base2017 {
  private static final Map<Status, Function<ReadDir, ReadDir>> simpleTurnLookup = Map.of(
      Status.Clean, ReadDir::turnLeft,
      Status.Infected, ReadDir::turnRight
  );
  private static final Map<Status, Function<ReadDir, ReadDir>> complexTurnLookup = Map.of(
      Status.Clean, ReadDir::turnLeft,
      Status.Weakened, Function.<ReadDir>identity(),
      Status.Infected, ReadDir::turnRight,
      Status.Flagged, ReadDir::turnAround
  );
  private static final IntFunction<Function<Status, Status>> statusLookupFactory =
      (jump) -> (before) -> safeGet(EnumUtils.enumValues(Status.class), before.ordinal() + jump);

  @Override
  protected Object part1(Loader2 loader) {
    return getNumInfectionsAfterBursts(10_000, loader, simpleTurnLookup, statusLookupFactory.apply(2));
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getNumInfectionsAfterBursts(10_000_000, loader, complexTurnLookup, statusLookupFactory.apply(1));
  }

  private int getNumInfectionsAfterBursts(
      int numBursts,
      Loader2 loader,
      Map<Status, Function<ReadDir, ReadDir>> turnLookup,
      Function<Status, Status> statusLookup
  ) {
    Map<ImmutablePoint, Status> grid = parseGrid(loader.ml());
    ImmutablePoint location = ImmutablePoint.origin;
    ReadDir facing = ReadDir.Up;
    int numInfections = 0;
    for (int i = 0; i < numBursts; ++i) {
      Status statusBefore = grid.getOrDefault(location, Status.Clean);
      Status statusAfter = statusLookup.apply(statusBefore);
      facing = turnLookup.get(statusBefore).apply(facing);
      updateGrid(grid, location, statusAfter);
      if (statusAfter == Status.Infected) {
        ++numInfections;
      }
      location = location.move(facing);
    }
    return numInfections;
  }

  private void updateGrid(Map<ImmutablePoint, Status> grid, ImmutablePoint location, Status statusAfter) {
    if (statusAfter == Status.Clean) {
      grid.remove(location);
    }
    else {
      grid.put(location, statusAfter);
    }
  }

  private Map<ImmutablePoint, Status> parseGrid(List<String> lines) {
    Map<ImmutablePoint, Status> grid = new HashMap<>();
    for (int row = 0; row < lines.size(); ++row) {
      String line = lines.get(row);
      for (int col = 0; col < line.length(); ++col) {
        if (line.charAt(col) == '#') {
          grid.put(new ImmutablePoint(col - (line.length() / 2), row - (lines.size() / 2)), Status.Infected);
        }
      }
    }
    return grid;
  }

  private enum Status {
    Clean,
    Weakened,
    Infected,
    Flagged;
  }
}
