package com.cairns.rich.aoc._2020;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class Day23 extends Base2020 {
  @Override
  protected void run() {
    outputSimpleGame("389125467", 10);
    outputSimpleGame("389125467", 100);
    outputSimpleGame("685974213", 100);
    outputComplexGame("389125467", 10_000_000);
    outputComplexGame("685974213", 10_000_000);
  }

  private void outputSimpleGame(String input, int numMoves) {
    Map<Integer, Node> lookup = runGame(input, numMoves, input.length());
    Node curCup = lookup.get(1);
    for (int i = 0; i < 8; ++i) {
      curCup = curCup.cw;
      System.out.print(curCup.label);
    }
    System.out.println();
  }

  private void outputComplexGame(String input, int numMoves) {
    Map<Integer, Node> lookup = runGame(input, numMoves, 1_000_000);
    Node curCup = lookup.get(1);
    long output = ((long) curCup.cw.label) * ((long) curCup.cw.cw.label);
    System.out.println(output);
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
