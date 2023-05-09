package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The elves are wrapping presents.  We need to figure out how much paper and ribbon they need.
 */
class Day02 extends Base2015 {
  /**
   * We need to figure out the total amount of paper needed, which is just the sum of each package.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Pkg::new).stream().mapToInt(Pkg::getPaperRequired).sum();
  }

  /**
   * We need to figure out the total amount of ribbon needed, which is just the sum of each package.
   */
  @Override
  protected Object part2(Loader loader) {
    return loader.ml(Pkg::new).stream().mapToInt(Pkg::getRibbonRequired).sum();
  }

  /**
   * Input class that specifies the (sorted) dimensions of a package.
   */
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

    /**
     * Paper required is the total surface area plus an extra bit for the smallest side.
     *
     * @implNote relies on {@link #dims} being sorted.
     */
    private int getPaperRequired() {
      return 2 * (dims[0] * dims[1] + dims[0] * dims[2] + dims[1] * dims[2])
           + dims[0] * dims[1];
    }

    /**
     * The ribbon required for a present is the sum of two pieces
     *   1) the perimeter of the smallest face
     *   2) the bow - which is the cubic volume
     *
     * @implNote relies on {@link #dims} being sorted.
     */
    private int getRibbonRequired() {
      return (dims[0] * dims[1] * dims[2])
           + (2 * (dims[0] + dims[1]));
    }
  }
}
