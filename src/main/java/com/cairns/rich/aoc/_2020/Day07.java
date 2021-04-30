package com.cairns.rich.aoc._2020;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day07 extends Base2020 {
  @Override
  protected void run() {
    Map<String, Rule> rules = getLookup(fullLoader.ml(Rule::new));
    System.out.println(countNumThatCanContainShinyGold(rules));
    System.out.println(countNumBagsInside(rules, new HashMap<>(), "shiny gold"));
  }
  
  private int countNumBagsInside(Map<String, Rule> rules, Map<String, Integer> bagCountCache, String bag) {
    if (!bagCountCache.containsKey(bag)) {
      int numBags = 0;
      Rule rule = rules.get(bag);
      for (String inside : rule.inside.keySet()) {
        numBags += rule.inside.get(inside) * (1 + countNumBagsInside(rules, bagCountCache, inside));
      }
      bagCountCache.put(bag, numBags);
    }
    return bagCountCache.get(bag);
  }
  
  private int countNumThatCanContainShinyGold(Map<String, Rule> rules) {
    int numThatCanContainShinyGold = 0;
    Map<String, Set<String>> insideCache = new HashMap<>();
    for (String bag : rules.keySet()) {
      Set<String> insides = getAllBags(rules, bag, insideCache);
      if (insides.contains("shiny gold")) {
        ++numThatCanContainShinyGold;
      }
    }
    return numThatCanContainShinyGold;
  }
  
  private Set<String> getAllBags(Map<String, Rule> rules, String curBag, Map<String, Set<String>> insideCache) {
    if (!insideCache.containsKey(curBag)) {
      Set<String> insides = new HashSet<>();
      Rule rule = rules.get(curBag);
      for (String inside : rule.inside.keySet()) {
        insides.add(inside);
        insides.addAll(getAllBags(rules, inside, insideCache));
      }
      insideCache.put(curBag, insides);
    }
    return insideCache.get(curBag);
  }
  
  private static class Rule implements HasId<String> {
    private static final Pattern pattern = Pattern.compile("^(.+) bags contain (.+)\\.$");
    private static final Pattern insidePattern = Pattern.compile("^(\\d+) (.+) bags?$");
    
    private final String outside;
    private final Map<String, Integer> inside = new HashMap<>();
    
    private Rule(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.outside = matcher.group(1);
      String parts = matcher.group(2);
      if (!"no other bags".equals(parts)) {
        for (String content : parts.split(", ")) {
          Matcher contentMatcher = matcher(insidePattern, content);
          inside.put(contentMatcher.group(2), Integer.parseInt(contentMatcher.group(1)));
        }
      }
    }
    
    @Override
    public String getId() {
      return outside;
    }
  }
}
