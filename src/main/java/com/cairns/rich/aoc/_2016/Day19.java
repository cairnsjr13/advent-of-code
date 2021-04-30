package com.cairns.rich.aoc._2016;

class Day19 extends Base2016 {
  @Override
  protected void run() {
    System.out.println(getIndexOfLastElfStealLeft(5));
    System.out.println(getIndexOfLastElfStealLeft(3001330));
    System.out.println(getIndexOfLastElfStealAcross(5));
    System.out.println(getIndexOfLastElfStealAcross(6));
    System.out.println(getIndexOfLastElfStealAcross(3001330));
  }
  
  private int getIndexOfLastElfStealLeft(int numElfs) {
    Elf current = createElfChain(numElfs);
    while (current.next != current) {
      current = current.next = current.next.next;
    }
    return current.index;
  }
  
  private int getIndexOfLastElfStealAcross(int numElfs) {
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
  
  private Elf createElfChain(int numElfs) {
    Elf first = new Elf(1);
    Elf last = first;
    for (int i = 2; i <= numElfs; ++i) {
      last = last.next = new Elf(i);
    }
    return last.next = first;
  }
  
  private static class Elf {
    private final int index;
    private Elf next = null;
    
    private Elf(int index) {
      this.index = index;
    }
  }
}
