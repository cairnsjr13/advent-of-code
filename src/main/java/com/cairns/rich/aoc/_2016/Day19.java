package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;

/**
 * The elves are playing are playing incorrect versions of white elephant.
 * Let's help them figure out who is going to end up with all of the presents.
 */
class Day19 extends Base2016 {
  /**
   * Returns the index of the elf that ends up with all of the presents when presents are stolen from the immediate next.
   */
  @Override
  protected Object part1(Loader loader) {
    int numElfs = Integer.parseInt(loader.sl());
    Elf current = createElfChain(numElfs);
    while (current.next != current) {
      current = current.next = current.next.next;
    }
    return current.index;
  }

  /**
   * Returns the index of the elf that ends up with all of the presents when presents are stolen from the elf across.
   */
  @Override
  protected Object part2(Loader loader) {
    int numElfs = Integer.parseInt(loader.sl());
    Elf current = createElfChain(numElfs);
    Elf remove = current.next;
    Elf removePrevious = current;
    for (int i = 0; i < numElfs / 2 - 1; ++i) {
      removePrevious = remove;
      remove = remove.next;
    }
    for (boolean jump = false; current != remove; jump = !jump) {
      removePrevious.next = remove.next;
      if (jump) {
        removePrevious = removePrevious.next;
      }
      remove = removePrevious.next;
      current = current.next;
    }
    return current.index;
  }

  /**
   * Creates a circular linked list of elves where elf 1 is pointed to by the last elf.
   */
  private Elf createElfChain(int numElfs) {
    Elf first = new Elf(1);
    Elf last = first;
    for (int i = 2; i <= numElfs; ++i) {
      last = last.next = new Elf(i);
    }
    return last.next = first;
  }

  /**
   * This is effectively a linked list node that has an index and a next pointer.
   */
  private static class Elf {
    private final int index;
    private Elf next = null;

    private Elf(int index) {
      this.index = index;
    }
  }
}
