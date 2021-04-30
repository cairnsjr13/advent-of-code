package com.cairns.rich.aoc._2020;

import java.util.List;

class Day03 extends Base2020 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    List<Long> trees = List.of(
        countTrees(lines, 1, 1),
        countTrees(lines, 1, 3),
        countTrees(lines, 1, 5),
        countTrees(lines, 1, 7),
        countTrees(lines, 2, 1)
    ); 
    System.out.println(trees);
    System.out.println(trees.stream().reduce(1L, Math::multiplyExact));
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
