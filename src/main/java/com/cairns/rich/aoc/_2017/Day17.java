package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;

/**
 * A spin lock is chewing up compute power and memory.  We can simulate part 1 directly, but part 2 will need some math.
 *
 * The spin lock starts with a circular buffer with the number 0.  Each line of the input steps that number of
 * items through the buffer and then inserts the next number (1, 2, 3, etc).  Each step will increase the size of
 * the buffer.  The inserted value will become the next current position for the next input line and insertion number.
 */
class Day17 extends Base2017 {
  private static ConfigToken<Integer> numInsertsToken = ConfigToken.of("numInserts", Integer::parseInt);

  /**
   * Computes the value after the configured number of inserts into our spin lock pattern.
   * We use a {@link Node} to simulate the circular buffer list and do direct walks.
   */
  @Override
  protected Object part1(Loader loader) {
    int stepSize = Integer.parseInt(loader.sl());
    int numInserts = loader.getConfig(numInsertsToken);
    Node head = new Node(0);
    head.next = head;
    Node current = head;
    for (int nextValue = 1; nextValue <= numInserts; ++nextValue) {
      for (int i = 0; i < stepSize; ++i) {
        current = current.next;
      }
      Node next = new Node(nextValue);
      next.next = current.next;
      current = current.next = next;
    }
    return current.next.value;
  }

  /**
   * Computes the value after 0 after the configured number of inserts into our spin lock pattern.
   * We can simplify the simulation by only caring about the inserted value happens after the number 0.
   */
  @Override
  protected Object part2(Loader loader) {
    int stepSize = Integer.parseInt(loader.sl());
    int numInserts = loader.getConfig(numInsertsToken);
    int current = 0;
    int second = 0;
    for (int nextValueAndCurSize = 1; nextValueAndCurSize <= numInserts; ++nextValueAndCurSize) {
      current = (current + stepSize + 1) % nextValueAndCurSize;
      if (current == 0) {
        second = nextValueAndCurSize;
      }
    }
    return second;
  }

  /**
   * Linked List Node that has a value and a next.
   */
  private class Node {
    private final int value;
    private Node next;

    private Node(int value) {
      this.value = value;
    }
  }
}
