package com.cairns.rich.aoc._2018;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class Day12 extends Base2018 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    TreeSet<Integer> plants = buildInit(lines.get(0).split(" ")[2]);
    Set<Integer> plantRules = buildPlantRules(lines.subList(2, lines.size()));
    System.out.println(sumIndexesAfterGens(plants, plantRules, 20));
    System.out.println(sumIndexesLargeGens(plants, plantRules, 10_000));
  }
  
  private long sumIndexesLargeGens(TreeSet<Integer> plants, Set<Integer> plantRules, int chunk) {
    long numInChunk = sumIndexesAfterGens(plants, plantRules, chunk);
    long multiplier = (numInChunk / 10_000) * 10_000;
    long mod = numInChunk % 10_000;
    return (50_000_000_000L / chunk) * multiplier + mod;
  }
  
  private int sumIndexesAfterGens(TreeSet<Integer> plants, Set<Integer> plantRules, int numGens) {
    plants = new TreeSet<>(plants);
    TreeSet<Integer> nextGen = new TreeSet<>();
    for (int i = 0; i < numGens; ++i) {
      nextGen.clear();
      
      int minPlant = plants.first();
      int maxPlant = plants.last();
      for (int inspect = minPlant - 2; inspect <= maxPlant + 2; ++inspect) {
        int indicator = (((plants.contains(inspect - 2)) ? 1 : 0) << 0)
                      + (((plants.contains(inspect - 1)) ? 1 : 0) << 1)
                      + (((plants.contains(inspect - 0)) ? 1 : 0) << 2)
                      + (((plants.contains(inspect + 1)) ? 1 : 0) << 3)
                      + (((plants.contains(inspect + 2)) ? 1 : 0) << 4);
        if (plantRules.contains(indicator)) {
          nextGen.add(inspect);
        }
      }
      
      TreeSet<Integer> swap = plants;
      plants = nextGen;
      nextGen = swap;
    }
    return plants.stream().mapToInt(Integer::intValue).sum();
  }
  
  private Set<Integer> buildPlantRules(List<String> ruleLines) {
    Set<Integer> plantRules = new HashSet<>();
    for (String ruleLine : ruleLines) {
      if (ruleLine.endsWith("#")) {
        plantRules.add(
            (((ruleLine.charAt(0) == '#') ? 1 : 0) << 0)
          + (((ruleLine.charAt(1) == '#') ? 1 : 0) << 1)
          + (((ruleLine.charAt(2) == '#') ? 1 : 0) << 2)
          + (((ruleLine.charAt(3) == '#') ? 1 : 0) << 3)
          + (((ruleLine.charAt(4) == '#') ? 1 : 0) << 4)
        );
      }
    }
    return plantRules;
  }
  
  private TreeSet<Integer> buildInit(String init) {
    TreeSet<Integer> plants = new TreeSet<>();
    for (int i = 0; i < init.length(); ++i) {
      if (init.charAt(i) == '#') {
        plants.add(i);
      }
    }
    return plants;
  }
}
