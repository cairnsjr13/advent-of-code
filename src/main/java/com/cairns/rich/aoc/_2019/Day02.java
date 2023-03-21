package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2019.IntCode.State;
import java.util.List;

class Day02 extends Base2019 {
  @Override
  protected Object part1(Loader loader) {
    return getResultOf(IntCode.parseProgram(loader), 12, 2);
  }

  @Override
  protected Object part2(Loader loader) {
    List<Long> program = IntCode.parseProgram(loader);
    int target = 19690720;
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
