package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc._2018.OpProgram.Op;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Day16 extends Base2018 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    List<Spec> specs = new ArrayList<>();
    for (int startIndex = 0; !lines.get(startIndex).isEmpty(); startIndex += 4) {
      specs.add(new Spec(lines, startIndex));
    }
    List<int[]> program = lines.subList(specs.size() * 4 + 2, lines.size()).stream()
        .map((line) -> fromStrWithSep(line, " "))
        .collect(Collectors.toList());

    Map<Integer, String> opCodeToName = findOpCodes(specs);
    long[] registers = new long[4];
    for (int[] inst : program) {
      OpProgram.ops.get(opCodeToName.get(inst[0])).action.accept(registers, inst);
    }
    System.out.println(registers[0]);
  }

  private Map<Integer, String> findOpCodes(List<Spec> specs) {
    Multimap<String, Integer> opCodeOptions = getOpCodeOptions(specs);
    Map<Integer, String> opCodeToName = new HashMap<>();
    while (opCodeToName.size() != OpProgram.ops.size()) {
      for (String opName : OpProgram.ops.keySet()) {
        Collection<Integer> options = opCodeOptions.get(opName);
        if (options.size() == 1) {
          int opCode = options.iterator().next();
          opCodeToName.put(opCode, opName);
          OpProgram.ops.keySet().forEach((opn) -> opCodeOptions.remove(opn, opCode));
        }
      }
    }
    return opCodeToName;
  }

  private Multimap<String, Integer> getOpCodeOptions(List<Spec> specs) {
    int numSpecsWithAtLeast3Matches = 0;
    Multimap<String, Integer> opCodeOptions = HashMultimap.create();
    for (Spec spec : specs) {
      int numMatches = 0;
      for (Op op : OpProgram.ops.values()) {
        long[] regs = Arrays.copyOf(spec.before, spec.before.length);
        op.action.accept(regs, spec.instruction);
        if (Arrays.equals(regs, spec.after)) {
          ++numMatches;
          opCodeOptions.put(op.name, spec.instruction[0]);
        }
      }
      if (numMatches >= 3) {
        ++numSpecsWithAtLeast3Matches;
      }
    }
    System.out.println(numSpecsWithAtLeast3Matches);
    return opCodeOptions;
  }

  private static int[] fromStrWithSep(String str, String sep) {
    return Arrays.stream(str.split(sep)).mapToInt(Integer::parseInt).toArray();
  }

  private static class Spec {
    private final long[] before;
    private final int[] instruction;
    private final long[] after;

    private Spec(List<String> lines, int startingAt) {
      this.before = beforeAfter(lines.get(startingAt));
      this.instruction = fromStrWithSep(lines.get(startingAt + 1), " ");
      this.after = beforeAfter(lines.get(startingAt + 2));
    }

    private long[] beforeAfter(String line) {
      return Arrays
          .stream(fromStrWithSep(line.substring(line.indexOf('[') + 1, line.length() - 1), ", "))
          .mapToLong((i) -> (long) i).toArray();
    }
  }
}
