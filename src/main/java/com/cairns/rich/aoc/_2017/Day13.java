package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.List;

class Day13 extends Base2017 {
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Scanner::new).stream().filter((s) -> s.atTopAtTime(s.layer)).mapToInt((s) -> s.depth * s.layer).sum();
  }

  @Override
  protected Object part2(Loader loader) {
    List<Scanner> scanners = loader.ml(Scanner::new);
    for (int d = 0; true; ++d) {
      int delay = d;
      if (!scanners.stream().anyMatch((s) -> s.atTopAtTime(delay + s.layer))) {
        return delay;
      }
    }
  }

  private static class Scanner {
    private final int layer;
    private final int depth;

    private Scanner(String spec) {
      String[] parts = spec.split(": +");
      this.layer = Integer.parseInt(parts[0]);
      this.depth = Integer.parseInt(parts[1]);
      if (depth <= 1) {
        throw fail(layer + ", " + depth);
      }
    }

    private boolean atTopAtTime(int t) {
      return t % (2 * (depth - 1)) == 0;
    }
  }
}
