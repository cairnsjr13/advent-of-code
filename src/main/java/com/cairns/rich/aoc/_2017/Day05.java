package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.function.IntUnaryOperator;

class Day05 extends Base2017 {
  @Override
  protected void run() {
    System.out.println(getStepsToEscape(fullLoader, (i) -> 1));
    System.out.println(getStepsToEscape(fullLoader, (i) -> (i >= 3) ? -1 : 1));
  }

  private int getStepsToEscape(Loader2 loader, IntUnaryOperator modify) {
    List<Integer> jmps = loader.ml(Integer::parseInt);
    int steps = 0;
    for (int i = 0; (0 <= i) && (i < jmps.size()); ++steps) {
      int jmp = jmps.get(i);
      jmps.set(i, jmp + modify.applyAsInt(jmp));
      i += jmp;
    }
    return steps;
  }
}
