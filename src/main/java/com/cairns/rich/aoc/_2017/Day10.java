package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2017.KnotHash.State;
import java.util.List;

class Day10 extends Base2017 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Integer> lengths = loader.sl(",", Integer::parseInt);
    State state = new State(256);
    state.knot(lengths);
    return state.list[0] * state.list[1];
  }

  @Override
  protected Object part2(Loader2 loader) {
    return KnotHash.getKnotHash(256, KnotHash.getLengthsFromString(loader.sl()));
  }
}
