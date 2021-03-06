package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.cairns.rich.aoc._2017.TabletCode.State;

class Day23 extends Base2017 {
  @Override
  protected void run() {
    List<ToIntFunction<State>> insts = fullLoader.ml(TabletCode::parse);
    State state = new State(false);
    TabletCode.executeState(insts, state);
    System.out.println(state.instCalls.count("mul"));
    System.out.println(numCompositesBetweenInclusive(109300, 126300, 17));
    System.out.println(numCompositesWithTabletCode(109300, 126300, 17));
  }
  
  private int numCompositesBetweenInclusive(int min, int max, int step) {
    int numComposites = 0;
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
