package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.SHexDir;
import java.util.function.ToIntFunction;

class Day11 extends Base2017 {
  @Override
  protected Object part1(Loader2 loader) {
    return doAllSteps(loader, State::stepsBackToMiddle);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return doAllSteps(loader, (state) -> state.maxDistance);
  }

  private int doAllSteps(Loader2 loader, ToIntFunction<State> toAnswer) {
    State state = new State();
    loader.sl(",", EnumUtils.getLookup(SHexDir.class)::get).forEach(state::move);
    return toAnswer.applyAsInt(state);
  }

  private static class State {
    private final MutablePoint location = MutablePoint.origin();
    private int maxDistance = 0;

    private void move(SHexDir dir) {
      location.move(dir);
      maxDistance = Math.max(maxDistance, stepsBackToMiddle());
    }

    private int stepsBackToMiddle() {
      int x = Math.abs(location.x());
      int y = Math.abs(location.y());
      return Math.min(x, y) // steps together
           + ((x > y) ? x - y : (y - x) / 2);
    }
  }
}
