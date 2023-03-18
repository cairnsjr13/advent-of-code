package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.function.Consumer;
import org.apache.commons.lang3.mutable.MutableInt;

class Day13 extends Base2016 {
  @Override
  protected Object part1(Loader2 loader) {
    int seed = Integer.parseInt(loader.sl());
    Spec spec = new Spec(seed, 31, 39);
    return bfs(
        new ImmutablePoint(1, 1),
        spec.target::equals,
        SearchState::getNumSteps,
        (candidate, registrar) -> explore(spec, candidate, registrar)
    ).get().getNumSteps();
  }

  @Override
  protected Object part2(Loader2 loader) {
    int seed = Integer.parseInt(loader.sl());
    Spec spec = new Spec(seed, -1, -1);
    MutableInt numLocations = new MutableInt(0);
    bfs(
        new ImmutablePoint(1, 1),
        (s) -> false,   // search everything
        SearchState::getNumSteps,
        (candidate, numSteps, registrar) -> {
          if (numSteps <= 50) {
            numLocations.increment();
            explore(spec, candidate, registrar);
          }
        }
    );
    return numLocations.getValue();
  }

  private void explore(Spec spec, ImmutablePoint candidate, Consumer<ImmutablePoint> registrar) {
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      ImmutablePoint next = candidate.move(dir);
      if ((0 <= next.x()) && (0 <= next.y()) && spec.isOpen(next)) {
        registrar.accept(next);
      }
    }
  }

  private static class Spec {
    private final int seed;
    private final ImmutablePoint target;

    private Spec(int seed, int targetX, int targetY) {
      this.seed = seed;
      this.target = new ImmutablePoint(targetX, targetY);
    }

    private boolean isOpen(ImmutablePoint location) {
      int x = location.x();
      int y = location.y();
      int numBits = Integer.bitCount((x * x) + (3 * x) + (2 * x * y) + (y) + (y * y) + seed);
      return numBits % 2 == 0;
    }
  }
}
