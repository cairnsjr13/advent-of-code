package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day01 extends Base2020 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Integer> inputs = loader.ml(Integer::parseInt);
    Set<Long> seen = new HashSet<>();
    for (long input : inputs) {
      long need = 2020 - input;
      if (seen.contains(need)) {
        return input * need;
      }
      seen.add(input);
    }
    throw fail();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Integer> inputs = loader.ml(Integer::parseInt);
    for (int i = 0; i < inputs.size(); ++i) {
      int iVal = inputs.get(i);
      for (int j = i + 1; j < inputs.size(); ++j) {
        int jVal = inputs.get(j);
        for (int k = j + 1; k < inputs.size(); ++k) {
          int kVal = inputs.get(k);
          if (iVal + jVal + kVal == 2020) {
            return iVal * jVal * kVal;
          }
        }
      }
    }
    throw fail();
  }
}
