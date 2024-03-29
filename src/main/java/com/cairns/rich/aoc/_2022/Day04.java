package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.Range;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day04 extends Base2022 {
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(ElfPair::new).stream().filter(ElfPair::hasRedundant).count();
  }

  @Override
  protected Object part2(Loader loader) {
    return loader.ml(ElfPair::new).stream().filter(ElfPair::hasOverlap).count();
  }

  private static class ElfPair {
    private static final Pattern pattern = Pattern.compile("^(\\d+)-(\\d+),(\\d+)-(\\d+)$");

    private final Range<Integer> first;
    private final Range<Integer> second;

    private ElfPair(String line) {
      Matcher matcher = matcher(pattern, line);
      this.first = Range.closed(num(matcher, 1), num(matcher, 2));
      this.second = Range.closed(num(matcher, 3), num(matcher, 4));
    }

    private boolean hasOverlap() {
      return first.isConnected(second);
    }

    private boolean hasRedundant() {
      return ((first.lowerEndpoint() <= second.lowerEndpoint()) && (second.upperEndpoint() <= first.upperEndpoint()))
          || ((second.lowerEndpoint() <= first.lowerEndpoint()) && (first.upperEndpoint() <= second.upperEndpoint()));
    }
  }
}
