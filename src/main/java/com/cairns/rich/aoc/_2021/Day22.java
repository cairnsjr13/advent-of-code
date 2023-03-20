package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day22 extends Base2021 {
  private static final Range<Long> initRange = Range.closed(-50L, 50L);
  private static final Instruction initCuboid = new Instruction(false, initRange, initRange, initRange);

  @Override
  protected Object part1(Loader2 loader) {
    return countFinalOns(loader, initCuboid::isConnected);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return countFinalOns(loader, (i) -> true);
  }

  private long countFinalOns(Loader2 loader, Predicate<Instruction> filter) {
    List<Instruction> finalList = new ArrayList<>();
    for (Instruction inst : loader.ml(Instruction::new)) {
      if (filter.test(inst)) {
        List<Instruction> intersections =
            finalList.stream().filter(inst::isConnected).map(inst::intersection).collect(Collectors.toList());
        if (inst.on) {
          finalList.add(inst);
        }
        finalList.addAll(intersections);
      }
    }
    return finalList.stream().mapToLong(Instruction::computeOnValue).sum();
  }

  private static class Instruction {
    private static final Function<Character, String> rangePattern = (coord) -> coord + "=(-?\\d+)\\.\\.(-?\\d+)";
    private static final Pattern pattern = Pattern.compile(
        "^(on|off) " + rangePattern.apply('x') + "," + rangePattern.apply('y') + "," + rangePattern.apply('z') + "$"
    );

    private final boolean on;
    private final Range<Long> xRange;
    private final Range<Long> yRange;
    private final Range<Long> zRange;

    private Instruction(String input) {
      Matcher matcher = matcher(pattern, input);
      this.on = "on".equals(matcher.group(1));
      IntFunction<Range<Long>> toRange = (li) -> Range.closed((long) num(matcher, li), (long) num(matcher, li + 1));
      this.xRange = toRange.apply(2);
      this.yRange = toRange.apply(4);
      this.zRange = toRange.apply(6);
    }

    private Instruction(boolean on, Range<Long> xRange, Range<Long> yRange, Range<Long> zRange) {
      this.on = on;
      this.xRange = xRange;
      this.yRange = yRange;
      this.zRange = zRange;
    }

    private boolean isConnected(Instruction other) {
      return xRange.isConnected(other.xRange)
          && yRange.isConnected(other.yRange)
          && zRange.isConnected(other.zRange);
    }

    private Instruction intersection(Instruction other) {
      return new Instruction(
          !other.on,
          xRange.intersection(other.xRange),
          yRange.intersection(other.yRange),
          zRange.intersection(other.zRange)
      );
    }

    private long computeOnValue() {
      return ((on) ? 1 : -1)
           * (xRange.upperEndpoint() - xRange.lowerEndpoint() + 1)
           * (yRange.upperEndpoint() - yRange.lowerEndpoint() + 1)
           * (zRange.upperEndpoint() - zRange.lowerEndpoint() + 1);
    }
  }
}
