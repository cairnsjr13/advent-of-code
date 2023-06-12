package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.SHexDir;
import java.util.function.ToIntFunction;

/**
 * A child process got lost on an infinite grid.  However, this grid is hex shaped!
 * Luckily we can use {@link SHexDir} (since the grid is oriented so you can move along the y axis directly).
 */
class Day11 extends Base2017 {
  /**
   * Computes the minimum number of steps to reach the lost child given their input path.
   */
  @Override
  protected Object part1(Loader loader) {
    return doAllSteps(loader, State::stepsBackToMiddle);
  }

  /**
   * Computes the maximum distance the child got during their input path.
   */
  @Override
  protected Object part2(Loader loader) {
    return doAllSteps(loader, (state) -> state.maxDistance);
  }

  /**
   * Applies all of the child processes path directions and then computes an answer based on the given function.
   */
  private int doAllSteps(Loader loader, ToIntFunction<State> toAnswer) {
    State state = new State();
    loader.sl(",", EnumUtils.getLookup(SHexDir.class)::get).forEach(state::move);
    return toAnswer.applyAsInt(state);
  }

  /**
   * State object tracking where we currently are and the maximum distance we ever reached.
   */
  private static class State {
    private final MutablePoint location = MutablePoint.origin();
    private int maxDistance = 0;

    /**
     * Moves the current location in the given hex dir.  Also updates the maxDistance.
     */
    private void move(SHexDir dir) {
      location.move(dir);
      maxDistance = Math.max(maxDistance, stepsBackToMiddle());
    }

    /**
     * Computes the minimum number of steps required to get back to the origin.
     * This is found by adding the number of steps we can take in both directions
     * and the number of steps that must be taken along one direction.
     */
    private int stepsBackToMiddle() {
      int x = Math.abs(location.x());
      int y = Math.abs(location.y());
      return Math.min(x, y) // steps together
           + ((x > y) ? x - y : (y - x) / 2);
    }
  }
}
