package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.function.Consumer;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * We find ourselves in an infinite cubicle grid.  Let's do some path searching through the office.
 */
class Day13 extends Base2016 {
  private static final ConfigToken<Integer> targetX = ConfigToken.of("targetX", Integer::parseInt);
  private static final ConfigToken<Integer> targetY = ConfigToken.of("targetY", Integer::parseInt);
  private static final ConfigToken<Integer> numMovesToken = ConfigToken.of("numMoves", Integer::parseInt);

  /**
   * Computes the minimum number of steps to traverse to the configured target location.
   */
  @Override
  protected Object part1(Loader loader) {
    Spec spec = new Spec(loader);
    ImmutablePoint target = new ImmutablePoint(loader.getConfig(targetX), loader.getConfig(targetY));
    return bfs(
        new ImmutablePoint(1, 1),
        target::equals,
        SearchState::getNumSteps,
        (candidate, registrar) -> explore(spec, candidate, registrar)
    ).get().getNumSteps();
  }

  /**
   * Computes the number of unique locations that can be reached with the configured number of steps.
   */
  @Override
  protected Object part2(Loader loader) {
    Spec spec = new Spec(loader);
    int numMoves = loader.getConfig(numMovesToken);
    MutableInt numLocations = new MutableInt(0);
    bfs(
        new ImmutablePoint(1, 1),
        (s) -> false,   // search everything
        SearchState::getNumSteps,
        (candidate, numSteps, registrar) -> {
          if (numSteps <= numMoves) {
            numLocations.increment();
            explore(spec, candidate, registrar);
          }
        }
    );
    return numLocations.getValue();
  }

  /**
   * Attempts to move in each of the {@link ReadDir}s to find new paths.  Will not move into negative or closed spaces.
   */
  private void explore(Spec spec, ImmutablePoint candidate, Consumer<ImmutablePoint> registrar) {
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      ImmutablePoint next = candidate.move(dir);
      if ((0 <= next.x()) && (0 <= next.y()) && spec.isOpen(next)) {
        registrar.accept(next);
      }
    }
  }

  /**
   * The cubicle grid is infinite but deterministic based on a seed.  This spec class will abstract that away.
   */
  private static class Spec {
    private final int seed;

    private Spec(Loader loader) {
      this.seed = Integer.parseInt(loader.sl());
    }

    /**
     * A cubicle location is determined to be open or a wall based on a deterministic math formula involving its coordinates.
     * This will return true if the given location should be considered as open and usable for path finding.
     */
    private boolean isOpen(ImmutablePoint location) {
      int x = location.x();
      int y = location.y();
      int numBits = Integer.bitCount((x * x) + (3 * x) + (2 * x * y) + (y) + (y * y) + seed);
      return numBits % 2 == 0;
    }
  }
}
