package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * A logic gate system needs to be simulated.  In the end, the system represents a multi-bit full adder.
 */
class Day24 extends Base2024 {
  /**
   * Comma separated list of flip groups where a flip group is a space separated triple of (flipOutA,flipOutB,bit).
   */
  private static final ConfigToken<String> flipsToken = ConfigToken.of("flips", Function.identity());

  /**
   * Simulating the input gate structure will result in a binary number being present on the z wires.
   */
  @Override
  protected Object part1(Loader loader) {
    List<String> lines = loader.ml();
    int blankIndex = lines.indexOf("");
    List<Gate> gates = lines.subList(blankIndex + 1, lines.size()).stream().map(Gate::new).collect(Collectors.toList());
    Multimap<String, Gate> wireToInput = HashMultimap.create();
    for (Gate gate : gates) {
      wireToInput.put(gate.left, gate);
      wireToInput.put(gate.right, gate);
    }

    Map<String, Boolean> wireValues = new HashMap<>();
    for (String wireSpec : lines.subList(0, blankIndex)) {
      String[] pieces = wireSpec.split(": ");
      String wire = pieces[0];
      boolean value = "1".equals(pieces[1]);
      handleWire(wireToInput, wireValues, wire, value);
    }
    return readZ(wireValues);
  }

  /**
   * Potentially recursive handle method to handle the given output wire receiving the given value.
   * Gates that have both input wires satisfied will in turn have their output values sent.
   */
  private void handleWire(
      Multimap<String, Gate> wireToInput,
      Map<String, Boolean> wireValues,
      String wire,
      boolean value
  ) {
    wireValues.put(wire, value);
    if (wireToInput.containsKey(wire)) {
      for (Gate inputGate : wireToInput.get(wire)) {
        Boolean leftVal = wireValues.get(inputGate.left);
        Boolean rightVal = wireValues.get(inputGate.right);
        if ((leftVal != null) && (rightVal != null)) {
          handleWire(wireToInput, wireValues, inputGate.out, inputGate.op.logic.test(leftVal, rightVal));
        }
      }
    }
  }

  /**
   * Reads the binary value represented by the z wires.  This is done by treating the
   * outputs on z wires a bits where z00 is the lowest significant bit.  This will loop
   * until a corresponding z wire is not found for the next bit as long as they are contiguous.
   */
  private long readZ(Map<String, Boolean> wireValues) {
    long z = 0;
    for (int i = 0; true; ++i) {
      String wire = wire('z', i);
      if (!wireValues.containsKey(wire)) {
        break;
      }
      if (wireValues.get(wire)) {
        z += (1L << i);
      }
    }
    return z;
  }

  /**
   * Turns out we are simulating a 45-bit full adder.  There are two binary numbers input into the x any y wires.
   * The full sum (with carry) will be present on the z wire.  However, a number of the gates have their output wires
   * miswired (flipped with each other).  We need to find these gates and return their output wires.  We can easily
   * verify there is indeed an issue with the adder since input x and y do not lead to x + y appearing on the z wire.
   *
   * Here is how a full adder works: https://www.build-electronic-circuits.com/full-adder/
   *
   * The important thing to note here is that each bit operates nearly independently aside from its two
   * input bits and the carry bit.  Each bit will have identical structure (aside from the first and last
   * bits).  Because of this we can detect any anomalies based on which gates are wired to each other.
   *
   * This will use {@link #getBitStructureIssue(Multimap, int)} to report suspected issues (and vaguely what the issue
   * might be) on each bit.  If there are no issues detected, prints the sorted output wires joined by commas.
   */
  @Override
  protected Object part2(Loader loader) {
    boolean shouldPrintDotGraph = false;
    Map<String, String> flips = new HashMap<>();
    String flipSpec = loader.getConfig(flipsToken);
    if (!flipSpec.isBlank()) {
      Arrays.stream(flipSpec.split(",")).map((flipGroup) -> flipGroup.split(" ")).forEach((flip) -> {
        flips.put(flip[0], flip[1]);
        flips.put(flip[1], flip[0]);
      });
    }

    List<String> lines = loader.ml();
    int blankIndex = lines.indexOf("");
    List<Gate> gates = lines.subList(blankIndex + 1, lines.size()).stream().map(Gate::new).collect(Collectors.toList());
    Multimap<String, Gate> wireToInput = HashMultimap.create();
    for (Gate gate : gates) {
      if (flips.containsKey(gate.out)) {
        gate.out = flips.get(gate.out);
      }
      wireToInput.put(gate.left, gate);
      wireToInput.put(gate.right, gate);
    }

    if (shouldPrintDotGraph) {
      printDotGraph(gates);
    }

    List<String> issues = new ArrayList<>();
    for (int bit = 1; bit < 45; ++bit) {
      String bitStructureIssue = getBitStructureIssue(wireToInput, bit);
      if (bitStructureIssue != null) {
        issues.add("Investigate bit" + bit + ": " + bitStructureIssue);
      }
    }
    return (issues.isEmpty())
         ? flips.keySet().stream().sorted().collect(Collectors.joining(","))
         : issues.stream().collect(Collectors.joining("\n"));
  }

  /**
   * Prints out a viz dot diagram that will show the gate relationships.
   * Paste the graph spec here: https://www.devtoolsdaily.com/graphviz/
   * Language details: https://graphviz.org/doc/info/lang.html
   */
  private void printDotGraph(List<Gate> gates) {
    System.out.println("digraph G {");
    for (Gate gate : gates) {
      String gateId = "G" + gate.id;
      System.out.println("  " + gateId + " [shape=" + gate.op.displayShape + "]");
      System.out.println("  " + gate.left + " -> " + gateId);
      System.out.println("  " + gate.right + " -> " + gateId);
      System.out.println("  " + gateId + " -> " + gate.out);
    }
    System.out.println("}");
    System.out.println();
  }

  /**
   * We take a couple of stabs in the dark and make assumptions about what the potential breaks are.
   *
   * The first and probably most important assumption is that gates who have their outputs flipped
   * will be constrained to the same bit (no cross bit flips).  If this were not true, the search
   * space would be gigantic and unlikely to be discoverable in automated fashion.
   *
   * Here are the structural requirements we check for:
   *
   *   - input wires for a bit have the exact same gates.
   *   - the gates for the input wires are exactly 1 XOR gate and 1 AND gate.
   *   - the input AND gate is outputed to exactly 1 OR gate.
   *   - the input XOR gate is output to exactly 1 XOR gate and 1 AND gate.
   *   - the output of the second XOR gate is the z output wire.
   *   - the OR gate in the middle is properly wired from both sides.
   */
  private String getBitStructureIssue(Multimap<String, Gate> wireToInput, int bit) {
    Collection<Gate> xWireInputsFor = wireToInput.get(wire('x', bit));
    Collection<Gate> yWireInputsFor = wireToInput.get(wire('y', bit));
    if (!xWireInputsFor.equals(yWireInputsFor)) {
      return "Different input destinations";
    }
    Map<Op, Gate> opToInputGate =
        xWireInputsFor.stream().collect(Collectors.toMap((g) -> g.op, Function.identity()));
    if (!opToInputGate.containsKey(Op.XOR) || !opToInputGate.containsKey(Op.AND)) {
      return "Missing input gate";
    }

    Gate inputAndGate = opToInputGate.get(Op.AND);
    Collection<Gate> orGates = wireToInput.get(inputAndGate.out);
    if (orGates.size() != 1) {
      return "Wrong number of OR gates";
    }
    Gate orGateFromAndPath = orGates.iterator().next();

    Gate inputXorGate = opToInputGate.get(Op.XOR);
    Map<Op, Gate> opToInputXorOutputGate =
        wireToInput.get(inputXorGate.out).stream().collect(Collectors.toMap((g) -> g.op, Function.identity()));
    if (!opToInputXorOutputGate.containsKey(Op.XOR) || !opToInputXorOutputGate.containsKey(Op.AND)) {
      return "Missing inputXor output gate";
    }
    Gate outputXorGate = opToInputXorOutputGate.get(Op.XOR);
    if (!outputXorGate.out.equals(wire('z', bit))) {
      return "Wrong output wire";
    }

    Gate andFromInputXor = opToInputXorOutputGate.get(Op.AND);
    orGates = wireToInput.get(andFromInputXor.out);
    if (orGates.size() != 1) {
      return "Wrong number of OR gates";
    }
    Gate orGateFromXorPath = orGates.iterator().next();
    if (orGateFromAndPath != orGateFromXorPath) {
      return "OR sinks different";
    }

    return null;
  }

  /**
   * Helper function to convert a bit number to a wire label by left padding 0s.
   */
  private String wire(char prefix, int bit) {
    return prefix + StringUtils.leftPad(Integer.toString(bit), 2, '0');
  }

  /**
   * Descriptor class of a gate's input wires and output wire along with its {@link Op} type.
   */
  private static final class Gate {
    private static final Pattern pattern = Pattern.compile("^([^ ]+) (XOR|AND|OR) ([^ ]+) -> ([^ ]+)$");
    private static int idCounter = 0;

    private final int id = idCounter++;
    private final Op op;
    private final String left;
    private final String right;
    private String out;

    private Gate(String line) {
      Matcher matcher = matcher(pattern, line);
      this.op = Op.valueOf(matcher.group(2));
      this.left = matcher.group(1);
      this.right = matcher.group(3);
      this.out = matcher.group(4);
    }
  }

  /**
   * The different gate types that exist in our wired system.
   */
  private enum Op {
    XOR("Mdiamond", (l, r) -> l ^ r),
    AND("triangle", (l, r) -> l && r),
    OR("tripleoctagon", (l, r) -> l || r);

    private final String displayShape;
    private final BiPredicate<Boolean, Boolean> logic;

    private Op(String displayShape, BiPredicate<Boolean, Boolean> logic) {
      this.displayShape = displayShape;
      this.logic = logic;
    }
  }
}
