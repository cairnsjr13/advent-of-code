package com.cairns.rich.aoc._2018;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;

class Day04 extends Base2018 {
  @Override
  protected void run() {
    List<Action> actions = fullLoader.ml(Action::new); // already sorted
    System.out.println(getGuardTimesMinuteStrategy1(actions));
    System.out.println(getGuardTimesMinuteStrategy2(actions));
  }

  private int getGuardTimesMinuteStrategy1(List<Action> actions) {
    Map<Integer, Multiset<Integer>> guardToMinutesAsleep = buildGuardToMinutesAsleep(actions);
    int maxGuard = getMax(guardToMinutesAsleep.keySet(), (guard) -> guardToMinutesAsleep.get(guard).size());
    Multiset<Integer> minutesAsleep = guardToMinutesAsleep.get(maxGuard);
    int maxMinute = getMax(minutesAsleep, minutesAsleep::count);
    return maxGuard * maxMinute;
  }

  private int getGuardTimesMinuteStrategy2(List<Action> actions) {
    Map<Integer, Multiset<Integer>> guardToMinutesAsleep = buildGuardToMinutesAsleep(actions);
    Stream<Pair<Integer, Integer>> guardAndMinutes = guardToMinutesAsleep.keySet().stream()
        .map((guard) -> guardToMinutesAsleep.get(guard).stream().map((minute) -> Pair.of(guard, minute)))
        .flatMap(Function.identity());
    Pair<Integer, Integer> guardAndMinute =
        getMax(guardAndMinutes::iterator, (gam) -> guardToMinutesAsleep.get(gam.getLeft()).count(gam.getRight()));
    return guardAndMinute.getLeft() * guardAndMinute.getRight();
  }

  private Map<Integer, Multiset<Integer>> buildGuardToMinutesAsleep(List<Action> actions) {
    Map<Integer, Multiset<Integer>> guardToMinutesAsleep = new HashMap<>();
    int currentGuard = -1;
    int sleepMinute = -1;
    for (Action action : actions) {
      if (action.action.startsWith("Guard")) {
        ensureSleepStatus(sleepMinute, -1);
        currentGuard = Integer.parseInt(action.action.split(" ")[1].substring(1));
      }
      else if (action.action.startsWith("falls")) {
        ensureSleepStatus(sleepMinute, -1);
        sleepMinute = action.minute();
      }
      else if (action.action.startsWith("wakes")) {
        ensureSleepStatus(sleepMinute, 1);
        Multiset<Integer> minutesAsleep =
            guardToMinutesAsleep.computeIfAbsent(currentGuard, (i) -> HashMultiset.create());
        IntStream.range(sleepMinute, action.minute()).forEach(minutesAsleep::add);
        sleepMinute = -1;
      }
    }
    return guardToMinutesAsleep;
  }

  // Helper method for making sure sleep status is correct.  zero and positive are treated as being asleep (hence the delta)
  private void ensureSleepStatus(int actual, int expected) {
    if (Math.signum(((double) actual) + .000001) != Math.signum(expected)) {
      throw fail(actual + ", " + expected);
    }
  }

  private static class Action implements Comparable<Action> {
    private static final Pattern pattern = Pattern.compile("^\\[1518-(\\d{2})-(\\d{2}) \\d{2}:(\\d{2})\\] (.+)$");

    private final int timestampCode;
    private final String action;

    private Action(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.timestampCode = (num(matcher, 1) << 16) + (num(matcher, 2) << 8) + (num(matcher, 3) << 0);
      this.action = matcher.group(4);
    }

    private int minute() {
      return timestampCode & 0b1111_1111;
    }

    @Override
    public int compareTo(Action other) {
      return Integer.compare(timestampCode, other.timestampCode);
    }
  }
}
