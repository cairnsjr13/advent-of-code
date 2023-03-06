package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc._2019.IntCode.State;
import java.util.List;

class Day02 extends Base2019 {
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    System.out.println(getResultOf(program, 12, 2));
    System.out.println(getNounAndVerbAnsFor(program, 19690720));
  }

  private int getNounAndVerbAnsFor(List<Long> program, long target) {
    for (int noun = 0; noun < 100; ++noun) {
      for (int verb = 0; verb < 100; ++verb) {
        if (target == getResultOf(program, noun, verb)) {
          return (noun * 100) + verb;
        }
      }
    }
    throw fail();
  }

  private long getResultOf(List<Long> program, long noun, long verb) {
    program.set(1, noun);
    program.set(2, verb);
    State state = IntCode.run(program);
    state.blockUntilHaltOrWaitForInput();
    return state.getMem(0);
  }
}
