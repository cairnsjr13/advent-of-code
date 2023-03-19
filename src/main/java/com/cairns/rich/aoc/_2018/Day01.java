package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day01 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    return loader.ml(Integer::parseInt).stream().mapToInt(Integer::intValue).sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Integer> changes = loader.ml(Integer::parseInt);
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
