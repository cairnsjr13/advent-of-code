package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Searching a network for connected components will allow us to locate the chief historian.
 */
public class Day23 extends Base2024 {
  /**
   * Finds all triple connected nodes that have a node that starts with a 't'.
   * We can easily find triple connected nodes by considering every single common connection between pairs of nodes.
   * This works because the pairs are guaranteed to be connected and any connection in common will provide the third.
   */
  @Override
  protected Object part1(Loader loader) {
    Map<Set<String>, Set<String>> connectedToCommons = computeConnectedToCommons(parseConnections(loader));
    Set<Set<String>> triples = new HashSet<>();
    connectedToCommons.forEach((connected, commons) -> commons.forEach((common) -> {
      Set<String> triple = new HashSet<>(connected);
      triple.add(common);
      triples.add(triple);
    }));
    return triples.stream().filter((triple) -> triple.stream().anyMatch((elem) -> elem.startsWith("t"))).count();
  }

  /**
   * Finds the largest completely connected component in the graph and returns its nodes ordered, joined by commas.
   * The largest connected component can be found by considering every pair of connected nodes and their common
   * connections.  This reduces the number of nodes to consider and makes a recursive power-set approach feasible.
   */
  @Override
  protected Object part2(Loader loader) {
    HashMultimap<String, String> connections = parseConnections(loader);
    Map<Set<String>, Set<String>> connectedToCommons = computeConnectedToCommons(connections);
    Set<String> largestConnectedComponent = Set.of();
    for (Set<String> option : connectedToCommons.keySet()) {
      Set<String> optionsLargestConnectedComponent = findLargestConnectedComponent(
          largestConnectedComponent.size(),
          connections,
          option,
          new ArrayList<>(connectedToCommons.get(option)),
          0
      );
      if (optionsLargestConnectedComponent.size() > largestConnectedComponent.size()) {
        largestConnectedComponent = optionsLargestConnectedComponent;
      }
    }
    return largestConnectedComponent.stream().sorted().collect(Collectors.joining(","));
  }

  /**
   * Parses a {@link HashMultimap} where every connection has both directions registered.
   */
  private HashMultimap<String, String> parseConnections(Loader loader) {
    List<String[]> connectionSpecs = loader.ml((line) -> line.split("-"));
    HashMultimap<String, String> connections = HashMultimap.create();
    for (String[] connectionSpec : connectionSpecs) {
      connections.put(connectionSpec[0], connectionSpec[1]);
      connections.put(connectionSpec[1], connectionSpec[0]);
    }
    return connections;
  }

  /**
   * Computes a mapping from pairs of nodes to all of the nodes they have in common.
   * Node pairs are only included if they have common connections.
   */
  private Map<Set<String>, Set<String>> computeConnectedToCommons(HashMultimap<String, String> connections) {
    Map<Set<String>, Set<String>> connectedToCommons = new HashMap<>();
    for (String from : connections.keySet()) {
      Set<String> fromConnections = connections.get(from);
      for (String to : fromConnections) {
        Set<String> commons = Sets.intersection(fromConnections, connections.get(to));
        if (!commons.isEmpty()) {
          connectedToCommons.put(new HashSet<>(Set.of(from, to)), commons);
        }
      }
    }
    return connectedToCommons;
  }

  /**
   * A recursive backtracking algorithm that finds the largest completely connected component by either
   * adding or not adding the node at the current index of common connections.  A short circuit param
   * is included to terminate early if it is not possible to achieve the required minimum size anymore.
   */
  private Set<String> findLargestConnectedComponent(
      int shortCircuit,
      HashMultimap<String, String> connections,
      Set<String> includedSoFar,
      List<String> commons,
      int index
  ) {
    if (includedSoFar.size() + (commons.size() - index) < shortCircuit) {
      return Set.of();
    }
    if (index == commons.size()) {
      return new HashSet<>(includedSoFar);
    }

    Set<String> largestWithout =
        findLargestConnectedComponent(shortCircuit, connections, includedSoFar, commons, index + 1);
    shortCircuit = Math.max(shortCircuit, largestWithout.size());

    String candidate = commons.get(index);
    if (!connections.get(candidate).containsAll(includedSoFar)) {
      return largestWithout;
    }
    includedSoFar.add(candidate);
    Set<String> largestWith =
        findLargestConnectedComponent(shortCircuit, connections, includedSoFar, commons, index + 1);
    includedSoFar.remove(candidate);

    return (largestWithout.size() > largestWith.size()) ? largestWithout : largestWith;
  }
}
