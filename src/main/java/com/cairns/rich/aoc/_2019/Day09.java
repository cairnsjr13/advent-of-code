package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2019.IntCode.State;

class Day09 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    return getResultFromInput(loader, 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getResultFromInput(loader, 2);
  }

  private long getResultFromInput(Loader2 loader, long input) {
    State state = IntCode.run(IntCode.parseProgram(loader));
    state.programInput.put(input);
    state.blockUntilHaltOrWaitForInput();
    return state.programOutput.take();
  }
}
