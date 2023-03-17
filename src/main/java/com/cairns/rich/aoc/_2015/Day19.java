package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class Day19 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<String> inputs = loader.ml();
    String molecule = inputs.get(inputs.size() - 1);
    Multimap<String, String> replacements = HashMultimap.create();
    for (String input : inputs.subList(0, inputs.size() - 2)) {
      String[] replacement = input.split(" => ");
      replacements.put(replacement[0], replacement[1]);
    }
    result.part1(getUniqueSingleReplacementMolecules(molecule, replacements));
    while (true) {
      Integer greedy = greedyDistance(molecule, replacements);
      if (greedy != null) {
        result.part2(greedy);
        return;
      }
    }
  }

  private Integer greedyDistance(String end, Multimap<String, String> replacements) {
    Map<String, String> contractions =
        replacements.entries().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    List<String> rightSides = new ArrayList<>(contractions.keySet());
    int steps = 0;
    while (true) {
      if ("e".equals(end)) {
        return steps;
      }
      List<String> candidates = getCandidates(end, rightSides);
      if (candidates.isEmpty()) {
        return null;
      }
      String candidate = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
      int index = end.indexOf(candidate);
      if (index != -1) {
        ++steps;
        end = end.substring(0, index) + contractions.get(candidate) + end.substring(index + candidate.length());
      }
    }
  }

  private List<String> getCandidates(String end, List<String> rightSides) {
    return rightSides.stream().filter((r) -> end.contains(r)).collect(Collectors.toList());
  }

  private int getUniqueSingleReplacementMolecules(String start, Multimap<String, String> replacements) {
    Set<String> molecules = new HashSet<>();
    forEachReplacement(start, replacements, molecules::add);
    return molecules.size();
  }

  private void forEachReplacement(
      String molecule,
      Multimap<String, String> replacements,
      Consumer<String> action
  ) {
    for (int i = 0; i < molecule.length(); ++i) {
      for (int length = 1; length <= 2; ++length) {
        if (i + length <= molecule.length()) {
          String candidate = molecule.substring(i, i + length);
          for (String replacement : replacements.get(candidate)) {
            action.accept(molecule.substring(0, i) + replacement + molecule.substring(i + length));
          }
        }
      }
    }
  }
}
