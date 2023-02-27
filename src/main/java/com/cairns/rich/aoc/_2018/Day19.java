package com.cairns.rich.aoc._2018;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class Day19 extends Base2018 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    String ipLine = lines.get(0);
    int instructionRegister = ipLine.charAt(ipLine.length() - 1) - '0';
    List<Consumer<long[]>> instructions =
        lines.subList(1, lines.size()).stream().map(OpProgram::parse).collect(Collectors.toList());

    System.out.println(OpProgram.run(instructionRegister, instructions)[0]);
    System.out.println(sumOfFactors(919));
    System.out.println(sumOfFactors(10551319));
  }

  private int sumOfFactors(int num) {
    int upperBound = num;
    int sumOfFactors = 0;
    for (int left = 1; left < upperBound; ++left) {
      if (num % left == 0) {
        int right = num / left;
        sumOfFactors += left + right;
        upperBound = right;
      }
    }
    return sumOfFactors;
  }
}
