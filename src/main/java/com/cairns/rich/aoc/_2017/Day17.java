package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;

class Day17 extends Base2017 {
  @Override
  protected Object part1(Loader loader) {
    int stepSize = Integer.parseInt(loader.sl());
    Node head = new Node(0);
    head.next = head;
    Node current = head;
    for (int nextValue = 1; nextValue <= 2017; ++nextValue) {
      for (int i = 0; i < stepSize; ++i) {
        current = current.next;
      }
      Node next = new Node(nextValue);
      next.next = current.next;
      current = current.next = next;
    }
    return current.next.value;
  }

  @Override
  protected Object part2(Loader loader) {
    int stepSize = Integer.parseInt(loader.sl());
    int current = 0;
    int second = 0;
    for (int nextValueAndCurSize = 1; nextValueAndCurSize <= 50_000_000; ++nextValueAndCurSize) {
      current = (current + stepSize + 1) % nextValueAndCurSize;
      if (current == 0) {
        second = nextValueAndCurSize;
      }
    }
    return second;
  }

  private class Node {
    private final int value;
    private Node next;

    private Node(int value) {
      this.value = value;
    }
  }
}
