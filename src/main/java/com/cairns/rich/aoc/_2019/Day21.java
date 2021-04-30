package com.cairns.rich.aoc._2019;

import java.util.Arrays;
import java.util.List;

import com.cairns.rich.aoc._2019.IntCode.State;

/**
 * This day was awful.
 * Just brute forced part 1 (assumed T was unused, commands never back to back, or/not/and). Ran in ~14 seconds.
 */
class Day21 extends Base2019 {
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    System.out.println(walkDroid(program));
    System.out.println(runDroid(program));
  }
  
  private long walkDroid(List<Long> program) {
    return execDroid(
        program,
        "OR A J",
        "AND C J",
        "NOT J J",
        "AND D J",
        "WALK"
    );
  }
  
  private long runDroid(List<Long> program) {
    return execDroid(
        program,
        "NOT H J",
        "OR C J",
        "AND B J",
        "AND A J",
        "NOT J J",
        "AND D J",
        "RUN"
    );
  }
  
  private long execDroid(List<Long> program, String... cmds) {
    State state = IntCode.run(program);
    Arrays.stream(cmds).forEach((cmd) -> (cmd + "\n").chars().forEach(state.programInput::put));
    state.blockUntilHalt();
    
    long val = 0;
    while (state.programOutput.hasMoreToTake()) {
      val = state.programOutput.take();
    }
    return val;
  }
}
