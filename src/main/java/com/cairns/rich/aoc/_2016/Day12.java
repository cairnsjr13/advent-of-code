package com.cairns.rich.aoc._2016;

import java.util.Map;

import com.cairns.rich.aoc._2016.AssemBunny.Inst;

class Day12 extends Base2016 {
  @Override
  protected void run() {
    System.out.println(AssemBunny.execute(fullLoader.ml(Inst::new), Map.of('c', 0)));
    System.out.println(AssemBunny.execute(fullLoader.ml(Inst::new), Map.of('c', 1)));
  }
}
