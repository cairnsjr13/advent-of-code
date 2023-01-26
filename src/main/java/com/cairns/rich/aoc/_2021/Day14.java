package com.cairns.rich.aoc._2021;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Day14 extends Base2021 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    String polymer = lines.get(0);
    Multimap<String, String> insertionRuleExpansions = HashMultimap.create();
    lines.subList(2, lines.size()).stream().map(InsertionRule::new).forEach((i) -> {
      insertionRuleExpansions.put(i.pair, i.pair.charAt(0) + i.insert);
      insertionRuleExpansions.put(i.pair, i.insert + i.pair.charAt(1));
    });
    
    System.out.println(answerAfterSteps(polymer, insertionRuleExpansions, 10));
    System.out.println(answerAfterSteps(polymer, insertionRuleExpansions, 40));
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
      pairs.put(pair, pairs.getOrDefault(pair, 0L) + 1);
    }
    return pairs;
  }
  
  private Map<String, Long> step(Multimap<String, String> insertionRuleExpansions, Map<String, Long> before) {
    Map<String, Long> after = new HashMap<>();
    for (String pair : before.keySet()) {
      insertionRuleExpansions.get(pair).forEach((expansion) -> after.put(expansion, after.getOrDefault(expansion, 0L) + before.get(pair)));
    }
    return after;
  }
  
  private Map<Character, Long> freq(Map<String, Long> polymer) {
    Map<Character, Long> freq = new HashMap<>();
    for (String poly : polymer.keySet()) {
      freq.put(poly.charAt(0), freq.getOrDefault(poly.charAt(0), 0L) + polymer.get(poly));
      freq.put(poly.charAt(1), freq.getOrDefault(poly.charAt(1), 0L) + polymer.get(poly));
    }
    return freq;
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
