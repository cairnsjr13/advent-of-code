package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2017.KnotHash.State;
import java.util.List;

class Day10 extends Base2017 {
  @Override
  protected void run() {
    Loader2 loader = fullLoader;
    System.out.println(getProductOfFirstTwoInKnotHash(256, loadPart1(loader)));
    System.out.println(KnotHash.getKnotHash(256, loadPart2(loader)));
  }

  private int getProductOfFirstTwoInKnotHash(int size, List<Integer> lengths) {
    State state = new State(size);
    state.knot(lengths);
    return state.list[0] * state.list[1];
  }

  private List<Integer> loadPart1(Loader2 loader) {
    return loader.sl(",", Integer::parseInt);
  }

  private List<Integer> loadPart2(Loader2 loader) {
    return KnotHash.getLengthsFromString(loader.sl());
  }
}
