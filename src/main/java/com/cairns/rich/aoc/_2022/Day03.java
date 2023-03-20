package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Day03 extends Base2022 {
  @Override
  protected Object part1(Loader2 loader) {
    return loader.ml(Rucksack::new).stream().map((r) -> r.inBoth).mapToInt(this::priority).sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Rucksack> rucksacks = loader.ml(Rucksack::new);
    int total = 0;
    for (int i = 0; i < rucksacks.size(); i += 3) {
      Set<Integer> overlap = Sets.intersection(
          rucksacks.get(i + 0).contents,
          Sets.intersection(rucksacks.get(i + 1).contents, rucksacks.get(i + 2).contents)
      );
      total += priority(overlap.iterator().next());
    }
    return total;
  }

  private int priority(int ch) {
    return 1 + ((Character.isLowerCase(ch)) ? (ch - 'a') : 26 + (ch - 'A'));
  }

  private static class Rucksack {
    private final Set<Integer> contents;
    private int inBoth;

    private Rucksack(String line) {
      int compartmentSize = line.length() / 2;
      Set<Integer> first = line.substring(0, compartmentSize).chars().boxed().collect(Collectors.toSet());
      Set<Integer> second = line.substring(compartmentSize).chars().boxed().collect(Collectors.toSet());
      this.contents = Sets.union(first, second);
      this.inBoth = Sets.intersection(first, second).iterator().next();
    }
  }
}
