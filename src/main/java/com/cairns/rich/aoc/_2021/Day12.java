package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import java.util.List;

class Day12 extends Base2021 {
  @Override
  protected Object part1(Loader loader) {
    return numPathsFrom(loader, false);
  }

  @Override
  protected Object part2(Loader loader) {
    return numPathsFrom(loader, true);
  }

  private int numPathsFrom(Loader loader, boolean allowRevisit) {
    List<String[]> inputs = loader.ml((line) -> line.split("-"));
    Multimap<String, String> edges = HashMultimap.create();
    for (String[] input : inputs) {
      edges.put(input[0], input[1]);
      edges.put(input[1], input[0]);
    }
    edges.values().removeIf("start"::equals);

    Multiset<String> cavesVisited = HashMultiset.create();
    cavesVisited.add("start");
    return numPathsFrom(allowRevisit, edges, cavesVisited, "start");
  }

  private int numPathsFrom(boolean allowRevisit, Multimap<String, String> edges, Multiset<String> cavesVisited, String current)
  {
    if ("end".equals(current)) {
      return 1;
    }
    int totalPaths = 0;
    for (String to : edges.get(current)) {
      if (to.chars().allMatch(Character::isLowerCase) && cavesVisited.contains(to)) {
        if (allowRevisit) {
          cavesVisited.add(to);
          totalPaths += numPathsFrom(false, edges, cavesVisited, to);
          cavesVisited.remove(to);
        }
        continue;
      }
      cavesVisited.add(to);
      totalPaths += numPathsFrom(allowRevisit, edges, cavesVisited, to);
      cavesVisited.remove(to);
    }
    return totalPaths;
  }
}
