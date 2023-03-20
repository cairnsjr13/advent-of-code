package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day14 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return answerAfterSteps(loader, 10);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return answerAfterSteps(loader, 40);
  }

  private long answerAfterSteps(Loader2 loader, int numSteps) {
    List<String> lines = loader.ml(); // TODO: multi group
    String polymer = lines.get(0);
    Multimap<String, String> insertionRuleExpansions = HashMultimap.create();
    lines.subList(2, lines.size()).stream().map(InsertionRule::new).forEach((i) -> {
      insertionRuleExpansions.put(i.pair, i.pair.charAt(0) + i.insert);
      insertionRuleExpansions.put(i.pair, i.insert + i.pair.charAt(1));
    });
    return answerAfterSteps(polymer, insertionRuleExpansions, numSteps);
  }

  private long answerAfterSteps(String polymer, Multimap<String, String> insertionRuleExpansions, int numSteps) {
    Map<String, Long> before = polymerToPairs(polymer);
    for (int i = 0; i < numSteps; ++i) {
      before = step(insertionRuleExpansions, before);
    }
    Map<Character, Long> part2Freq = freq(before);
    char max = getMax(part2Freq.keySet(), part2Freq::get);
    char min = getMin(part2Freq.keySet(), part2Freq::get);
    long maxC = (part2Freq.get(max) + 1) / 2;
    long minC = (part2Freq.get(min) + 1) / 2;
    return maxC - minC;
  }

  private Map<String, Long> polymerToPairs(String polymer) {
    Map<String, Long> pairs = new HashMap<>();
    for (int i = 0; i < polymer.length() - 1; ++i) {
      String pair = polymer.substring(i, i + 2);
      increment(pairs, pair, 1);
    }
    return pairs;
  }

  private Map<String, Long> step(Multimap<String, String> insertionRuleExpansions, Map<String, Long> before) {
    Map<String, Long> after = new HashMap<>();
    for (String pair : before.keySet()) {
      for (String expansion : insertionRuleExpansions.get(pair)) {
        increment(after, expansion, before.get(pair));
      }
    }
    return after;
  }

  private Map<Character, Long> freq(Map<String, Long> polymer) {
    Map<Character, Long> freq = new HashMap<>();
    for (String poly : polymer.keySet()) {
      long delta = polymer.get(poly);
      increment(freq, poly.charAt(0), delta);
      increment(freq, poly.charAt(1), delta);
    }
    return freq;
  }

  private <T> void increment(Map<T, Long> totals, T totalKey, long delta) {
    totals.put(totalKey, totals.getOrDefault(totalKey, 0L) + delta);
  }

  private static final class InsertionRule {
    private static final Pattern pattern = Pattern.compile("^(..) -> (.)$");

    private final String pair;
    private final String insert;

    private InsertionRule(String line) {
      Matcher matcher = matcher(pattern, line);
      this.pair = matcher.group(1);
      this.insert = matcher.group(2);
    }
  }
}
