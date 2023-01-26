package com.cairns.rich.aoc._2022;

import java.util.List;
import java.util.PriorityQueue;

public class Day01 extends Base2022 {
  @Override
  protected void run() throws Throwable {
    List<Integer> elves = fullLoader.gDelim("", (l) -> l.stream().mapToInt(Integer::parseInt).sum());
    System.out.println(sumOfTop(elves, 1));
    System.out.println(sumOfTop(elves, 3));
  }
  
  private int sumOfTop(List<Integer> elves, int top) {
    PriorityQueue<Integer> pq = new PriorityQueue<>();
    for (int elf : elves) {
      if (pq.size() < top) {
        pq.offer(elf);
      }
      else if (elf > pq.peek()) {
        pq.poll();
        pq.offer(elf);
      }
    }
    return pq.stream().mapToInt(Integer::intValue).sum();
  }
}
