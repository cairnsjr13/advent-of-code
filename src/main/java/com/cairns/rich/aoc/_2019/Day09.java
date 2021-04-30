package com.cairns.rich.aoc._2019;

import java.util.List;

import com.cairns.rich.aoc._2019.IntCode.State;

class Day09 extends Base2019 {
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    System.out.println(getResultFromInput(program, 1));
    System.out.println(getResultFromInput(program, 2));
  }
  
  private long getResultFromInput(List<Long> program, long input) {
    State state = IntCode.run(program);
    state.programInput.put(input);
    state.blockUntilHaltOrWaitForInput();
    return state.programOutput.take();
  }
}
