package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Day16 extends Base2015 {
  private static final Map<String, Att> nameLookup = Arrays.stream(Att.values()).collect(Collectors.toMap(
      (att) -> att.name().toLowerCase(),
      Function.identity()
  ));

  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<EnumMap<Att, Integer>> sues = loader.ml(this::parseLine);
    Map<Att, Predicate<Integer>> filters = new HashMap<>();
    filters.put(Att.Children, (v) -> v == 3);
    filters.put(Att.Cats, (v) -> v == 7);
    filters.put(Att.Samoyeds, (v) -> v == 2);
    filters.put(Att.Pomeranians, (v) -> v == 3);
    filters.put(Att.Akitas, (v) -> v == 0);
    filters.put(Att.Vizslas, (v) -> v == 0);
    filters.put(Att.Goldfish, (v) -> v == 5);
    filters.put(Att.Trees, (v) -> v == 3);
    filters.put(Att.Cars, (v) -> v == 2);
    filters.put(Att.Perfumes, (v) -> v == 1);
    result.part1(filter(sues, filters));

    filters.put(Att.Cats, (v) -> v > 7);
    filters.put(Att.Trees, (v) -> v > 3);
    filters.put(Att.Pomeranians, (v) -> v < 3);
    filters.put(Att.Goldfish, (v) -> v < 5);
    result.part2(filter(sues, filters));
  }

  private int filter(List<EnumMap<Att, Integer>> sues, Map<Att, Predicate<Integer>> tests) {
    for (int i = 0; i < sues.size(); ++i) {
      EnumMap<Att, Integer> sue = sues.get(i);
      if (passes(sue, tests)) {
        return i + 1;
      }
    }
    throw fail();
  }

  private boolean passes(EnumMap<Att, Integer> sue, Map<Att, Predicate<Integer>> tests) {
    for (Att att : tests.keySet()) {
      if (sue.containsKey(att) && !tests.get(att).test(sue.get(att))) {
        return false;
      }
    }
    return true;
  }

  private enum Att {
    Children,
    Cats,
    Samoyeds,
    Pomeranians,
    Akitas,
    Vizslas,
    Goldfish,
    Trees,
    Cars,
    Perfumes;
  }

  private EnumMap<Att, Integer> parseLine(String line) {
    EnumMap<Att, Integer> sue = new EnumMap<>(Att.class);
    String right = line.substring(line.indexOf(':') + 2);
    String[] attSpecs = right.split(", ");
    for (String attSpec : attSpecs) {
      String[] parts = attSpec.split(": ");
      sue.put(nameLookup.get(parts[0]), Integer.parseInt(parts[1]));
    }
    return sue;
  }
}
