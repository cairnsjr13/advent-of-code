package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day02 extends Base2015 {
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Pkg::new).stream().mapToInt(Pkg::getPaperRequired).sum();
  }

  @Override
  protected Object part2(Loader loader) {
    return loader.ml(Pkg::new).stream().mapToInt(Pkg::getRibbonRequired).sum();
  }

  private static class Pkg {
    private static final Pattern pattern = Pattern.compile("^(\\d+)x(\\d+)x(\\d+)$");

    private final int[] dims;

    private Pkg(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.dims = new int[] {
          Integer.parseInt(matcher.group(1)),
          Integer.parseInt(matcher.group(2)),
          Integer.parseInt(matcher.group(3))
      };
      Arrays.sort(dims);
    }

    private int getPaperRequired() {
      return 2 * (dims[0] * dims[1] + dims[0] * dims[2] + dims[1] * dims[2])
           + dims[0] * dims[1];
    }

    private int getRibbonRequired() {
      return (dims[0] * dims[1] * dims[2])
           + (2 * (dims[0] + dims[1]));
    }
  }
}
