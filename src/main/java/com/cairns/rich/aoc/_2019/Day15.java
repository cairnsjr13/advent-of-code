package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2019.IntCode.State;
import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import org.apache.commons.lang3.mutable.MutableObject;

class Day15 extends Base2019 {
  private static final Map<CardDir, Long> dirCodes = Map.of(
      CardDir.North, 1L,
      CardDir.South, 2L,
      CardDir.West, 3L,
      CardDir.East, 4L
  );

  @Override
  protected Object part1(Loader loader) {
    return getAnswer(
        loader,
        (state, oxygenSystem) -> getMinStepsToOxygenSystem(state, oxygenSystem, new HashMap<>(), ImmutablePoint.origin, 0)
    );
  }

  @Override
  protected Object part2(Loader loader) {
    return getAnswer(
        loader,
        (state, oxygenSystem) -> {
          HashMap<ImmutablePoint, Character> grid = new HashMap<>();
          getMinStepsToOxygenSystem(state, oxygenSystem, grid, ImmutablePoint.origin, 0);
          return getTimeToFillSpace(oxygenSystem, grid);
        }
    );
  }

  private int getAnswer(Loader loader, BiFunction<State, MutableObject<ImmutablePoint>, Integer> toAnswer) {
    List<Long> program = IntCode.parseProgram(loader);
    return toAnswer.apply(IntCode.run(program), new MutableObject<>());
  }

  private int getMinStepsToOxygenSystem(
      State state,
      MutableObject<ImmutablePoint> oxygenSystem,
      Map<ImmutablePoint, Character> grid,
      ImmutablePoint location,
      int numSteps
  ) {
    int minSteps = Integer.MAX_VALUE;
    if (!grid.containsKey(location)) {
      grid.put(location, ' ');
      for (CardDir dir : dirCodes.keySet()) {
        ImmutablePoint newLocation = location.move(dir);
        state.programInput.put(dirCodes.get(dir));
        long result = state.programOutput.take();
        if (result == 0) {
          grid.put(newLocation, '#');
          continue;
        }
        else if (result == 2) {
          oxygenSystem.setValue(newLocation);
          minSteps = numSteps + 1;
        }
        minSteps = Math.min(
            minSteps,
            getMinStepsToOxygenSystem(state, oxygenSystem, grid, newLocation, numSteps + 1)
        );
        state.programInput.put(dirCodes.get(dir.turnLeft().turnLeft()));
        long expectNot0 = state.programOutput.take();
        Preconditions.checkState(expectNot0 != 0, expectNot0);
      }
    }
    return minSteps;
  }

  private int getTimeToFillSpace(MutableObject<ImmutablePoint> oxygenSystem, Map<ImmutablePoint, Character> grid) {
    Set<ImmutablePoint> locationsWithOxygen = new HashSet<>();
    Set<ImmutablePoint> locationsToExpandFrom = new HashSet<>();
    locationsToExpandFrom.add(oxygenSystem.getValue());
    for (int t = 0; true; ++t) {
      Set<ImmutablePoint> nextLocationsToExpandFrom = new HashSet<>();
      for (ImmutablePoint locationToExpandFrom : locationsToExpandFrom) {
        locationsWithOxygen.add(locationToExpandFrom);
        for (CardDir dir : dirCodes.keySet()) {
          ImmutablePoint nextLocation = locationToExpandFrom.move(dir);
          if (!locationsWithOxygen.contains(nextLocation) &&
              !locationsToExpandFrom.contains(nextLocation) &&
              (grid.get(nextLocation) == ' ')
          ) {
            nextLocationsToExpandFrom.add(nextLocation);
          }
        }
      }
      if (nextLocationsToExpandFrom.isEmpty()) {
        return t;
      }
      locationsToExpandFrom.clear();
      locationsToExpandFrom.addAll(nextLocationsToExpandFrom);
    }
  }
}
