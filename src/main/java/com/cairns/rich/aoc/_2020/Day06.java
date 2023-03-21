package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.List;

class Day06 extends Base2020 {
  @Override
  protected Object part1(Loader loader) {
    return loader.gDelim("", Group::new).stream().mapToLong(Group::numAny).sum();
  }

  @Override
  protected Object part2(Loader loader) {
    return loader.gDelim("", Group::new).stream().mapToLong(Group::numAll).sum();
  }

  private static class Group {
    private final int numPeople;
    private final int[] qs = new int[26];

    private Group(List<String> lines) {
      this.numPeople = lines.size();
      lines.forEach((line) -> line.chars().forEach((ch) -> ++qs[ch - 'a']));
    }

    private long numAny() {
      return Arrays.stream(qs).filter((q) -> q > 0).count();
    }

    private long numAll() {
      return Arrays.stream(qs).filter((q) -> q == numPeople).count();
    }
  }
}
