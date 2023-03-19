package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2019.IntCode.State;
import java.util.Arrays;
import java.util.List;

/**
 * This day was awful.
 * Just brute forced part 1 (assumed T was unused, commands never back to back, or/not/and). Ran in ~14 seconds.
 * There is probably a way to discover the map and figure out the program in java instead of {@link IntCode}.
 */
class Day21 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    return execDroid(
        IntCode.parseProgram(loader),
        "OR A J",
        "AND C J",
        "NOT J J",
        "AND D J",
        "WALK"
    );
  }

  @Override
  protected Object part2(Loader2 loader) {
    return execDroid(
        IntCode.parseProgram(loader),
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
    while (state.programOutput.queue.size() > 1) {
      state.programOutput.take();
    }
    return state.programOutput.take();
  }
}
