package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.stream.IntStream;

class Day01 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return general(loader, 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return general(loader, 3);
  }

  private long general(Loader2 loader, int windowSize) {
    List<Integer> inputs = loader.ml(Integer::parseInt);
    return IntStream.range(0, inputs.size() - windowSize)
        .filter((i) -> inputs.get(i + windowSize) > inputs.get(i))
        .count();
  }
}
