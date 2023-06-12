package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc._2017.KnotHash.State;
import java.util.List;

/**
 * We need to simulate a new hash algorithm.  Namely {@link KnotHash}.
 */
class Day10 extends Base2017 {
  private static final ConfigToken<Integer> size = ConfigToken.of("size", Integer::parseInt);

  /**
   * Simulates the {@link State#knot(List)} algorithm and returns the product of the resultant first two elements.
   * The size of the loop will be as is configured in the loader.
   */
  @Override
  protected Object part1(Loader loader) {
    List<Integer> lengths = loader.sl(",", Integer::parseInt);
    State state = new State(loader.getConfig(size));
    state.knot(lengths);
    return state.list[0] * state.list[1];
  }

  /**
   * Returns the {@link KnotHash#getKnotHash(int, List)} value for the given input.
   */
  @Override
  protected Object part2(Loader loader) {
    return KnotHash.getKnotHash(loader.getConfig(size), KnotHash.getLengthsFromString(loader.sl()));
  }
}
