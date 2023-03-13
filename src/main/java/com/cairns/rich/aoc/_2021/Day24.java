package com.cairns.rich.aoc._2021;

import java.util.Arrays;
import java.util.stream.Collectors;

class Day24 extends Base2021 {
  private static final Constants[] digitConstants = {
      new Constants(1, 11, 14, 1),
      new Constants(1, 13, 8, 1),
      new Constants(1, 11, 4, 6),
      new Constants(1, 10, 10, 2),
      new Constants(26, -3, 14, 3),
      new Constants(26, -4, 10, 2),
      new Constants(1, 12, 4, 2),
      new Constants(26, -8, 14, 3),
      new Constants(26, -3, 1, 2),
      new Constants(26, -12, 6, 1),
      new Constants(1, 14, 0, 1),
      new Constants(26, -6, 9, 1),
      new Constants(1, 11, 13, 1),
      new Constants(26, -12, 12, 1)
  };

  @Override
  protected void run() {
    verifyAndPrintResult(new int[] { 7, 4, 9, 2, 9, 9, 9, 5, 9, 9, 9, 3, 8, 9 });   // Largest
    verifyAndPrintResult(new int[] { 1, 1, 1, 1, 8, 1, 5, 1, 6, 3, 7, 1, 1, 2 });   // Smallest
  }

  private void verifyAndPrintResult(int[] ws) {
    int z = 0;
    for (int i = 0; i < ws.length; ++i) {
      z = getFinalZValue(digitConstants[i], ws[i], z);
    }
    String answer = Arrays.stream(ws).mapToObj(Integer::toString).collect(Collectors.joining());
    System.out.println(z + " <-- " + answer);
  }

  private int getFinalZValue(Constants constants, int w, int z) {
    int x = z % 26;
    z /= constants.a;
    x += constants.b;
    x = (x == w) ? 0 : 1;

    int y = (25 * x) + 1;
    z *= y;
    y = (w + constants.c) * x;
    z += y;

    return z;
  }

  private static class Constants {
    private final int a;
    private final int b;
    private final int c;
    private final int depth;

    private Constants(int a, int b, int c, int depth) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.depth = depth;
    }

    @Override
    public String toString() {
      return "a=" + a + ", b=" + b + ", c=" + c + ", depth=" + depth;
    }
  }
}
