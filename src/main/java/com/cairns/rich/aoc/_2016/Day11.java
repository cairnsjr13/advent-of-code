package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 6666555555555544444444443333333333222222222211111111110000000000
 * 3210987654321098765432109876543210987654321098765432109876543210
 *               RRRQQQPPPOOONNNMMMLLLKKKJJJIIIHHHGGGFFFEEEDDDCCCAA
 *
 * A - elevator floor       -> offset  0
 *
 *     (gen, chip) counts   -> offset  [2+3*(4g+c)]
 * C - (  0,    0) count    -> offset  2
 * D - (  0,    1) count    -> offset  5
 * E - (  0,    2) count    -> offset  8
 * F - (  0,    3) count    -> offset 11
 * G - (  1,    0) count    -> offset 14
 * H - (  1,    1) count    -> offset 17
 * I - (  1,    2) count    -> offset 20
 * J - (  1,    3) count    -> offset 23
 * K - (  2,    0) count    -> offset 26
 * L - (  2,    1) count    -> offset 29
 * M - (  2,    2) count    -> offset 32
 * N - (  2,    3) count    -> offset 35
 * O - (  3,    0) count    -> offset 38
 * P - (  3,    1) count    -> offset 41
 * Q - (  3,    2) count    -> offset 44
 * R - (  3,    3) count    -> offset 47
 */
class Day11 extends Base2016 {
  private static final int[] DIRS = { 1, -1 };
  private static final int ELEVATOR_FLOOR_MASK = 0b11;
  private static final int NUM_FLOORS = 4;
  private static final int LOCATION_COUNT_BITS = 3;
  private static final int LOCATION_COUNT_MASK = 0b111;

  @Override
  protected Object part1(Loader2 loader) {
    return computeMinMoves(loader.ml(Location::new));
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Location> locations = loader.ml(Location::new);
    locations.add(new Location(0, 0));
    locations.add(new Location(0, 0));
    return computeMinMoves(locations);
  }

  private long computeMinMoves(List<Location> initLocations) {
    return bfs(
        buildState(0, initLocations),
        (s) -> isDone(getLocations(s)),
        this::getPriority,
        (candidate, registrar) -> {
          long elevatorFloor = getElevatorFloor(candidate);
          List<Location> locations = getLocations(candidate);
          for (int i = 0; i < locations.size(); ++i) {
            Location first = locations.get(i);
            for (int j = i; j < locations.size(); ++j) {
              Location second = locations.get(j);
              for (int dir : DIRS) {
                for (Field field1 : EnumUtils.enumValues(Field.class)) {
                  for (Field field2 : EnumUtils.enumValues(Field.class)) {
                    registerAllOptions(registrar, elevatorFloor, dir, locations, first, field1, second, field2);
                  }
                }
              }
            }
          }
        }
    ).get().getNumSteps();
  }

  private void registerAllOptions(
      Consumer<Long> registrar,
      long oldElevatorFloor,
      long dir,
      List<Location> oldLocations,
      Location first,
      Field field1,
      Location second,
      Field field2
  ) {
    long newElevatorFloor = oldElevatorFloor + dir;
    if ((0 <= newElevatorFloor) && (newElevatorFloor < NUM_FLOORS)) {
      if ((field1.getter.applyAsLong(first) == oldElevatorFloor) &&
          (field2.getter.applyAsLong(second) == oldElevatorFloor))
      {
        List<Location> newLocations = new ArrayList<>();
        Location newFirst = new Location(first.genFloor, first.chipFloor);
        newLocations.add(newFirst);
        Location newSecond;
        if (first == second) {
          newSecond = newFirst;
        }
        else {
          newSecond = new Location(second.genFloor, second.chipFloor);
          newLocations.add(newSecond);
        }
        field1.setter.accept(newFirst, newElevatorFloor);
        field2.setter.accept(newSecond, newElevatorFloor);
        for (Location oldLocation : oldLocations) {
          if ((oldLocation != first) && (oldLocation != second)) {
            newLocations.add(new Location(oldLocation.genFloor, oldLocation.chipFloor));
          }
        }
        if (isSafe(newLocations)) {
          registrar.accept(buildState(newElevatorFloor, newLocations));
        }
      }
    }
  }

  private boolean isSafe(List<Location> locations) {
    for (Location location : locations) {
      if (location.genFloor != location.chipFloor) {
        for (Location potentiallyDangerous : locations) {
          if ((location != potentiallyDangerous) && (potentiallyDangerous.genFloor == location.chipFloor)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private boolean isDone(List<Location> locations) {
    for (Location location : locations) {
      if ((location.genFloor != (NUM_FLOORS - 1)) || (location.chipFloor != (NUM_FLOORS - 1))) {
        return false;
      }
    }
    return true;
  }

  private int getPriority(SearchState<Long> searchState) {
    int numMovesMoreMin = 0;
    for (Location location : getLocations(searchState.state)) {
      numMovesMoreMin += (NUM_FLOORS - location.genFloor - 1)
                       + (NUM_FLOORS - location.chipFloor - 1);
    }
    return (int) (searchState.getNumSteps() + numMovesMoreMin);
  }

  private long getElevatorFloor(long state) {
    return state & ELEVATOR_FLOOR_MASK;
  }

  private List<Location> getLocations(long state) {
    List<Location> locations = new ArrayList<>();
    for (int genFloor = 0; genFloor < NUM_FLOORS; ++genFloor) {
      for (int chipFloor = 0; chipFloor < NUM_FLOORS; ++chipFloor) {
        int offset = Location.computeOffset(genFloor, chipFloor);
        int count = (int) ((state >> offset) & LOCATION_COUNT_MASK);
        for (int i = 0; i < count; ++i) {
          locations.add(new Location(genFloor, chipFloor));
        }
      }
    }
    return locations;
  }

  private long buildState(long elevatorFloor, List<Location> locations) {
    long state = elevatorFloor;
    Multiset<Location> locationSet = HashMultiset.create(locations);
    for (Location location : locationSet.elementSet()) {
      state |= (((long) locationSet.count(location)) << location.computeOffset());
    }
    return state;
  }

  private static class Location {
    private static final Pattern pattern = Pattern.compile("^gen=(\\d+) chip=(\\d+)$");

    private long genFloor;
    private long chipFloor;

    private Location(String line) {
      Matcher matcher = matcher(pattern, line);
      this.genFloor = num(matcher, 1);
      this.chipFloor = num(matcher, 2);
    }

    private Location(long genFloor, long chipFloor) {
      this.genFloor = genFloor;
      this.chipFloor = chipFloor;
    }

    @Override
    public boolean equals(Object obj) {
      Location other = (Location) obj;
      return (genFloor == other.genFloor)
          && (chipFloor == other.chipFloor);
    }

    @Override
    public int hashCode() {
      return (int) ((genFloor << 3) + chipFloor);
    }

    @Override
    public String toString() {
      return "[" + genFloor + ", " + chipFloor + "]";
    }

    private int computeOffset() {
      return computeOffset(genFloor, chipFloor);
    }

    private static int computeOffset(long genFloor, long chipFloor) {
      return (int) (2 + LOCATION_COUNT_BITS * (NUM_FLOORS * genFloor + chipFloor));
    }
  }

  private enum Field {
    GenFloor((location) -> location.genFloor, (location, floor) -> location.genFloor = floor),
    ChipFloor((location) -> location.chipFloor, (location, floor) -> location.chipFloor = floor);

    private final ToLongFunction<Location> getter;
    private final BiConsumer<Location, Long> setter;

    private Field(ToLongFunction<Location> getter, BiConsumer<Location, Long> setter) {
      this.getter = getter;
      this.setter = setter;
    }
  }
}
