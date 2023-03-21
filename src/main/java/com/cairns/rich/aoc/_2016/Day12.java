package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2016.AssemBunny.Inst;
import java.util.Map;

class Day12 extends Base2016 {
  @Override
  protected Object part1(Loader loader) {
    return AssemBunny.execute(loader.ml(Inst::new), Map.of('c', 0));
  }

  @Override
  protected Object part2(Loader loader) {
    return AssemBunny.execute(loader.ml(Inst::new), Map.of('c', 1));
  }
}
