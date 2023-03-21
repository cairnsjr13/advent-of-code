package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class Day23 extends Base2020 {
  @Override
  protected Object part1(Loader loader) {
    String input = loader.sl();
    StringBuilder out = new StringBuilder();
    Map<Integer, Node> lookup = runGame(input, 100, input.length());
    Node curCup = lookup.get(1);
    for (int i = 0; i < 8; ++i) {
      curCup = curCup.cw;
      out.append(curCup.label);
    }
    return out;
  }

  @Override
  protected Object part2(Loader loader) {
    Node curCup = runGame(loader.sl(), 10_000_000, 1_000_000).get(1);
    return ((long) curCup.cw.label) * ((long) curCup.cw.cw.label);
  }

  private Map<Integer, Node> runGame(String input, int numMoves, int padTo) {
    Map<Integer, Node> lookup = new HashMap<>();
    Node currentCup = build(input, lookup, padTo);
    for (int i = 0; i < numMoves; ++i) {
      Node next1Cup = currentCup.cw;
      Node next2Cup = next1Cup.cw;
      Node next3Cup = next2Cup.cw;
      currentCup.cw = next3Cup.cw;
      Node destinationCup = destinationCup(lookup, currentCup, Set.of(next1Cup, next2Cup, next3Cup));
      Node destinationCw = destinationCup.cw;
      destinationCup.cw = next1Cup;
      next3Cup.cw = destinationCw;
      currentCup = currentCup.cw;
    }
    return lookup;
  }

  private Node destinationCup(Map<Integer, Node> lookup, Node currentCup, Set<Node> nextCups) {
    while (true) {
      int destinationLabel = 1 + ((currentCup.label + (lookup.size() - 2)) % lookup.size());
      currentCup = lookup.get(destinationLabel);
      if (!nextCups.contains(currentCup)) {
        return currentCup;
      }
    }
  }

  private Node build(String input, Map<Integer, Node> lookup, int padTo) {
    Node head = new Node(input, 0);
    Node tail = head;
    lookup.put(head.label, head);
    for (int i = 1; i < input.length(); ++i) {
      Node next = new Node(input, i);
      tail = tail.cw = next;
      lookup.put(next.label, next);
    }
    for (int i = input.length() + 1; i <= padTo; ++i) {
      Node next = new Node(i);
      tail = tail.cw = next;
      lookup.put(next.label, next);
    }
    tail.cw = head;
    return head;
  }

  private static class Node {
    private final int label;
    private Node cw;

    private Node(int label) {
      this.label = label;
    }

    private Node(String input, int index) {
      this(input.charAt(index) - '0');
    }

    @Override
    public boolean equals(Object obj) {
      return label == ((Node) obj).label;
    }

    @Override
    public int hashCode() {
      return label;
    }
  }
}
