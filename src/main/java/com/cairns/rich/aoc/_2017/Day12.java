package com.cairns.rich.aoc._2017;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

class Day12 extends Base2017 {
  @Override
  protected void run() {
    Map<Integer, Connection> connections = getLookup(fullLoader.ml(Connection::new));
    List<Set<Integer>> groups = computeGroups(connections);
    for (Set<Integer> group : groups) {
      if (group.contains(0)) {
        System.out.println("Part 1: " + group.size());
        break;
      }
    }
    System.out.println("Part 2: " + groups.size());
  }
  
  private List<Set<Integer>> computeGroups(Map<Integer, Connection> connections) {
    List<Set<Integer>> groups = new ArrayList<>();
    Set<Integer> visited = new HashSet<>();
    for (int startingFrom : connections.keySet()) {
      if (!visited.contains(startingFrom)) {
        Set<Integer> group = computeGroup(connections, startingFrom);
        visited.addAll(group);
        groups.add(group);
      }
    }
    return groups;
  }
  
  private Set<Integer> computeGroup(Map<Integer, Connection> connections, int startingFrom) {
    Set<Integer> group = new HashSet<>();
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> toVisit = new ArrayDeque<>();
    toVisit.add(startingFrom);
    while (!toVisit.isEmpty()) {
      int visit = toVisit.poll();
      group.add(visit);
      for (int to : connections.get(visit).pipesTo) {
        if (!visited.contains(to)) {
          visited.add(to);
          toVisit.add(to);
        }
      }
    }
    return group;
  }
  
  private static class Connection implements HasId<Integer> {
    private final int id;
    private final Set<Integer> pipesTo;
    
    private Connection(String spec) {
      String parts[] = spec.split(" <-> ");
      this.id = Integer.parseInt(parts[0]);
      this.pipesTo = Arrays.stream(parts[1].split(", ")).map(Integer::parseInt).collect(Collectors.toSet());
    }
    
    @Override
    public Integer getId() {
      return id;
    }
  }
}
