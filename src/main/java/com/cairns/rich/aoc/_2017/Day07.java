package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

class Day07 extends Base2017 {
  @Override
  protected void run() {
    Map<String, Program> programs = getLookup(fullLoader.ml(Program::new));
    String bottom = getBottomProgram(programs);
    System.out.println(bottom);
    System.out.println(getNeededWeightForIncorrectProgram(programs, bottom));
  }
  
  private String getBottomProgram(Map<String, Program> programs) {
    return Sets.difference(
        programs.keySet(),
        programs.values().stream().map((program) -> program.holds).flatMap(Set::stream).collect(Collectors.toSet())
    ).stream().findFirst().get();
  }
  
  private int getNeededWeightForIncorrectProgram(Map<String, Program> programs, String inspect) {
    Map<String, Integer> cache = new HashMap<>();
    int needToBe = getWeight(programs, cache, inspect);
    while (true) {
      Multimap<Integer, String> holdingWeights = getHoldingWeights(programs, cache, inspect);
      if (holdingWeights == null) {
        break;
      }
      
      for (int weight : holdingWeights.keySet()) {
        if (holdingWeights.get(weight).size() > 1) {
          needToBe = weight;
        }
        else {
          inspect = holdingWeights.get(weight).iterator().next();
        }
      }
    }
    return needToBe - getWeight(programs, cache, inspect) + programs.get(inspect).weight;
  }
  
  private Multimap<Integer, String> getHoldingWeights(Map<String, Program> programs, Map<String, Integer> cache, String name) {
    Multimap<Integer, String> weights = HashMultimap.create();
    programs.get(name).holds.forEach((held) -> weights.put(getWeight(programs, cache, held), held));
    return (weights.keySet().size() == 1) ? null : weights;
  }
  
  private int getWeight(Map<String, Program> programs, Map<String, Integer> cache, String name) {
    if (!cache.containsKey(name)) {
      Program program = programs.get(name);
      cache.put(
          name,
          program.weight + program.holds.stream().mapToInt((held) -> getWeight(programs, cache, held)).sum()
      );
    }
    return cache.get(name);
  }
  
  private static class Program implements HasId<String> {
    private static final Pattern pattern = Pattern.compile("^([^ ]+) \\((\\d+)\\)( -> (.+))?$");
    
    private final String name;
    private final int weight;
    private final Set<String> holds = new HashSet<>();
    
    private Program(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.name = matcher.group(1);
      this.weight = Integer.parseInt(matcher.group(2));
      String holdSpec = matcher.group(4);
      if (holdSpec != null) {
        Arrays.stream(holdSpec.split(", *")).forEach(holds::add);
      }
    }
    
    @Override
    public String getId() {
      return name;
    }
    
    @Override
    public String toString() {
      return "{" + name + " - " + weight + " - " + holds + "}";
    }
  }
}
