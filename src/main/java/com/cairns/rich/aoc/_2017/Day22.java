package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * A virus is walking an infinite grid and causing infection.  We need to track how many nodes get hit.
 */
class Day22 extends Base2017 {
  private static final ConfigToken<Integer> bursts = ConfigToken.of("bursts", Integer::parseInt);
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

  /**
   * Computes the number of bursts (of the configured number) that cause infection following simple infection rules:
   *    - Turn left if current node is clean, right otherwise.
   *    - Flip the infected/clean status of the current node.
   *    - Move forward 1
   */
  @Override
  protected Object part1(Loader loader) {
    return getNumInfectionsAfterBursts(loader, simpleTurnLookup, statusLookupFactory.apply(2));
  }

  /**
   * Computes the number of bursts (of the configured number) that cause infection following complex infection rules:
   *    - Turn left if current node is clean, doesn't turn if weakened, turns right if infected, and turns around if flagged.
   *    - Updates current node status: clean -> weakened -> infected -> flagged -> clean -> weakened...
   *    - Move forward 1
   */
  @Override
  protected Object part2(Loader loader) {
    return getNumInfectionsAfterBursts(loader, complexTurnLookup, statusLookupFactory.apply(1));
  }

  /**
   * Returns the number of bursts that result in infection following the given rules.
   * The virus carrier starts at the origin facing {@link ReadDir#Up}.
   */
  private int getNumInfectionsAfterBursts(
      Loader loader,
      Map<Status, Function<ReadDir, ReadDir>> turnLookup,
      Function<Status, Status> statusLookup
  ) {
    Map<ImmutablePoint, Status> grid = parseGrid(loader.ml());
    int numBursts = loader.getConfig(bursts);
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

  /**
   * Convenience method to update the given location to the appropriate status.
   * Note that a {@link Status#Clean} value will result in the location being removed.
   */
  private void updateGrid(Map<ImmutablePoint, Status> grid, ImmutablePoint location, Status statusAfter) {
    if (statusAfter == Status.Clean) {
      grid.remove(location);
    }
    else {
      grid.put(location, statusAfter);
    }
  }

  /**
   * Parses a lookup grid where initially infected grids are recorded.  The positioning
   * is done in such a way that the virus carrier (middle of the grid) is at the origin.
   */
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

  /**
   * The different statuses a node can have depending on the virus carrier's activity.
   *
   * Note: The {@link #ordinal()}s are important here as they are used to compute the next state.
   */
  private enum Status {
    Clean,
    Weakened,
    Infected,
    Flagged;
  }
}
