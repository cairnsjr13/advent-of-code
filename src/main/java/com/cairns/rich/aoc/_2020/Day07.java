package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day07 extends Base2020 {
  private static final String SHINY_GOLD = "shiny gold";
  @Override
  protected Object part1(Loader loader) {
    Map<String, Set<String>> insideCache = new HashMap<>();
    Map<String, Rule> rules = getLookup(loader.ml(Rule::new));
    return rules.keySet().stream()
        .map((bag) -> getAllBags(rules, bag, insideCache))
        .filter((insides) -> insides.contains(SHINY_GOLD))
        .count();
  }

  @Override
  protected Object part2(Loader loader) {
    return countNumBagsInside(getLookup(loader.ml(Rule::new)), new HashMap<>(), SHINY_GOLD);
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

  private Set<String> getAllBags(Map<String, Rule> rules, String curBag, Map<String, Set<String>> insideCache) {
    if (!insideCache.containsKey(curBag)) {
      Set<String> insides = new HashSet<>();
      for (String inside : rules.get(curBag).inside.keySet()) {
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
          inside.put(contentMatcher.group(2), num(contentMatcher, 1));
        }
      }
    }

    @Override
    public String getId() {
      return outside;
    }
  }
}
