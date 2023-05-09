package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * We stumble across a factory where robots are comparing microchips.  Lets see what the output is.
 */
class Day10 extends Base2016 {
  private static final ConfigToken<Integer> sigLo = ConfigToken.of("sigLo", Integer::parseInt);
  private static final ConfigToken<Integer> sigHi = ConfigToken.of("sigHi", Integer::parseInt);
  private static final Pattern valPattern = Pattern.compile("^value (\\d+) goes to (bot \\d+)$");
  private static final Pattern botPattern = Pattern.compile("^(bot \\d+) gives low to ((bot|output) \\d+) and high to ((bot|output) \\d+)$");

  /**
   * Simulates the robots and returns the number of the robot comparing {@link #sigLo} and {@link #sigHi}.
   */
  @Override
  protected Object part1(Loader loader) {
    return simulate(
        loader,
        (state) -> state.comparings.get(loader.getConfig(sigLo), loader.getConfig(sigHi)).substring("bot ".length())
    );
  }

  /**
   * Simulates the robots and returns the product of the first three output positions.
   */
  @Override
  protected Object part2(Loader loader) {
    return simulate(loader, (state) -> IntStream.range(0, 3).map(state.outputs::get).reduce(1, Math::multiplyExact));
  }

  /**
   * Executes each input line in order where each line represents an action a robot must take.
   * A robot does not take its action until it has two microchips.  Each robot that compares
   * microchips will register itself as the comparator.  Output signals are stored in the state.
   */
  private Object simulate(Loader loader, Function<State, Object> toAnswer) {
    State state = new State();
    for (String line : loader.ml()) {
      if (line.startsWith("value")) {
        Matcher matcher = matcher(valPattern, line);
        String bot = matcher.group(2);
        int val = num(matcher, 1);
        state.inits.add(() -> state.receivers.get(bot).accept(val));
      }
      else if (line.startsWith("bot")) {
        Matcher matcher = matcher(botPattern, line);
        String bot = matcher.group(1);
        String loTo = matcher.group(2);
        String hiTo = matcher.group(4);
        state.receivers.put(bot, (receive) -> {
          state.holdings.put(bot, receive);
          NavigableSet<Integer> holding = state.holdings.get(bot);
          if (holding.size() == 2) {
            int lo = holding.first();
            int hi = holding.last();
            holding.clear();
            state.receivers.get(loTo).accept(lo);
            state.receivers.get(hiTo).accept(hi);
            state.comparings.put(lo, hi, bot);
          }
        });
      }
      else {
        throw fail(line);
      }
    }
    state.inits.forEach(Runnable::run);
    return toAnswer.apply(state);
  }

  /**
   * Container class responsible for holding the varying data-structures to simulate the robots.
   */
  private static class State {
    private final List<Runnable> inits = new ArrayList<>();
    private final TreeMultimap<String, Integer> holdings = TreeMultimap.create();
    private final Map<String, Consumer<Integer>> receivers = new HashMap<>();
    private final TreeMap<Integer, Integer> outputs = new TreeMap<>();
    private final Table<Integer, Integer, String> comparings = HashBasedTable.create();

    private State() {
      IntStream.range(0, 100).forEach((i) -> receivers.put("output " + i, (receive) -> outputs.put(i, receive)));
    }
  }
}
