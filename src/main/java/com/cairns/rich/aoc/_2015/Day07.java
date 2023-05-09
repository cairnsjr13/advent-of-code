package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Little bobby tables needs help assembling a circuit.  Bitwise logic and dependency traversal.
 */
class Day07 extends Base2015 {
  private static final ConfigToken<String> returnWire = ConfigToken.of("returnWire", Function.identity());
  private static final Map<String, IntBinaryOperator> binaryOps = Map.of(
      "AND", (l, r) -> l & r,
      "OR", (l, r) -> l | r,
      "LSHIFT", (l, r) -> l << r,
      "RSHIFT", (l, r) -> l >> r
  );

  /**
   * Emulates the input circuit and returns the configured wire's value.
   */
  @Override
  protected Object part1(Loader loader) {
    String wireToReturn = loader.getConfig(returnWire);
    Map<String, Instruction> instructionsByOutput = getLookup(loader.ml(Instruction::new));
    Map<String, Integer> circuit = new HashMap<>();
    return 0xffff & compute(circuit, instructionsByOutput, wireToReturn);
  }

  /**
   * Emulates the input circuit to retrieve the configured wire's value.  Then overrides wire
   * b with that value, resets the circuit values and recomputes the configured wire's value.
   */
  @Override
  protected Object part2(Loader loader) {
    String wireToReturn = loader.getConfig(returnWire);
    Map<String, Instruction> instructionsByOutput = getLookup(loader.ml(Instruction::new));
    Map<String, Integer> circuit = new HashMap<>();
    int originalValue = 0xffff & compute(circuit, instructionsByOutput, wireToReturn);
    circuit.clear();
    circuit.put("b", originalValue);
    return 0xffff & compute(circuit, instructionsByOutput, wireToReturn);
  }

  /**
   * Recursively computes (and caches) the value for the given output wire.
   * An instruction can be one of:
   *   1) load (1arg) - a single argument (wire or constant) is sent to the output wire
   *   2) NOT (2arg) - the keyword followed by a single argument to bitwise invert is sent to the output wire
   *   3) binary ops (3arg) - two arguments joined by a logic gate and then sent to the output
   *      a) AND - binary and of the two inputs
   *      b) OR - binary or of the two inputs
   *      c) LSHIFT - binary left shift of the first argument a number of times indicated by the second argument
   *      d) RSHIFT - binary right shift of the first argument a number of times indicated by the second argument
   */
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
    }
    else {
      throw fail(Arrays.toString(ls));
    }
    circuit.put(output, value);
    return value;
  }

  /**
   * Input class describing a logic gate and its inputs/output.
   * Includes an id based on its output to make lookups fast.
   */
  private static class Instruction implements HasId<String> {
    private static final Pattern pattern = Pattern.compile("^(.+) -> (.+)$");
    private final String[] leftSide;
    private final String output;

    private Instruction(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.leftSide = matcher.group(1).split(" ");
      this.output = matcher.group(2);
    }

    @Override
    public String getId() {
      return output;
    }
  }
}
