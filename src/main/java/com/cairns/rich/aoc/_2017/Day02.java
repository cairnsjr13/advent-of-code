package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

class Day02 extends Base2017 {
  @Override
  protected void run() {
    List<List<Integer>> rows = fullLoader.ml(this::parse);
    System.out.println(computeResult(rows, this::computeChecksumResultForRow));
    System.out.println(computeResult(rows, this::computeEvenlyDivisibleResultForRow));
  }

  private int computeResult(List<List<Integer>> rows, ToIntFunction<List<Integer>> toResult) {
    return rows.stream().mapToInt(toResult).sum();
  }

  private int computeChecksumResultForRow(List<Integer> row) {
    return row.stream().mapToInt(Integer::intValue).max().getAsInt()
         - row.stream().mapToInt(Integer::intValue).min().getAsInt();
  }

  private int computeEvenlyDivisibleResultForRow(List<Integer> row) {
    for (int left : row) {
      for (int right : row) {
        if ((left != right) && (left % right == 0)) {
          return left / right;
        }
      }
    }
    throw fail(row);
  }

  private List<Integer> parse(String spec) {
    return Arrays.stream(spec.split(" +")).map(Integer::parseInt).collect(Collectors.toList());
  }
}
