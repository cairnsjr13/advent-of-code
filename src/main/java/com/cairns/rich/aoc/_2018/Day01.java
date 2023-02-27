package com.cairns.rich.aoc._2018;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day01 extends Base2018 {
  @Override
  protected void run() {
    List<Integer> changes = fullLoader.ml(Integer::parseInt);
    System.out.println(changes.stream().mapToInt(Integer::intValue).sum());
    System.out.println(findFirstRepeat(changes));
  }

  private int findFirstRepeat(List<Integer> changes) {
    Set<Integer> seen = new HashSet<>();
    int current = 0;
    seen.add(current);
    while (true) {
      for (int change : changes) {
        current += change;
        if (!seen.add(current)) {
          return current;
        }
      }
    }
  }
}
