package com.cairns.rich.aoc._2018;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.IntPredicate;

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
    long multiplier = (numInChunk / chunk) * chunk;
    long mod = numInChunk % chunk;
    return (50_000_000_000L / chunk) * multiplier + mod;
  }

  private int sumIndexesAfterGens(TreeSet<Integer> init, Set<Integer> plantRules, int numGens) {
    TreeSet<Integer> plants = new TreeSet<>(init);
    TreeSet<Integer> nextGen = new TreeSet<>();
    for (int i = 0; i < numGens; ++i) {
      nextGen.clear();
      doGeneration(plantRules, plants, nextGen);
      TreeSet<Integer> swap = plants;
      plants = nextGen;
      nextGen = swap;
    }
    return plants.stream().mapToInt(Integer::intValue).sum();
  }

  private void doGeneration(Set<Integer> plantRules, TreeSet<Integer> plants, TreeSet<Integer> nextGen) {
    int minPlant = plants.first();
    int maxPlant = plants.last();
    for (int inspect = minPlant - 2; inspect <= maxPlant + 2; ++inspect) {
      int offset = inspect - 2;
      int indicator = buildIndicator((j) -> plants.contains(offset + j));
      if (plantRules.contains(indicator)) {
        nextGen.add(inspect);
      }
    }
  }

  private Set<Integer> buildPlantRules(List<String> ruleLines) {
    Set<Integer> plantRules = new HashSet<>();
    for (String ruleLine : ruleLines) {
      if (ruleLine.endsWith("#")) {
        plantRules.add(buildIndicator((i) -> ruleLine.charAt(i) == '#'));
      }
    }
    return plantRules;
  }

  private int buildIndicator(IntPredicate testForIndex) {
    return (((testForIndex.test(0)) ? 1 : 0) << 0)
         + (((testForIndex.test(1)) ? 1 : 0) << 1)
         + (((testForIndex.test(2)) ? 1 : 0) << 2)
         + (((testForIndex.test(3)) ? 1 : 0) << 3)
         + (((testForIndex.test(4)) ? 1 : 0) << 4);
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
