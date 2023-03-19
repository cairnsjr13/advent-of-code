package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

class Day07 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    Multimap<Character, Character> stepDependsOn = buildDeps(loader.ml());
    TreeSet<Character> steps = new TreeSet<>();
    steps.addAll(stepDependsOn.keySet());
    steps.addAll(stepDependsOn.values());
    LinkedHashSet<Character> order = new LinkedHashSet<>();
    while (order.size() < steps.size()) {
      order.add(steps.stream().filter((s) -> !order.contains(s) && canRun(stepDependsOn, order, s)).findFirst().get());
    }
    return order.stream().map(Object::toString).collect(Collectors.joining());
  }

  @Override
  protected Object part2(Loader2 loader) {
    Multimap<Character, Character> stepDependsOn = buildDeps(loader.ml());
    TreeSet<Character> stepsLeft = new TreeSet<>();
    stepsLeft.addAll(stepDependsOn.keySet());
    stepsLeft.addAll(stepDependsOn.values());
    int numWorkers = 5;
    int timeBuffer = 60;

    Set<Character> finished = new HashSet<>();
    PriorityQueue<Pair<Character, Integer>> workers = new PriorityQueue<>(Comparator.comparing(Pair::getRight));
    int currentTime = 0;
    while (!stepsLeft.isEmpty() || !workers.isEmpty()) {
      if (workers.size() < numWorkers) {
        Optional<Character> canRun = stepsLeft.stream().filter((step) -> canRun(stepDependsOn, finished, step)).findFirst();
        if (canRun.isPresent()) {
          char step = canRun.get();
          stepsLeft.remove(step);
          workers.add(Pair.of(step, getFinishTime(timeBuffer, currentTime, step)));
          continue;
        }
      }
      Pair<Character, Integer> worker = workers.poll();
      finished.add(worker.getLeft());
      currentTime = Math.max(currentTime, worker.getRight());
    }
    return currentTime;
  }

  private boolean canRun(Multimap<Character, Character> stepDependsOn, Set<Character> finished, Character step) {
    return finished.containsAll(stepDependsOn.get(step));
  }

  private int getFinishTime(int timeBuffer, int currentTime, char step) {
    return currentTime + timeBuffer + step - 'A' + 1;
  }

  private Multimap<Character, Character> buildDeps(List<String> lines) {
    Multimap<Character, Character> stepDependsOn = TreeMultimap.create();
    for (String line : lines) {
      char step = line.charAt("Step X must be finished before step ".length());
      char dependsOn = line.charAt("Step ".length());
      stepDependsOn.put(step, dependsOn);
    }
    return stepDependsOn;
  }
}
