package com.cairns.rich.aoc._2018;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

class Day08 extends Base2018 {
  @Override
  protected void run() {
    Queue<Integer> inputs = new ArrayDeque<Integer>(fullLoader.sl(" +", Integer::parseInt));
    Node root = new Node(inputs);
    System.out.println(sumOfMetadatas(root));
    System.out.println(root.getValue());
  }
  
  private int sumOfMetadatas(Node node) {
    int sumMetadatas = 0;
    Queue<Node> toAdd = new LinkedList<>();
    toAdd.add(node);
    while (!toAdd.isEmpty()) {
      Node next = toAdd.poll();
      sumMetadatas += next.metadata.stream().mapToInt(Integer::intValue).sum();
      next.children.forEach(toAdd::offer);
    }
    return sumMetadatas;
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
    
    private int getValue() {
      if (cachedValue == -1) {
        if (children.isEmpty()) {
          cachedValue = metadata.stream().mapToInt(Integer::intValue).sum();
        }
        else {
          Map<Integer, Integer> cachedChildrenValues = new HashMap<>();
          cachedValue = metadata.stream().mapToInt((index) -> cachedChildrenValues.computeIfAbsent(index, (i) -> {
            int actualIndex = i - 1;
            return ((0 <= actualIndex) && (actualIndex < children.size()))
                ? children.get(actualIndex).getValue()
                : 0;
          })).sum();
        }
      }
      return cachedValue;
    }
  }
}
