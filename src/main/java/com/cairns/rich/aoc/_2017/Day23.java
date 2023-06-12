package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2017.TabletCode.State;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * We need to do another {@link TabletCode} simulation.  Only this one is quite a bit more inefficient than the last.
 * We are counting the number of composites and we will need an optimized version of the code for part two.
 */
class Day23 extends Base2017 {
  /**
   * Returns the number of mul instructions run in the input program.
   */
  @Override
  protected Object part1(Loader loader) {
    List<ToIntFunction<State>> insts = loader.ml(TabletCode::parse);
    State state = new State(false);
    TabletCode.executeState(insts, state);
    return state.instCalls.count("mul");
  }

  /**
   * Returns the number of composite integers found between min/max using a given step.
   * The program as given in the input is extremely slow and needs to be optimized.  We
   * run an optimized version of the code and verify that it matches java implementation.
   */
  @Override
  protected Object part2(Loader loader) {  // TODO: parse out min/max/step from input
    long fromJava = numCompositesBetweenInclusive(109300, 126300, 17);
    long fromTabletCode = numCompositesWithTabletCode(109300, 126300, 17);
    if (fromJava != fromTabletCode) {
      fail(fromJava + " - " + fromTabletCode);
    }
    return fromJava;
  }

  /**
   * Computes the number of composite integers between and including the given min/max stepping the given amount each time.
   */
  private long numCompositesBetweenInclusive(int min, int max, int step) {
    long numComposites = 0;
    for (int target = min; target <= max; target += step) {
      for (int factor = 2; factor < target; ++factor) {
        if (target % factor == 0) {
          ++numComposites;
          break;
        }
      }
    }
    return numComposites;
  }

  /**
   * Runs an optimized version of the input program to calculate the number of composite integers.
   */
  private long numCompositesWithTabletCode(int min, int max, int step) {
    List<ToIntFunction<State>> insts = Arrays.asList(
        "set a " + min,
        "sub a " + step,
        "add a " + step,
        "set b 1",
        "add b 1",
        "set c a",
        "mod c b",
        "jnz c 3",
        "add d 1",
        "jnz 1 4",
        "set c b",
        "sub c " + ((int) Math.ceil(Math.sqrt(max))),
        "jnz c -8",
        "set c a",
        "sub c " + max,
        "jnz c -13"
    ).stream().map(TabletCode::parse).collect(Collectors.toList());
    State state = new State(false);
    TabletCode.executeState(insts, state);
    return state.registerValue('d');
  }
}
