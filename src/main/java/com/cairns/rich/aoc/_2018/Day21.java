package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

class Day21 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    return findReg0Value(loader, (state) -> state.firstSeen);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return findReg0Value(loader, (state) -> state.lastSeen);
  }

  private long findReg0Value(Loader2 loader, ToLongFunction<State> toAnswer) {
    State state = new State(loader);
    Set<Long> seen = new HashSet<>();
    OpProgram.run(state.instructionRegister, state.instructions, (instructionPtr, registers) -> {
      if (instructionPtr == 28) {
        if (state.lastSeen == -1) {
          state.firstSeen = registers[4];
        }
        if (!seen.add(registers[4])) {
          return true;
        }
        state.lastSeen = registers[4];
      }
      return false;
    });
    return toAnswer.applyAsLong(state);
  }

  private static class State {
    private final int instructionRegister;
    private final List<Consumer<long[]>> instructions;
    private long firstSeen = -1L;
    private long lastSeen = -1L;

    private State(Loader2 loader) {
      List<String> lines = loader.ml();
      this.instructionRegister = safeCharAt(lines.get(0), -1) - '0';
      this.instructions = lines.subList(1, lines.size()).stream().map(OpProgram::parse).collect(Collectors.toList());
    }
  }
}
