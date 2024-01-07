package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * We can reactivate the snow by cutting a graph correctly.  We are told that the cut needs to be exactly {@link #NUM_CUTS}
 * and this will roughly cut the graph in half.  This allows us to use a randomized algorithm with high confidence.
 */
class Day25 extends Base2023 {
  private static final ConfigToken<Double> sampleFactorToken = ConfigToken.of("sampleFactor", Double::parseDouble);

  private static final int MAX_TRIES = 4;
  private static final int NUM_CUTS = 3;

  /**
   * Finds the product of the sizes of 2 connected components that are created by cutting exactly
   * {@link #NUM_CUTS} connections.  We use a randomized approach {@link #NUM_CUTS} times to find
   * the most common connection and removing it.  At this point the graph should be severed into two
   * disjoint pieces.  If this fails, increase {@link #SAMPLE_FACTOR} to improve the odds of success.
   */
  @Override
  protected Object part1(Loader loader) {
    double sampleFactor = loader.getConfig(sampleFactorToken);
    for (int tryI = 0; tryI < MAX_TRIES; ++tryI) {
      Multimap<String, String> connections = parseConnections(loader);
      List<String> nodes = new ArrayList<>(connections.keySet());
      for (int cutI = 0; cutI < NUM_CUTS; ++cutI) {
        Connection cut = findMostCommonConnection(sampleFactor, connections, nodes);
        connections.remove(cut.from, cut.to);
        connections.remove(cut.to, cut.from);
      }
      List<Set<String>> components = findConnectedComponents(connections);
      if (components.size() == 2) {
        return components.stream().mapToLong(Set::size).reduce(Math::multiplyExact).getAsLong();
      }
    }
    throw fail("Increase sampleFactor");
  }

  /**
   * Uses a randomized algorithm to predicat which {@link Connection} is the most common in the graph.
   * We do this by picking two random (different) nodes and finding the shortest path between them.
   * Keeping track of all of the connections along the way will allow us to determine which specific
   * connection is the most common.  In theory, this should be one of the three cut candidates.  Roughly
   * 50% of the pairs will reside in different final components, meaning they are guaranteed to contain
   * one of the  connections to be cut.  Depending on the number of cut connections still present, we
   * expect roughly 16.66%-50% of all pairs to include a given cut connection.  For a sufficiently complex
   * graph and high enough sampleFactor, we have a high probability of identifying a correct connection.
   */
  private Connection findMostCommonConnection(double sampleFactor, Multimap<String, String> connections, List<String> nodes) {
    Supplier<String> randomNode = () -> nodes.get(ThreadLocalRandom.current().nextInt(nodes.size()));
    Multiset<Connection> pathConnections = HashMultiset.create();
    int numSamples = (int) (nodes.size() * sampleFactor);
    while (pathConnections.size() < numSamples) {
      String start = randomNode.get();
      String end = randomNode.get();
      if (!start.equals(end)) {
        pathConnections.addAll(findPath(connections, start, end));
      }
    }
    return getMax(pathConnections.elementSet(), pathConnections::count);
  }

  /**
   * Uses breadth first search to find the shortest path between the given start/end nodes.
   */
  private List<Connection> findPath(Multimap<String, String> connections, String start, String end) {
    Set<String> visited = new HashSet<>();
    visited.add(start);
    Deque<List<Connection>> q = new ArrayDeque<>();
    q.offerLast(List.of(new Connection(start, start)));
    while (!q.isEmpty()) {
      List<Connection> curPath = q.pollFirst();
      Connection lastConnection = safeGet(curPath, -1);
      for (String to : connections.get(lastConnection.to)) {
        if (visited.add(to)) {
          List<Connection> newPath = new ArrayList<>(curPath);
          newPath.add(new Connection(lastConnection.to, to));
          if (end.equals(to)) {
            return newPath.subList(1, newPath.size());    // remove initial (start,start) connection
          }
          q.offerLast(newPath);
        }
      }
    }
    throw fail(start + ", " + end);
  }

  /**
   * Uses breadth first search to find all of the connected components from the given graph.
   */
  private List<Set<String>> findConnectedComponents(Multimap<String, String> connections) {
    Set<String> visited = new HashSet<>();
    List<Set<String>> components = new ArrayList<>();
    for (String node : connections.keySet()) {
      if (!visited.contains(node)) {
        Set<String> component = new HashSet<>();
        bfs(
            node,
            (n) -> false,
            SearchState::getNumSteps,
            (cur, registrar) -> {
              component.add(cur);
              visited.add(cur);
              connections.get(cur).forEach(registrar);
            }
        );
        components.add(component);
      }
    }
    return components;
  }

  /**
   * Parses the connection graph from the input.  Both directions for each connection will be included,
   * meaning the {@link Multimap#keySet()} of the returned graph will have ALL nodes represented.
   */
  private Multimap<String, String> parseConnections(Loader loader) {
    Multimap<String, String> connections = HashMultimap.create();
    for (String line : loader.ml()) {
      String[] parts = line.split(": +");
      String from = parts[0];
      for (String to : parts[1].split(" +")) {
        connections.put(from, to);
        connections.put(to, from);
      }
    }
    return connections;
  }

  /**
   * Container class for an undirectioned connection.  Equality (and hash) are done irrespective of from/to ordering.
   */
  private static class Connection {
    private final String from;
    private final String to;

    private Connection(String from, String to) {
      this.from = from;
      this.to = to;
    }

    /**
     * Returns true if the given object is a {@link Connection} that has a from/to or to/from that matches this.
     */
    @Override
    public boolean equals(Object other) {
      Connection otherC = (Connection) other;
      return (from.equals(otherC.from) && to.equals(otherC.to))
          || (from.equals(otherC.to) && to.equals(otherC.from));
    }

    /**
     * Returns a hashcode that is consistent with another {@link Connection} which has the same from/to in either order.
     */
    @Override
    public int hashCode() {
      return from.hashCode() + to.hashCode();
    }
  }
}
