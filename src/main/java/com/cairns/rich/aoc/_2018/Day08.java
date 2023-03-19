package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.ToIntFunction;

class Day08 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    return computeAnswer(loader, Node::getRecursiveMetadataSum);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return computeAnswer(loader, Node::getValue);
  }

  private int computeAnswer(Loader2 loader, ToIntFunction<Node> toAnswer) {
    Queue<Integer> inputs = new ArrayDeque<>(loader.sl(" +", Integer::parseInt));
    return toAnswer.applyAsInt(new Node(inputs));
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
