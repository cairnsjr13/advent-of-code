package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.function.ToLongFunction;

class Day03 extends Base2020 {
  @Override
  protected Object part1(Loader2 loader) {
    return getAnswer(loader, (trees) -> trees.get(1));
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getAnswer(loader, (trees) -> trees.stream().reduce(1L, Math::multiplyExact));
  }

  private long getAnswer(Loader2 loader, ToLongFunction<List<Long>> toAnswer) {
    List<String> lines = loader.ml();
    return toAnswer.applyAsLong(List.of(
        countTrees(lines, 1, 1),
        countTrees(lines, 1, 3),
        countTrees(lines, 1, 5),
        countTrees(lines, 1, 7),
        countTrees(lines, 2, 1)
    ));
  }

  private long countTrees(List<String> inputs, int rowMove, int colMove) {
    int row = 0;
    int col = 0;
    long trees = 0;
    while (row < inputs.size()) {
      if (inputs.get(row).charAt(col) == '#') {
        ++trees;
      }
      row += rowMove;
      col = (col + colMove) % inputs.get(0).length();
    }
    return trees;
  }
}
