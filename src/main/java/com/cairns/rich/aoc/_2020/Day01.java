package com.cairns.rich.aoc._2020;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day01 extends Base2020 {
  @Override
  protected void run() {
    List<Integer> inputs = fullLoader.ml(Integer::parseInt);
    System.out.println(findTwoExpenses(inputs));
    System.out.println(findThreeExpenses(inputs));
  }

  private long findTwoExpenses(List<Integer> inputs) {
    Set<Long> seen = new HashSet<>();
    for (long input : inputs) {
      long need = 2020 - input;
      if (seen.contains(need)) {
        System.out.print(input + " x " + need + " = ");
        return input * need;
      }
      seen.add(input);
    }
    throw fail();
  }

  private long findThreeExpenses(List<Integer> inputs) {
    for (int i = 0; i < inputs.size(); ++i) {
      int iVal = inputs.get(i);
      for (int j = i + 1; j < inputs.size(); ++j) {
        int jVal = inputs.get(j);
        for (int k = j + 1; k < inputs.size(); ++k) {
          int kVal = inputs.get(k);
          if (iVal + jVal + kVal == 2020) {
            System.out.print(iVal + " x " + jVal + " x " + kVal + " = ");
            return iVal * jVal * kVal;
          }
        }
      }
    }
    throw fail();
  }
}
