package com.cairns.rich.aoc._2021;

import java.util.List;
import java.util.stream.IntStream;

class Day01 extends Base2021 {
  @Override
  protected void run() {
    List<Integer> inputs = fullLoader.ml(Integer::parseInt);
    System.out.println(general(inputs, 1));
    System.out.println(general(inputs, 3));
  }

  private long general(List<Integer> inputs, int windowSize) {
    return IntStream.range(0, inputs.size() - windowSize)
        .filter((i) -> inputs.get(i + windowSize) > inputs.get(i))
        .count();
  }
}
