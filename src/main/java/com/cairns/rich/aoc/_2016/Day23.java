package com.cairns.rich.aoc._2016;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.cairns.rich.aoc._2016.AssemBunny.Inst;

class Day23 extends Base2016 {
  @Override
  protected void run() {
    System.out.println(execute(testLoader::ml, 0));
    System.out.println(execute(fullLoader::ml, 7));
    System.out.println(execute(fullLoader::ml, 12));
  }
  
  private int execute(Function<Function<String, Inst>, List<Inst>> load, int initA) {
    return AssemBunny.execute(load.apply(Inst::new), Map.of('a', initA));
  }
}
