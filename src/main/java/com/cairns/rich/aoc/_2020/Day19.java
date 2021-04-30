package com.cairns.rich.aoc._2020;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

class Day19 extends Base2020 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    int indexOfBlank = lines.indexOf("");
    List<String> ruleLines = lines.subList(0, indexOfBlank);
    List<String> messages = lines.subList(indexOfBlank + 1, lines.size());
    
    Map<Integer, Character> atomRules = new HashMap<>();
    Multimap<Integer, Rule> expansionRules = HashMultimap.create();
    parseRules(ruleLines, atomRules, expansionRules);
    
    System.out.println(countMatches(atomRules, expansionRules, messages));
    expansionRules.put(8, new Rule(8, List.of(42, 8)));
    expansionRules.put(11, new Rule(11, List.of(42, 11, 31)));
    System.out.println(countMatches(atomRules, expansionRules, messages));
  }
  
  private long countMatches(
      Map<Integer, Character> atomRules,
      Multimap<Integer, Rule> expansionRules,
      List<String> messages
  ) {
    Stack<Integer> remaining = new Stack<>();
    remaining.push(0);
    return messages.stream().filter((message) -> match(atomRules, expansionRules, message, remaining)).count();
  }
  
  private boolean match(
      Map<Integer, Character> atomRules,
      Multimap<Integer, Rule> expansionRules,
      String message,
      Stack<Integer> remaining
  ) {
    if (message.length() == 0) {
      return remaining.isEmpty();
    }
    else if (remaining.isEmpty()) {
      return false;
    }

    int nextRuleIndex = remaining.pop();
    try {
      if (atomRules.containsKey(nextRuleIndex)) {
        return (message.charAt(0) == atomRules.get(nextRuleIndex))
            && match(atomRules, expansionRules, message.substring(1), remaining);
      }
      else {
        for (Rule nextRule : expansionRules.get(nextRuleIndex)) {
          reversePush(nextRule, remaining);
          try {
            if (match(atomRules, expansionRules, message, remaining)) {
              return true;
            }
          }
          finally {
            popAll(nextRule, remaining);
          }
        }
      }
      return false;
    }
    finally {
      remaining.push(nextRuleIndex);
    }
  }
  
  private void reversePush(Rule rule, Stack<Integer> remaining) {
    for (int i = rule.subRules.size() - 1; i >= 0; --i) {
      remaining.push(rule.subRules.get(i));
    }
  }
  
  private void popAll(Rule rule, Stack<Integer> remaining) {
    for (int i = 0; i < rule.subRules.size(); ++i) {
      int popped = remaining.pop();
      if (popped != rule.subRules.get(i)) {
        throw fail("Out of order " + i + " " + popped + " " + rule.subRules);
      }
    }
  }
  
  private void parseRules(
      List<String> lines,
      Map<Integer, Character> atomRules,
      Multimap<Integer, Rule> expansionRules
  ) {
    for (String line : lines) {
      int colonIndex = line.indexOf(':');
      int index = Integer.parseInt(line.substring(0, colonIndex));
      int quoteAt = line.indexOf('"');
      if (quoteAt != -1) {
        atomRules.put(index, line.charAt(quoteAt + 1));
      }
      else {
        for (String rulePiece : line.substring(colonIndex + 2).split(" *\\| *")) {
          String[] subRulePieces = rulePiece.split(" +");
          List<Integer> subRules = Arrays.stream(subRulePieces).map(Integer::parseInt).collect(Collectors.toList());
          Rule rule = new Rule(index, subRules);
          expansionRules.put(index, rule);
        }
      }
    }
  }
  
  private static class Rule {
    private final int index;
    private final List<Integer> subRules;
    
    private Rule(int index, List<Integer> subRules) {
      this.index = index;
      this.subRules = subRules;
    }
    
    @Override
    public String toString() {
      return "{" + index + "\t" + subRules + "}";
    }
  }
}
