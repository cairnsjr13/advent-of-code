package com.cairns.rich.aoc._2015;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day07 extends Base2015 {
  private static final Map<String, IntBinaryOperator> binaryOps = Map.of(
      "AND", (l, r) -> l & r,
      "OR", (l, r) -> l | r,
      "LSHIFT", (l, r) -> l << r,
      "RSHIFT", (l, r) -> l >> r
  );
  
  @Override
  protected void run() {
    List<Instruction> instructions = fullLoader.ml(Instruction::new);
    
    Map<String, Integer> circuit = new HashMap<>();
    Map<String, Instruction> instructionsByOutput = instructions.stream().collect(Collectors.toMap(
        (instruction) -> instruction.output,
        Function.identity()
    ));
    int originalValue = 0xffff & compute(circuit, instructionsByOutput, "a");
    System.out.println("originalA = " + originalValue);
    
    circuit.clear();
    circuit.put("b", originalValue);
    int finalValue = 0xffff & compute(circuit, instructionsByOutput, "a");
    System.out.println("FinalA = " + finalValue);
  }
  
  private int compute(
      Map<String, Integer> circuit,
      Map<String, Instruction> instructionsByOutput,
      String output
  ) {
    if (Character.isDigit(output.charAt(0))) {
      return Integer.parseInt(output);
    }
    if (circuit.containsKey(output)) {
      return circuit.get(output);
    }
    Instruction instruction = instructionsByOutput.get(output);
    String[] ls = instruction.leftSide;
    int value;
    if (ls.length == 1) { // load
      value = compute(circuit, instructionsByOutput, ls[0]);
    }
    else if (ls.length == 2) { // NOT
      value = ~compute(circuit, instructionsByOutput, ls[1]);
    }
    else if (ls.length == 3) { // binary ops
      if (!binaryOps.containsKey(ls[1])) {
        throw fail(ls[1]);
      }
      value = binaryOps.get(ls[1]).applyAsInt(
          compute(circuit, instructionsByOutput, ls[0]),
          compute(circuit, instructionsByOutput, ls[2])
      );
    } else {
      throw fail(Arrays.toString(ls));
    }
    circuit.put(output, value);
    return value;
  }
  
  private static class Instruction {
    private static final Pattern pattern = Pattern.compile("^(.+) -> (.+)$");
    private final String[] leftSide;
    private final String output;
    
    private Instruction(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.leftSide = matcher.group(1).split(" ");
      this.output = matcher.group(2);
    }
  }
}
