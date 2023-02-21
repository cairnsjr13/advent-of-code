package com.cairns.rich.aoc._2017;

class Day17 extends Base2017 {
  @Override
  protected void run() {
    System.out.println(getValueAfterFinalCurrentNode(380, 2017));
    System.out.println(getSecondValue(380, 50_000_000));
  }

  private int getValueAfterFinalCurrentNode(int stepSize, int lastValue) {
    Node head = new Node(0);
    head.next = head;
    Node current = head;
    for (int nextValue = 1; nextValue <= lastValue; ++nextValue) {
      for (int i = 0; i < stepSize; ++i) {
        current = current.next;
      }
      Node next = new Node(nextValue);
      next.next = current.next;
      current = current.next = next;
    }
    return current.next.value;
  }

  private int getSecondValue(int stepSize, int lastValue) {
    int current = 0;
    int second = 0;
    for (int nextValueAndCurSize = 1; nextValueAndCurSize <= lastValue; ++nextValueAndCurSize) {
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
