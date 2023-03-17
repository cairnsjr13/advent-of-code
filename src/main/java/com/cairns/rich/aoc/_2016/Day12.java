package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2016.AssemBunny.Inst;
import java.util.Map;

class Day12 extends Base2016 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    result.part1(AssemBunny.execute(loader.ml(Inst::new), Map.of('c', 0)));
    result.part2(AssemBunny.execute(loader.ml(Inst::new), Map.of('c', 1)));
  }
}
