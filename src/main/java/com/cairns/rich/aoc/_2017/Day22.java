package com.cairns.rich.aoc._2017;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;

class Day22 extends Base2017 {
  private static final Map<Status, Function<RelDir, RelDir>> simpleTurnLookup = Map.of(
      Status.Clean, RelDir::turnLeft,
      Status.Infected, RelDir::turnRight
  );
  private static final Map<Status, Function<RelDir, RelDir>> complexTurnLookup = Map.of(
      Status.Clean, RelDir::turnLeft,
      Status.Weakened, Function.<RelDir>identity(),
      Status.Infected, RelDir::turnRight,
      Status.Flagged, RelDir::turnAround
  );
  private static final IntFunction<Function<Status, Status>> statusLookupFactory =
      (jump) -> (before) -> safeGet(EnumUtils.enumValues(Status.class), before.ordinal() + jump);
  
  @Override
  protected void run() {
    Map<ImmutablePoint, Status> init = parseGrid(fullLoader.ml());
    System.out.println(getNumInfectionsAfterBursts(10_000, init, simpleTurnLookup, statusLookupFactory.apply(2)));
    System.out.println(getNumInfectionsAfterBursts(10_000_000, init, complexTurnLookup, statusLookupFactory.apply(1)));
  }
  
  private int getNumInfectionsAfterBursts(
      int numBursts,
      Map<ImmutablePoint, Status> init,
      Map<Status, Function<RelDir, RelDir>> turnLookup,
      Function<Status, Status> statusLookup
  ) {
    State state = new State(new HashMap<>(init));
    int numInfections = 0;
    for (int i = 0; i < numBursts; ++i) {
      Status statusBefore = state.locationStatus();
      state.facing = turnLookup.get(statusBefore).apply(state.facing);
      state.setStatus(statusLookup.apply(statusBefore));
      if (state.locationStatus() == Status.Infected) {
        ++numInfections;
      }
      state.location = state.location.move(state.facing);
    }
    return numInfections;
  }
  
  
  private Map<ImmutablePoint, Status> parseGrid(List<String> lines) {
    Map<ImmutablePoint, Status> grid = new HashMap<>();
    for (int row = 0; row < lines.size(); ++row) {
      String line = lines.get(row);
      for (int col = 0; col < line.length(); ++col) {
        if (line.charAt(col) == '#') {
          grid.put(new ImmutablePoint((-line.length() / 2) + col, (lines.size() / 2) - row), Status.Infected);
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
  
  private static class State {
    private final Map<ImmutablePoint, Status> grid;
    private ImmutablePoint location = new ImmutablePoint(0, 0);
    private RelDir facing = RelDir.Up;
    
    private State(Map<ImmutablePoint, Status> grid) {
      this.grid = grid;
    }
    
    private Status locationStatus() {
      return grid.getOrDefault(location, Status.Clean);
    }
    
    private void setStatus(Status status) {
      if (status == Status.Clean) {
        grid.remove(location);
      }
      else {
        grid.put(location, status);
      }
    }
  }
}
