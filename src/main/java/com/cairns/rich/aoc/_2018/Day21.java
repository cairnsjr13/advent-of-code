package com.cairns.rich.aoc._2018;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.mutable.MutableLong;

class Day21 extends Base2018 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    String ipLine = lines.get(0);
    int instructionRegister = ipLine.charAt(ipLine.length() - 1) - '0';
    List<Consumer<long[]>> instructions =
        lines.subList(1, lines.size()).stream().map(OpProgram::parse).collect(Collectors.toList());

    MutableLong lastSeen = new MutableLong(-1);
    Set<Long> seen = new HashSet<>();
    OpProgram.run(instructionRegister, instructions, (instructionPtr, registers) -> {
      if (instructionPtr == 28) {
        if (lastSeen.longValue() == -1) {
          System.out.println(registers[4]);
        }
        if (!seen.add(registers[4])) {
          return true;
        }
        lastSeen.setValue(registers[4]);
      }
      return false;
    });
    System.out.println(lastSeen.longValue());
  }
}
