package com.cairns.rich.aoc._2018;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

class Day08 extends Base2018 {
  @Override
  protected void run() {
    Queue<Integer> inputs = new ArrayDeque<>(fullLoader.sl(" +", Integer::parseInt));
    Node root = new Node(inputs);
    System.out.println(root.getRecursiveMetadataSum());
    System.out.println(root.getValue());
  }

  private static class Node {
    private final List<Node> children = new ArrayList<>();
    private final List<Integer> metadata = new ArrayList<>();;
    private int cachedValue = -1;

    private Node(Queue<Integer> inputs) {
      int numChildren = inputs.poll();
      int numMetadata = inputs.poll();
      for (int i = 0; i < numChildren; ++i) {
        children.add(new Node(inputs));
      }
      for (int i = 0; i < numMetadata; ++i) {
        metadata.add(inputs.poll());
      }
    }

    private int getRecursiveMetadataSum() {
      return metadata.stream().mapToInt(Integer::intValue).sum()
           + children.stream().mapToInt(Node::getRecursiveMetadataSum).sum();
    }

    private int getValue() {
      if (cachedValue == -1) {
        cachedValue = metadata.stream().mapToInt((i) -> {
          if (children.isEmpty()) {
            return i;
          }
          return ((1 <= i) && (i <= children.size())) ? children.get(i - 1).getValue() : 0;
        }).sum();
      }
      return cachedValue;
    }
  }
}
