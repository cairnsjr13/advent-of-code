package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.List;

/**
 * We need to cross a firewall with a very peculiar security system.  Each "layer" has a scanner which moves up and down
 * the layer (which has a depth), one step at a time, turning around when it reaches either end.  In order to cross, we
 * must move at one layer per step across the top of each layer.
 */
class Day13 extends Base2017 {
  /**
   * Detects the severity of getting caught if we launch a packet at t=0.  The severity is the sum of each layer's "caught"
   * severity.  This can be calculated as 0 if uncaught at that layer or the depth multiplied by the layer position if caught.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Scanner::new).stream().filter((s) -> s.atTopAtTime(s.layer)).mapToInt((s) -> s.depth * s.layer).sum();
  }

  /**
   * Calculates the first time (starting at 0) in which we can launch a packet and get all the way across the firewall without
   * being caught.  We do a simple linear increasing search for a time that works.  We can figure this out by checking if any of
   * the scanners will be at the top of their layer given the time it takes to reach that layer (the delay + its layer index).
   */
  @Override
  protected Object part2(Loader loader) {
    List<Scanner> scanners = loader.ml(Scanner::new);
    for (int d = 0; true; ++d) {
      int delay = d;    // TODO: a later version of java should have better "effectively final" detection
      if (!scanners.stream().anyMatch((s) -> s.atTopAtTime(delay + s.layer))) {
        return delay;
      }
    }
  }

  /**
   * Descriptor class that holds the definition of a layer/scanner.
   * Also capable of computing if it is a threat at any given time.
   */
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

    /**
     * Returns true if this scanner is at the top of its layer at the given time.  Indicating a packet will be "caught".
     */
    private boolean atTopAtTime(int t) {
      return t % (2 * (depth - 1)) == 0;
    }
  }
}
