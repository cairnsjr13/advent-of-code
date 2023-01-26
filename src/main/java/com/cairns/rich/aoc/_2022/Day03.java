package com.cairns.rich.aoc._2022;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

public class Day03 extends Base2022 {
  @Override
  protected void run() throws Throwable {
    List<Rucksack> rucksacks = fullLoader.ml(Rucksack::new);
    System.out.println(getPerSackCommonTotalPriority(rucksacks));
    System.out.println(getGroupedCommonTotalPriority(rucksacks));
  }
  
  private int getPerSackCommonTotalPriority(List<Rucksack> rucksacks) {
    return rucksacks.stream().map((r) -> r.inBoth).mapToInt(this::priority).sum();
  }
  
  private int getGroupedCommonTotalPriority(List<Rucksack> rucksacks) {
    int total = 0;
    for (int i = 0; i < rucksacks.size(); i += 3) {
      Set<Character> overlap = Sets.intersection(
          rucksacks.get(i + 0).contents,
          Sets.intersection(rucksacks.get(i + 1).contents, rucksacks.get(i + 2).contents)
      );
      total += priority(overlap.iterator().next());
    }
    return total;
  }
  
  private int priority(char ch) {
    return 1 + ((Character.isLowerCase(ch)) ? (ch - 'a') : 26 + (ch - 'A'));
  }
  
  private static class Rucksack {
    private final Set<Character> contents;
    private char inBoth;
    
    private Rucksack(String line) {
      int compartmentSize = line.length() / 2;
      Set<Character> first = line.substring(0, compartmentSize).chars().mapToObj((i) -> (char) i).collect(Collectors.toSet());
      Set<Character> second = line.substring(compartmentSize).chars().mapToObj((i) -> (char) i).collect(Collectors.toSet());
      this.contents = Sets.union(first, second);
      this.inBoth = Sets.intersection(first, second).iterator().next();
    }
  }
}
