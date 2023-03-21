package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2016.AssemBunny.Inst;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class Day23 extends Base2016 {
  @Override
  protected Object part1(Loader loader) {
    return execute(loader::ml, 7);
  }

  @Override
  protected Object part2(Loader loader) {
    return execute(loader::ml, 12);
  }

  private int execute(Function<Function<String, Inst>, List<Inst>> load, int initA) {
    return AssemBunny.execute(load.apply(Inst::new), Map.of('a', initA));
  }
}
