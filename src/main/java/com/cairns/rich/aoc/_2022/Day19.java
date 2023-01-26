package com.cairns.rich.aoc._2022;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

public class Day19 extends Base2022 {
  private static final char[] materials = { 'g', 'b', 'c', 'o' };
  private static final Map<Character, Integer> buildCutoff = Map.of('g', 1, 'b', 2, 'c', 3, 'o', 4);
  private static final Supplier<Multiset<Character>> initRobots = () -> HashMultiset.create(List.of('o'));
  
  @Override
  protected void run() throws Throwable {
    List<Blueprint> blueprints = fullLoader.ml(Blueprint::new);
    System.out.println(maxFromBlueprints(24, blueprints, 0, (i) -> i + 1, (l, r) -> l + r));
    System.out.println(maxFromBlueprints(32, blueprints.subList(0, 3), 1, (i) -> 1, (l, r) -> l * r));
  }
  
  private int maxFromBlueprints(
      int time,
      List<Blueprint> blueprints,
      int reductionInit,
      IntUnaryOperator indexFactor,
      IntBinaryOperator reduceOp
  ) {
    ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Future<Integer>> futures = new ArrayList<>();
    blueprints.stream()
        .map((b) -> exec.submit(() -> maxFromBlueprint(b, 0, 0, time, initRobots.get(), HashMultiset.create())))
        .forEach(futures::add);
    List<Integer> results = futures.stream().map((f) -> quietly(() -> f.get())).collect(Collectors.toList());
    exec.shutdown();
    int reduction = reductionInit;
    for (int i = 0; i < results.size(); ++i) {
      reduction = reduceOp.applyAsInt(reduction, indexFactor.applyAsInt(i) * results.get(i));
    }
    return reduction;
  }
  
  private int maxFromBlueprint(
      Blueprint blueprint,
      int soFar,
      int bestSeen,
      int timeLeft,
      Multiset<Character> robotCounts,
      Multiset<Character> resourceCounts
  ) {
    if (timeLeft < 0) {
      throw fail();
    }
    if ((timeLeft == 0) || (theoreticalBest(soFar, timeLeft, robotCounts) <= bestSeen)) {
      return soFar;
    }
    int gRobotsBefore = robotCounts.count('g');
    int max = soFar + timeLeft * gRobotsBefore;
    for (char robot : materials) {
      if (robotCounts.count(robot) < blueprint.robotLimit.get(robot)) {
        Integer timeToBuild = timeToBuild(blueprint, robot, robotCounts, resourceCounts);
        if ((timeToBuild != null) &&
            (timeToBuild < timeLeft) &&
            (buildCutoff.get(robot) <= timeLeft - timeToBuild)
        ) {
          robotCounts.entrySet().forEach((e) -> resourceCounts.add(e.getElement(), timeToBuild * e.getCount()));
          blueprint.costs.row(robot).forEach(resourceCounts::remove);
          robotCounts.add(robot);
          max = Math.max(max, maxFromBlueprint(
              blueprint,
              soFar + (timeToBuild * gRobotsBefore),
              max,
              timeLeft - timeToBuild,
              robotCounts,
              resourceCounts
          ));
          robotCounts.remove(robot);
          blueprint.costs.row(robot).forEach(resourceCounts::add);
          robotCounts.entrySet().forEach((e) -> resourceCounts.remove(e.getElement(), timeToBuild * e.getCount()));
        }
      }
    }
    return max;
  }
  
  private int theoreticalBest(int soFar, int timeLeft, Multiset<Character> robotCounts) {
    return soFar
         + robotCounts.count('g') * timeLeft
         + (timeLeft * (timeLeft - 1) / 2);   // if we build geode robot every turn
  }
  
  private Integer timeToBuild(
      Blueprint blueprint,
      char robot,
      Multiset<Character> robotCounts,
      Multiset<Character> resourceCounts
  ) {
    int timeNeeded = 0;
    Map<Character, Integer> costs = blueprint.costs.row(robot);
    for (char material : costs.keySet()) {
      int lacking = costs.get(material) - resourceCounts.count(material);
      if (lacking > 0) {
        int perTime = robotCounts.count(material);
        if (perTime == 0) {
          return null;
        }
        timeNeeded = Math.max(timeNeeded, (lacking / perTime) + ((lacking % perTime > 0) ? 1 : 0));
      }
    }
    return timeNeeded + 1;
  }
  
  private static class Blueprint {
    private static final Pattern pattern = Pattern.compile("^Each .+ robot costs (\\d+) ore( and (\\d+) .+)?$");
    
    private final Table<Character, Character, Integer> costs = HashBasedTable.create();
    private final Map<Character, Integer> robotLimit;
    
    private Blueprint(String line) {
      String[] pieces = line.split("[:\\.]+ ");
      Matcher obsidianMatcher = matcher(pattern, pieces[3]);
      Matcher geodeMatcher = matcher(pattern, pieces[4]);
      costs.put('o', 'o', num(matcher(pattern, pieces[1]), 1));
      costs.put('c', 'o', num(matcher(pattern, pieces[2]), 1));
      costs.put('b', 'o', num(obsidianMatcher, 1));
      costs.put('b', 'c', num(obsidianMatcher, 3));
      costs.put('g', 'o', num(geodeMatcher, 1));
      costs.put('g', 'b', num(geodeMatcher, 3));
      this.robotLimit = Map.of(
          'o', getMax(costs.column('o').values(), Function.identity()),
          'c', costs.get('b', 'c'),
          'b', costs.get('g', 'b'),
          'g', Integer.MAX_VALUE
      );
    }
  }
}
