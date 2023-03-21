package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day08 extends Base2017 {
  private static final Map<String, TestOp> testOps = EnumUtils.getLookup(TestOp.class);

  @Override
  protected Object part1(Loader loader) {
    return runInstructions(loader, (state) -> state.registers.values().stream().mapToInt(Integer::intValue).max().getAsInt());
  }

  @Override
  protected Object part2(Loader loader) {
    return runInstructions(loader, (state) -> state.highWaterMark);
  }

  private int runInstructions(Loader loader, ToIntFunction<State> toAnswer) {
    State state = new State();
    for (Inst inst : loader.ml(Inst::new)) {
      String modifiedRegister = inst.executeAndGetModifiedRegister.apply(state);
      state.highWaterMark = Math.max(state.highWaterMark, state.value(modifiedRegister));
    }
    return toAnswer.applyAsInt(state);
  }

  private static class Inst {
    private static final Pattern pattern = Pattern.compile("^([^ ]+) (inc|dec) (-?\\d+) if ([^ ]+) ([^ ]+) (-?\\d+)$");

    private final Function<State, String> executeAndGetModifiedRegister;

    private Inst(String spec) {
      Matcher matcher = matcher(pattern, spec);
      String modifyRegister =  matcher.group(1);
      int delta = (("inc".equals(matcher.group(2))) ? 1 : -1) * num(matcher, 3);
      String testRegister = matcher.group(4);
      TestOp testOp = testOps.get(matcher.group(5));
      int testValue = num(matcher, 6);

      this.executeAndGetModifiedRegister = (state) -> {
        if (testOp.test.test(state.value(testRegister), testValue)) {
          state.modify(modifyRegister, delta);
        }
        return modifyRegister;
      };
    }
  }

  private static class State {
    private final Map<String, Integer> registers = new TreeMap<>();
    private int highWaterMark = Integer.MIN_VALUE;

    private int value(String register) {
      return registers.getOrDefault(register, 0);
    }

    private void modify(String register, int delta) {
      registers.put(register, delta + value(register));
    }
  }

  private enum TestOp implements HasId<String> {
    Less("<", (l, r) -> l < r),
    LessEq("<=", (l, r) -> l <= r),
    Eq("==", (l, r) -> l.equals(r)),
    Neq("!=", (l, r) -> !l.equals(r)),
    GreatEq(">=", (l, r) -> l >= r),
    Great(">", (l, r) -> l > r);

    private final String id;
    private final BiPredicate<Integer, Integer> test;

    private TestOp(String id, BiPredicate<Integer, Integer> test) {
      this.id = id;
      this.test = test;
    }

    @Override
    public String getId() {
      return id;
    }
  }
}
