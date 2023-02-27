package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import org.apache.commons.lang3.tuple.Pair;

class Day22 extends Base2018 {
  @Override
  protected void run() {
    executeFor(510, new ImmutablePoint(10, 10));
    executeFor(11739, new ImmutablePoint(11, 718));
  }

  private void executeFor(int depth, ImmutablePoint target) {
    System.out.println(depth + " - " + target);
    StatusMap map = new StatusMap(depth, target);
    System.out.println("Part 1: " + computeRiskLevel(map));
    System.out.println("Part 2: " + minMinutesToFind(map));
    System.out.println();
  }

  private int computeRiskLevel(StatusMap map) {
    int riskLevel = 0;
    for (int y = 0; y <= map.target.y(); ++y) {
      for (int x = 0; x <= map.target.x(); ++x) {
        riskLevel += map.getStatus(x, y).risk;
      }
    }
    return riskLevel;
  }

  private long minMinutesToFind(StatusMap map) {
    Table<ImmutablePoint, Tool, Integer> visited = HashBasedTable.create();
    return bfs( // TODO: have custom step sizes in base function
        new State(new ImmutablePoint(0, 0), Tool.Torch, 0),
        (s) -> s.location.equals(map.target) && (s.equiped == Tool.Torch),
        (ss) -> ss.state.weight(map),
        (current, registrar) -> {
          Status status = map.getStatus(current.location);
          for (Tool tool : EnumUtils.enumValues(Tool.class)) {
            if (tool.validIn.contains(status)) {
              tryRoute(registrar, visited, current.location, tool, current.minutesSpent + 7);
            }
          }
          for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
            ImmutablePoint move = current.location.move(dir);
            if (current.equiped.validIn.contains(map.getStatus(move))) {
              tryRoute(registrar, visited, move, current.equiped, current.minutesSpent + 1);
            }
          }
        }
    ).get().state.minutesSpent;
  }

  private void tryRoute(
      Consumer<State> registrar,
      Table<ImmutablePoint, Tool, Integer> visited,
      ImmutablePoint location,
      Tool tool,
      int nextMinutes
  ) {
    if (!visited.contains(location, tool) || (nextMinutes < visited.get(location, tool))) {
      registrar.accept(new State(location, tool, nextMinutes));
      visited.put(location, tool, nextMinutes);
    }
  }

  private static class State {
    private final ImmutablePoint location;
    private final Tool equiped;
    private final int minutesSpent;

    private State(ImmutablePoint location, Tool equiped, int minutesSpent) {
      this.location = location;
      this.equiped = equiped;
      this.minutesSpent = minutesSpent;
    }

    private int weight(StatusMap map) {
      return minutesSpent + ((equiped == Tool.Torch) ? 0 : 7)
           + Math.abs(map.target.x() - location.x())
           + Math.abs(map.target.y() - location.y());
    }
  }

  private static class StatusMap {
    private final Table<Integer, Integer, Pair<Integer, Status>> map = HashBasedTable.create();

    private final IntUnaryOperator geoIndexToErosionLevel;
    private final ImmutablePoint target;

    private StatusMap(int depth, ImmutablePoint target) {
      this.geoIndexToErosionLevel = (geoIndex) -> (geoIndex + depth) % 20183;
      this.target = target;
    }

    private Status getStatus(ImmutablePoint location) {
      return getStatus(location.x(), location.y());
    }

    private Status getStatus(int x, int y) {
      return ((0 <= x) && (0 <= y)) ? load(x, y).getRight() : Status.Blocked;
    }

    private Pair<Integer, Status> load(int x, int y) {
      if (!map.contains(x, y)) {
        int erosion = geoIndexToErosionLevel.applyAsInt(computeGeoIndex(x, y));
        map.put(x, y, Pair.of(erosion, EnumUtils.enumValues(Status.class)[erosion % 3]));
      }
      return map.get(x, y);
    }

    private int computeGeoIndex(int x, int y) {
      if (((x == 0) && (y == 0)) || (x == target.x() && (y == target.y()))) {
        return 0;
      }
      else if (y == 0) {
        return 16807 * x;
      }
      else if (x == 0) {
        return 48271 * y;
      }
      return load(x, y - 1).getLeft() * load(x - 1, y).getLeft();
    }
  }

  private enum Status {
    Rocky(0),
    Wet(1),
    Narrow(2),
    Blocked(0);

    private final int risk;

    private Status(int risk) {
      this.risk = risk;
    }
  }

  private enum Tool {
    None(Status.Wet, Status.Narrow),
    Torch(Status.Rocky, Status.Narrow),
    ClimbingGear(Status.Rocky, Status.Wet);

    private final Set<Status> validIn;

    private Tool(Status... validIn) {
      this.validIn = Set.of(validIn);
    }
  }
}
