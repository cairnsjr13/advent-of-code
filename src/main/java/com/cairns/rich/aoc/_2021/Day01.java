package com.cairns.rich.aoc._2021;

import java.util.List;

public class Day01 extends Base2021 {
  @Override
  protected void run() {
    List<Integer> inputs = fullLoader.ml(Integer::parseInt);
    System.out.println(general(inputs, 1));
    System.out.println(general(inputs, 3));
  }
  
  private int general(List<Integer> inputs, int windowSize) {
    int numIncreases = 0;
    for (int i = 0; i < inputs.size() - windowSize; ++i) {
      if (inputs.get(i + windowSize) > inputs.get(i)) {
        ++numIncreases;
      }
    }
    return numIncreases;
  }
}
