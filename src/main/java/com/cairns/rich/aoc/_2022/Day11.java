package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;

class Day11 extends Base2022 {
  @Override
  protected Object part1(Loader loader) {
    return getMonkeyBusiness(loader, 20, 3);
  }

  @Override
  protected Object part2(Loader loader) {
    return getMonkeyBusiness(loader, 10_000, 1);
  }

  private long getMonkeyBusiness(Loader loader, int numRounds, int relief) {
    List<Monkey> monkeys = loader.gDelim("", Monkey::new);
    long reduceMod = monkeys.stream().mapToLong((m) -> m.testFactor).reduce(Math::multiplyExact).getAsLong();
    for (int round = 0; round < numRounds; ++round) {
      for (Monkey monkey : monkeys) {
        monkey.numInspects += monkey.held.size();
        while (!monkey.held.isEmpty()) {
          long itemWorry = monkey.inspectOp.applyAsLong(monkey.held.pollFirst()) / relief;
          monkeys.get(monkey.throwFn.applyAsInt(itemWorry)).held.offerLast(itemWorry % reduceMod);
        }
      }
    }
    Collections.sort(monkeys, Comparator.comparingLong((m) -> -m.numInspects));
    return monkeys.get(0).numInspects * monkeys.get(1).numInspects;
  }

  private static class Monkey {
    private final ArrayDeque<Long> held = new ArrayDeque<>();
    private final LongUnaryOperator inspectOp;
    private final long testFactor;
    private final LongToIntFunction throwFn;
    private long numInspects = 0;

    private Monkey(List<String> lines) {
      Arrays.stream(lines.get(1).substring("  Starting items: ".length()).split(", +"))
          .mapToLong(Long::parseLong)
          .forEach(held::add);
      String[] opParts = lines.get(2).split(" +");
      String opRight = opParts[opParts.length - 1];
      LongUnaryOperator toRight = (old) -> ("old".equals(opRight)) ? old : Long.parseLong(opRight);
      this.inspectOp = (opParts[opParts.length - 2].equals("+"))
          ? (old) -> old + toRight.applyAsLong(old)
          : (old) -> old * toRight.applyAsLong(old);
      this.testFactor = Long.parseLong(lines.get(3).substring("  Test: divisible by ".length()));
      int trueThrow = lines.get(4).charAt(lines.get(4).length() - 1) - '0';
      int falseThrow = lines.get(5).charAt(lines.get(5).length() - 1) - '0';
      this.throwFn = (val) -> (val % testFactor == 0) ? trueThrow : falseThrow;
    }
  }
}
