package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We have a bunch of programs that are holding other programs in grouped towers.  They all
 * have weights and it appears as if they are unbalanced because of one that is incorrect.
 * We need to figure out the root as well as the proper weight of this incorrect program.
 */
class Day07 extends Base2017 {
  /**
   * Returns the name of the program that is at the bottom of the tower (held by none).
   */
  @Override
  protected Object part1(Loader loader) {
    return getBottomProgram(getLookup(loader.ml(Program::new)));
  }

  /**
   * Computes the weight the incorrectly weighted program would need to be in order to balance the tower.
   */
  @Override
  protected Object part2(Loader loader) {
    Map<String, Program> programs = getLookup(loader.ml(Program::new));
    String bottom = getBottomProgram(programs);
    return getNeededWeightForIncorrectProgram(programs, bottom);
  }

  /**
   * Finds the name of a program that is not held by any other program.
   */
  private String getBottomProgram(Map<String, Program> programs) {
    Set<String> all = new HashSet<>(programs.keySet());
    programs.values().stream().forEach((program) -> all.removeAll(program.holds));
    return all.iterator().next();
  }

  /**
   * Finds the program that is improperly weighted and computes what it should be.
   *
   * It does this by finding a program who's immediate subtowers are not all the same weight.
   * When this happens, there will be two different weight classes on the disk.  One of them will have
   * a single program belonging to that weight.  This is the tower that contains the improperly weighted
   * program.  Once we find a disk where all of the subtowers are the same weight, that indicates the
   * program holding those subtowers (the inspect program) is the wrong weight.  To figure out what it
   * should weigh, we figure out how much the sibling towers weigh (needToBe var) and subtract out what the
   * program's held programs currently weigh.  This leaves us with exactly what the wrong program should weigh.
   */
  private int getNeededWeightForIncorrectProgram(Map<String, Program> programs, String inspect) {
    Map<String, Integer> cache = new HashMap<>();
    int needToBe = 0;
    while (true) {
      Multimap<Integer, String> holdingWeights = getHoldingWeights(programs, cache, inspect);
      if (holdingWeights.keySet().size() == 1) {
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

  /**
   * Computes a mapping of weight to program names for all of the programs the given program holds.
   */
  private Multimap<Integer, String> getHoldingWeights(Map<String, Program> programs, Map<String, Integer> cache, String name) {
    Multimap<Integer, String> weights = HashMultimap.create();
    programs.get(name).holds.forEach((held) -> weights.put(getWeight(programs, cache, held), held));
    return weights;
  }

  /**
   * Returns a cached, recursively computed weight for the given program.  A program's weight is comprised of
   * its own direct weight plus the sum of all of the (recursive) weights of programs that it is holding.
   */
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

  /**
   * Container object describing a program.  A program has a unique name, weight and references to other programs it is holding.
   */
  private static class Program implements HasId<String> {
    private static final Pattern pattern = Pattern.compile("^([^ ]+) \\((\\d+)\\)( -> (.+))?$");

    private final String name;
    private final int weight;
    private final Set<String> holds = new HashSet<>();

    private Program(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.name = matcher.group(1);
      this.weight = num(matcher, 2);
      String holdSpec = matcher.group(4);
      if (holdSpec != null) {
        Arrays.stream(holdSpec.split(", *")).forEach(holds::add);
      }
    }

    @Override
    public String getId() {
      return name;
    }
  }
}
