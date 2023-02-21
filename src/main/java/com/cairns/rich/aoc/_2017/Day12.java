package com.cairns.rich.aoc._2017;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day12 extends Base2017 {
  @Override
  protected void run() {
    Multimap<Integer, Integer> connections = parse(fullLoader.ml((line) -> line.split(" <-> ")));
    List<Set<Integer>> groups = computeGroups(connections);
    System.out.println("Part 1: " + groups.stream().filter((g) -> g.contains(0)).findFirst().get().size());
    System.out.println("Part 2: " + groups.size());
  }

  private List<Set<Integer>> computeGroups(Multimap<Integer, Integer> connections) {
    List<Set<Integer>> groups = new ArrayList<>();
    Set<Integer> visited = new HashSet<>();
    for (int startingFrom : connections.keySet()) {
      if (!visited.contains(startingFrom)) {
        Set<Integer> group = new HashSet<>();
        bfs(
            startingFrom,
            (s) -> false,
            SearchState::getNumSteps,
            (visit, registrar) -> {
              group.add(visit);
              connections.get(visit).forEach(registrar::accept);
            }
        );
        groups.add(group);
        visited.addAll(group);
      }
    }
    return groups;
  }

  private Multimap<Integer, Integer> parse(List<String[]> connectionDescs) {
    Multimap<Integer, Integer> connections = HashMultimap.create();
    for (String[] connectionDesc : connectionDescs) {
      int from = Integer.parseInt(connectionDesc[0]);
      Arrays.stream(connectionDesc[1].split(", *")).map(Integer::parseInt).forEach((to) -> connections.put(from, to));
    }
    return connections;
  }
}
