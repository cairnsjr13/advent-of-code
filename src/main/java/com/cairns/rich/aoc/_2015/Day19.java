package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Rudolph is sick and we need to make him some medicine.  We can do this by doing molecule replacement.
 */
class Day19 extends Base2015 {
  /**
   * Returns the number of distinct molecules that we can generate from one replacement.
   */
  @Override
  protected Object part1(Loader loader) {
    Pair<String, Multimap<String, String>> moleculeAndReplacements = computeMoleculeAndReplacements(loader);
    String molecule = moleculeAndReplacements.getLeft();
    Multimap<String, String> replacements = moleculeAndReplacements.getRight();

    Set<String> molecules = new HashSet<>();
    for (int i = 0; i < molecule.length(); ++i) {
      for (int length = 1; length <= 2; ++length) {
        if (i + length <= molecule.length()) {
          String candidate = molecule.substring(i, i + length);
          for (String replacement : replacements.get(candidate)) {
            molecules.add(molecule.substring(0, i) + replacement + molecule.substring(i + length));
          }
        }
      }
    }
    return molecules.size();
  }

  /**
   * Returns the fewest number of steps required to go from base 'e' all the way to the target
   * molecule using the input replacements.  We do this by computing the distance by greedily
   * picking a random replacement from the options at each step.  While this is not academically
   * guaranteed to return an answer, we can loop until we find one.  The randomization options
   * are not so expansive that the runtime explodes to an unreasonable level.
   */
  @Override
  protected Object part2(Loader loader) {
    Pair<String, Multimap<String, String>> moleculeAndReplacements = computeMoleculeAndReplacements(loader);
    String molecule = moleculeAndReplacements.getLeft();
    Multimap<String, String> replacements = moleculeAndReplacements.getRight();
    while (true) {
      Integer greedy = greedyDistance(molecule, replacements);
      if (greedy != null) {
        return greedy;
      }
    }
  }

  /**
   * Returns the minimum number of replacements it will take to go from the molecule to 'e'.
   * Repeatedly tries to shrink our target string by picking a random valid contraction and applying it.
   * Eventually we will either reach 'e' (and return the number of steps required) or hit a dead end and return null.
   * While randomness isnt usually a great feeling, this quickly does get to answer given the branching factors involved.
   */
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

  /**
   * Finds all of the "end state"s of replacements (the rightSides of the replacement) that are found in the target string.
   * These will represent the options we have to contract a string to try to get closer to 'e'.
   */
  private List<String> getCandidates(String end, List<String> rightSides) {
    return rightSides.stream().filter(end::contains).collect(Collectors.toList());
  }

  /**
   * Parser method that finds the target molecule (left) and all of the allowed molecule replacements (right).
   */
  private Pair<String, Multimap<String, String>> computeMoleculeAndReplacements(Loader loader) {
    List<String> inputs = loader.ml();
    String molecule = inputs.get(inputs.size() - 1);
    Multimap<String, String> replacements = HashMultimap.create();
    for (String input : inputs.subList(0, inputs.size() - 2)) {
      String[] replacement = input.split(" => ");
      replacements.put(replacement[0], replacement[1]);
    }
    return Pair.of(molecule, replacements);
  }
}
