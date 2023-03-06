package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc._2019.IntCode.IO;
import com.cairns.rich.aoc._2019.IntCode.State;
import java.util.List;

class Day05 extends Base2019 {
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    System.out.println(runWithInput(program, 1));
    System.out.println(runWithInput(program, 5));
  }

  private IO runWithInput(List<Long> program, long input) {
    State state = IntCode.run(program);
    state.programInput.put(input);
    state.blockUntilHalt();
    return state.programOutput;
  }
}
