package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;

class Day08 extends Base2025 {
  private static final ConfigToken<Integer> numConnectionsToken = ConfigToken.of("numConnections", Integer::parseInt);

  /**
   * By running a configured number of shortest connections, a list of disjoint circuits can be derived.
   * From this list, we return the product of the sizes of the three largest circuits.
   *
   * The terminal condition for our circuit search is simply the configured number of connections.
   */
  @Override
  protected Object part1(Loader loader) {
    int numConnections = loader.getConfig(numConnectionsToken);
    List<JunctionBox> boxes = loader.ml(JunctionBox::new);
    MutableInt connectionCounter = new MutableInt();
    List<Set<JunctionBox>> circuits = findLastConnectedAndCircuits(
        boxes,
        (boxToCircuit) -> connectionCounter.incrementAndGet() == numConnections
    ).getRight();
    Collections.sort(circuits, Comparator.comparing(Set::size));
    return safeGet(circuits, -1).size()
         * safeGet(circuits, -2).size()
         * safeGet(circuits, -3).size();
  }

  /**
   * Continuously connecting the shortest available connection will eventually lead to a circuit that includes all
   * {@link JunctionBox}es.  We return the product of x values of the earliest connection that leads to this.
   *
   * The terminal condition for our circuit search is when the first circuit contains all {@link JunctionBox}es.
   *
   * NOTE: The incremental tracking approach is substantially faster than
   *       linear or logarithmic search based on the number of connections.
   */
  @Override
  protected Object part2(Loader loader) {
    List<JunctionBox> boxes = loader.ml(JunctionBox::new);
    JBoxPair lastConnection = findLastConnectedAndCircuits(
        boxes,
        (boxToCircuit) -> boxToCircuit.get(boxes.get(0)).size() == boxes.size()
    ).getLeft();
    return lastConnection.left.x * lastConnection.right.x;
  }

  /**
   * Helper function to create connections until the given isDone predicate signals to stop.
   * By starting with every {@link JunctionBox} in its own circuit, we can merge circuits when making connections.
   * While it is true that {@link JunctionBox} will be merged into numerous circuits, by always selecting the
   * largest as a target, we can bound the total number of movements each junction box must go through.
   */
  private Pair<JBoxPair, List<Set<JunctionBox>>> findLastConnectedAndCircuits(
      List<JunctionBox> boxes,
      Predicate<Map<JunctionBox, Set<JunctionBox>>> isDone
  ) {
    Map<JunctionBox, Set<JunctionBox>> boxToCircuit = boxes.stream().collect(Collectors.toMap(
        Function.identity(),
        (p) -> new HashSet<>(Set.of(p))
    ));
    BiConsumer<Set<JunctionBox>, Set<JunctionBox>> mergeCircuitsXIntoY = (x, y) -> {
      y.addAll(x);
      x.forEach((p) -> boxToCircuit.put(p, y));
    };
    PriorityQueue<JBoxPair> pairs = buildPairs(boxes);
    while (!pairs.isEmpty()) {
      JBoxPair connected = pairs.poll();
      Set<JunctionBox> leftCircuit = boxToCircuit.get(connected.left);
      Set<JunctionBox> rightCircuit = boxToCircuit.get(connected.right);
      if (leftCircuit != rightCircuit) {
        if (leftCircuit.size() < rightCircuit.size()) {
          mergeCircuitsXIntoY.accept(leftCircuit, rightCircuit);
        }
        else {
          mergeCircuitsXIntoY.accept(rightCircuit, leftCircuit);
        }
      }

      if (isDone.test(boxToCircuit)) {
        return Pair.of(connected, boxToCircuit.values().stream().distinct().collect(Collectors.toList()));
      }
    }
    throw fail();
  }

  /**
   * Constructs a {@link PriorityQueue} sorted by distance of all pairs of the given {@link JunctionBox}es.
   */
  private PriorityQueue<JBoxPair> buildPairs(List<JunctionBox> junctionBoxes) {
    PriorityQueue<JBoxPair> pairs = new PriorityQueue<>(Comparator.comparing(JBoxPair::euclidDist));
    for (int i = 0; i < junctionBoxes.size(); ++i) {
      for (int j = i + 1; j < junctionBoxes.size(); ++j) {
        pairs.add(new JBoxPair(junctionBoxes.get(i), junctionBoxes.get(j)));
      }
    }
    return pairs;
  }

  /**
   * Container describing the location of a junction box in 3D space.
   */
  private static class JunctionBox {
    private static final Pattern pattern = Pattern.compile("^(\\d+),(\\d+),(\\d+)$");

    private final long x;
    private final long y;
    private final long z;

    private JunctionBox(String line) {
      Matcher matcher = matcher(pattern, line);
      this.x = num(matcher, 1);
      this.y = num(matcher, 2);
      this.z = num(matcher, 3);
    }
  }

  /**
   * Container describing two {@link JunctionBox}es and their distance.
   */
  private static class JBoxPair {
    private final JunctionBox left;
    private final JunctionBox right;

    private JBoxPair(JunctionBox left, JunctionBox right) {
      this.left = left;
      this.right = right;
    }

    /**
     * Straight line euclidean distance between the two {@link JunctionBox}es.
     */
    private double euclidDist() {
      return Math.sqrt(
          Math.pow((left.x - right.x), 2)
          + Math.pow((left.y - right.y), 2)
          + Math.pow((left.z - right.z), 2)
      );
    }
  }
}
