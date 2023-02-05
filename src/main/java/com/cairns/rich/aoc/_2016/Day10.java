package com.cairns.rich.aoc._2016;

import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

class Day10 extends Base2016 {
  private static final Pattern valPattern = Pattern.compile("^value (\\d+) goes to (bot \\d+)$");
  private static final Pattern botPattern = Pattern.compile("^(bot \\d+) gives low to ((bot|output) \\d+) and high to ((bot|output) \\d+)$");

  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    TreeMultimap<String, Integer> holdings = TreeMultimap.create();
    List<Runnable> inits = new ArrayList<>();
    Map<String, Consumer<Integer>> receivers = new HashMap<>();
    TreeMap<Integer, Integer> outputs = new TreeMap<>();
    IntStream.range(0, 100).forEach((i) -> receivers.put("output " + i, (receive) -> outputs.put(i, receive)));
    for (String line : lines) {
      if (line.startsWith("value")) {
        Matcher matcher = matcher(valPattern, line);
        String bot = matcher.group(2);
        int val = num(matcher, 1);
        inits.add(() -> receivers.get(bot).accept(val));
      }
      else if (line.startsWith("bot")) {
        Matcher matcher = matcher(botPattern, line);
        String bot = matcher.group(1);
        String loTo = matcher.group(2);
        String hiTo = matcher.group(4);
        receivers.put(bot, (receive) -> {
          holdings.put(bot, receive);
          NavigableSet<Integer> holding = holdings.get(bot);
          if (holding.size() == 2) {
            int lo = holding.first();
            int hi = holding.last();
            if ((lo == 17) && (hi == 61)) {
              System.out.println(bot + " compared the targets");
            }
            holding.clear();
            receivers.get(loTo).accept(lo);
            receivers.get(hiTo).accept(hi);
          }
        });
      }
      else {
        throw fail(line);
      }
    }
    inits.forEach(Runnable::run);
    System.out.println(outputs.get(0) * outputs.get(1) * outputs.get(2));
  }
}
