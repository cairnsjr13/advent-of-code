package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class Day20 extends Base2022 {
  @Override
  protected Object part1(Loader2 loader) {
    return solve(loader, 1, 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return solve(loader, 811_589_153, 10);
  }

  private long solve(Loader2 loader, long factor, int loops) {
    List<Integer> input = loader.ml(Integer::parseInt);
    List<Node> nodes = buildNodes(input.stream().map((n) -> n * factor).collect(Collectors.toList()));
    for (int loop = 0; loop < loops; ++loop) {
      for (Node node : nodes) {
        if (node.value % (nodes.size() - 1) != 0) {
          Function<Node, Node> movement = (node.value < 0) ? (n) -> n.prev : (n) -> n.next;
          long mag = (Math.abs(node.value) % (nodes.size() - 1)) + ((node.value < 0) ? 1 : 0);
          Node insertAfter = node;
          for (long i = 0; i < mag; ++i) {
            insertAfter = movement.apply(insertAfter);
          }
          // unhook myself
          node.prev.next = node.next;
          node.next.prev = node.prev;
          // correct myself
          node.prev = insertAfter;
          node.next = insertAfter.next;
          // insert myself
          node.prev.next = node;
          node.next.prev = node;
        }
      }
    }
    long total = 0;
    Node current = nodes.stream().filter((n) -> n.value == 0).findFirst().get();
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 1000; ++j) {
        current = current.next;
      }
      total += current.value;
    }
    return total;
  }

  private List<Node> buildNodes(List<Long> nums) {
    List<Node> nodes = nums.stream().map(Node::new).collect(Collectors.toList());
    for (int i = 1; i < nodes.size(); ++i) {
      Node current = nodes.get(i);
      current.prev = nodes.get(i - 1);
      current.prev.next = current;
    }
    Node first = nodes.get(0);
    first.prev = nodes.get(nodes.size() - 1);
    first.prev.next = first;
    return nodes;
  }

  private static class Node {
    private final long value;
    private Node prev;
    private Node next;

    private Node(long value) {
      this.value = value;
    }
  }
}
