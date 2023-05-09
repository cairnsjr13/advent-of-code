package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
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
 * Microchips and generators need to be brought up to the fourth floor... But the generators
 * are unsafe unless they are hooked up to their microchips.  On top of this, the elevator
 * will only function if there is something in the elevator.  We need to figure out the minimum
 * number of moves required to get all of the items to the top floor.  The key optimization here
 * is that we don't actually care what the element distinctions are and states are equivalent if
 * they have chip/gen in the same positions.  ie ( [h|e|H|E] is the same as [e|h|E|H] ).
 */
class Day11 extends Base2016 {
  private static final int[] DIRS = { 1, -1 };
  private static final int ELEVATOR_FLOOR_MASK = 0b11;
  private static final int NUM_FLOORS = 4;
  private static final int LOCATION_COUNT_BITS = 3;
  private static final int LOCATION_COUNT_MASK = 0b111;

  /**
   * Computes the minimum number of moves required to move all gens/chips described in the input to the top floor.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeMinMoves(loader.ml(Location::new));
  }

  /**
   * Computes the minimum number of moves required to move all gens/chips described
   * in the input as well as two additional sets of gens/chips to the top floor.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Location> locations = loader.ml(Location::new);
    locations.add(new Location(0, 0));
    locations.add(new Location(0, 0));
    return computeMinMoves(locations);
  }

  /**
   * Uses breadth first search to find the minimum number of moves to get all components to the top floor.
   * To explore the step space, we consider every pair of locations (including duplicates) and trying to move each
   * component (chip/generator) in each direction.  The vast majority of these moves will not be valid because the
   * components in question will not be on the same floor as the elevator or the moves will result in an unsafe state.
   */
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

  /**
   * Computes a new state using the given changes.  A state will only be considered if:
   *   - the new elevator floor is valid (in range)
   *   - the two components being moved are on the old elevator floor
   *   - the resulting state would be {@link #isSafe(List)}
   * Note that first and second are allowed to be the same location as are the fields.
   * This represents moving both components of an element (same location different field)
   * or moving only one component (same location same field).
   */
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

  /**
   * Returns true if the given locations to not violate the constraints of the gen/chips.
   * A state is considered unsafe if a generator is not accompanied by its chip AND it is
   * located on a floor with a different chip.
   */
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

  /**
   * Returns true if every location has both the generator and chip on the top floor.
   */
  private boolean isDone(List<Location> locations) {
    for (Location location : locations) {
      if ((location.genFloor != (NUM_FLOORS - 1)) || (location.chipFloor != (NUM_FLOORS - 1))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Computes a heuristic for a searchState based on the theoretical minimum number of moves required
   * to get to a success state.  This is simply the number of floors each element must move.
   */
  private int getPriority(SearchState<Long> searchState) {
    int numMovesMoreMin = 0;
    for (Location location : getLocations(searchState.state)) {
      numMovesMoreMin += (NUM_FLOORS - location.genFloor - 1)
                       + (NUM_FLOORS - location.chipFloor - 1);
    }
    return (int) (searchState.getNumSteps() + numMovesMoreMin);
  }

  /**
   * Decodes the elevator floor from the given state.
   *
   * @see #buildState(long, List) for encoding
   */
  private long getElevatorFloor(long state) {
    return state & ELEVATOR_FLOOR_MASK;
  }

  /**
   * Decodes the element locations from the given state.
   *
   * @see #buildState(long, List) for encoding
   */
  private List<Location> getLocations(long state) {
    List<Location> locations = new ArrayList<>();
    for (int genFloor = 0; genFloor < NUM_FLOORS; ++genFloor) {
      for (int chipFloor = 0; chipFloor < NUM_FLOORS; ++chipFloor) {
        int offset = Location.computeOffset(genFloor, chipFloor);
        long count = (state >> offset) & LOCATION_COUNT_MASK;
        for (int i = 0; i < count; ++i) {
          locations.add(new Location(genFloor, chipFloor));
        }
      }
    }
    return locations;
  }

  /**
   * Encodes the given state into a primitive long that uniquely describes the situation.
   * The elevator floor is represented by the 2 least significant bits since there are only 4 floors.
   * After that is a list of counts describing how many elementLocations fall into that group.
   * For example the first count is the number of elements whos generator is on floor 0 and microchip
   * is on floor 0.  There are 16 counts as there are 4 floors for each generator/microchip (4x4=16).
   * Each count is represented by 3 bits, which allows up to 7 distinct elements.  If there are ever
   * more than 7 elements, the count will need to be increased to 4 bits, which a long cannot contain.
   *
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
  private long buildState(long elevatorFloor, List<Location> locations) {
    long state = elevatorFloor;
    Multiset<Location> locationSet = HashMultiset.create(locations);
    for (Location location : locationSet.elementSet()) {
      state |= (((long) locationSet.count(location)) << location.computeOffset());
    }
    return state;
  }

  /**
   * Container class describing the generator and chip location for a given element.
   * Note that the actual element is irrelevant as they all behave the same.
   */
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

    /**
     * Computes the bit-packed offset for this location based on its generator floor and chip floor.
     */
    private int computeOffset() {
      return computeOffset(genFloor, chipFloor);
    }

    /**
     * Computes the bit-packed offset for the given generator floor and chip floor.
     */
    private static int computeOffset(long genFloor, long chipFloor) {
      return (int) (2 + LOCATION_COUNT_BITS * (NUM_FLOORS * genFloor + chipFloor));
    }
  }

  /**
   * Convenience descriptor to allow programmatic getter/setter access to generator or chip floor fields of a {@link Location}.
   */
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
