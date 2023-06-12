package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * We have a partitioned network where programs communicate through pipes.  We need to analyze the network
 * and figure out how large program 0's accessible network is as well as the number of disjoint groups.
 */
class Day12 extends Base2017 {
  /**
   * Computes the total number of programs that program 0 can communicate with directly or indirectly.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeAnswer(loader, (groups) -> groups.stream().filter((g) -> g.contains(0)).findFirst().get());
  }

  /**
   * Computes the number of disjoint network groups.  Each group is can communicate to any program within it, but none outside.
   */
  @Override
  protected Object part2(Loader loader) {
    return computeAnswer(loader, (g) -> g);   // TODO: for some reason Function.identity() doesnt work here
  }

  /**
   * Executes a topological search where we compute all of the disjoint groups in the network.
   * The answer is computed by applying the given toSizedAnswer and returning the results size.
   */
  private int computeAnswer(Loader loader, Function<List<Set<Integer>>, Collection<?>> toSizedAnswer) {
    Multimap<Integer, Integer> connections = parse(loader);
    List<Set<Integer>> groups = new ArrayList<>();
    Set<Integer> visited = new HashSet<>();
    for (int startingFrom : connections.keySet()) {
      if (!visited.contains(startingFrom)) {
        Set<Integer> group = new HashSet<>();
        bfs(
            startingFrom,
            (s) -> false,   // search everything
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
    return toSizedAnswer.apply(groups).size();
  }

  /**
   * Parses the input into a {@link Multimap} where each program is mapped to all of the programs its directly connected to.
   */
  private Multimap<Integer, Integer> parse(Loader loader) {
    List<String[]> connectionDescs = loader.ml((line) -> line.split(" <-> "));
    Multimap<Integer, Integer> connections = HashMultimap.create();
    for (String[] connectionDesc : connectionDescs) {
      int from = Integer.parseInt(connectionDesc[0]);
      Arrays.stream(connectionDesc[1].split(", *")).map(Integer::parseInt).forEach((to) -> connections.put(from, to));
    }
    return connections;
  }
}
