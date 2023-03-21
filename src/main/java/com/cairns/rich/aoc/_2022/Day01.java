package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader;
import java.util.List;
import java.util.PriorityQueue;

class Day01 extends Base2022 {
  @Override
  protected Object part1(Loader loader) {
    return sumOfTop(loader, 1);
  }

  @Override
  protected Object part2(Loader loader) {
    return sumOfTop(loader, 3);
  }

  private int sumOfTop(Loader loader, int top) {
    List<Integer> elves = loader.gDelim("", (l) -> l.stream().mapToInt(Integer::parseInt).sum());
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
