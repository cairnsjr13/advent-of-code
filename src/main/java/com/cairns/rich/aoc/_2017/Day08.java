package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The cpu needs us to simulate some instructions and registers again.  This time it involves comparisons and an arbitrary
 * number of registers.  We need to keep track of the largest register at various points in the simulation.
 */
class Day08 extends Base2017 {
  private static final Pattern pattern = Pattern.compile("^([^ ]+) (inc|dec) (-?\\d+) if ([^ ]+) ([^ ]+) (-?\\d+)$");
  private static final Map<String, TestOp> testOps = EnumUtils.getLookup(TestOp.class);

  /**
   * Returns the highest value of any register after the simulation completes.
   */
  @Override
  protected Object part1(Loader loader) {
    return runInstructions(loader, (state) -> state.registers.values().stream().mapToInt(Integer::intValue).max().getAsInt());
  }

  /**
   * Returns the highest value of any register at any point in the simulation.
   */
  @Override
  protected Object part2(Loader loader) {
    return runInstructions(loader, (state) -> state.highWaterMark);
  }

  /**
   * Applies all of the input instructions and computes an answer based on the given function.
   */
  private int runInstructions(Loader loader, ToIntFunction<State> toAnswer) {
    State state = new State();
    loader.ml(this::parse).stream().forEach((inst) -> inst.accept(state));
    return toAnswer.applyAsInt(state);
  }

  /**
   * Parses an instruction from the given spec string.
   * An instruction increments or decrements a certain register if a {@link TestOp} conditional is met.
   */
  private Consumer<State> parse(String spec) {
    Matcher matcher = matcher(pattern, spec);
    String modifyRegister =  matcher.group(1);
    int delta = (("inc".equals(matcher.group(2))) ? 1 : -1) * num(matcher, 3);
    String testRegister = matcher.group(4);
    TestOp testOp = testOps.get(matcher.group(5));
    int testValue = num(matcher, 6);
    return (state) -> {
      if (testOp.test.test(state.value(testRegister), testValue)) {
        state.modify(modifyRegister, delta);
      }
    };
  }

  /**
   * State object tracking the current value of all registers as well as the highest value any of them have reached.
   */
  private static class State {
    private final Map<String, Integer> registers = new TreeMap<>();
    private int highWaterMark = Integer.MIN_VALUE;

    /**
     * Fetches the current value of the given register.  Will default to 0 if it has never been used.
     */
    private int value(String register) {
      return registers.getOrDefault(register, 0);
    }

    /**
     * Updates the given register by the given amount.  Defaults the existing value to 0 if it has never been used.
     */
    private void modify(String register, int delta) {
      int newValue = value(register) + delta;
      registers.put(register, newValue);
      highWaterMark = Math.max(highWaterMark, newValue);
    }
  }

  /**
   * An enumeration of all of the test operations that the cpu needs simulating.
   */
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
