package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2019.IntCode.State;
import java.util.List;

class Day05 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) throws InterruptedException {
    return runWithInput(loader, 1);
  }

  @Override
  protected Object part2(Loader2 loader) throws InterruptedException {
    return runWithInput(loader, 5);
  }

  private long runWithInput(Loader2 loader, long input) throws InterruptedException {
    List<Long> program = IntCode.parseProgram(loader);
    State state = IntCode.run(program);
    state.programInput.put(input);
    state.blockUntilHalt();
    while (state.programOutput.queue.size() > 1) {
      state.programOutput.queue.take();
    }
    return state.programOutput.take();
  }
}
