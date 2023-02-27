package com.cairns.rich.aoc._2018;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day03 extends Base2018 {
  @Override
  protected void run() {
    List<Claim> claims = fullLoader.ml(Claim::new);
    System.out.println(getMultiClaims(claims));
    System.out.println(getClearClaim(claims).id);
  }

  private int getMultiClaims(List<Claim> claims) {
    int multiClaims = 0;
    Table<Integer, Integer, Boolean> claimed = HashBasedTable.create();
    for (Claim claim : claims) {
      for (int x = claim.xRange.lowerEndpoint(); x < claim.xRange.upperEndpoint(); ++x) {
        for (int y = claim.yRange.lowerEndpoint(); y < claim.yRange.upperEndpoint(); ++y) {
          if (!claimed.contains(x, y)) {
            claimed.put(x, y, true);
          }
          else if (claimed.get(x, y)) {
            ++multiClaims;
            claimed.put(x, y, false);
          }
        }
      }
    }
    return multiClaims;
  }

  private Claim getClearClaim(List<Claim> claims) {
    Predicate<Claim> isClear = (inspect) -> !claims.stream().filter((c) -> c != inspect).anyMatch(inspect::overlaps);
    return claims.stream().filter(isClear).findFirst().get();
  }

  private static class Claim {
    private static final Pattern pattern = Pattern.compile("^#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)$");

    private final int id;
    private final Range<Integer> xRange;
    private final Range<Integer> yRange;

    private Claim(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.id = num(matcher, 1);
      int x = num(matcher, 2);
      int y = num(matcher, 3);
      this.xRange = Range.closedOpen(x, x + num(matcher, 4));
      this.yRange = Range.closedOpen(y, y + num(matcher, 5));
    }

    private boolean overlaps(Claim other) {
      return xRange.isConnected(other.xRange) && yRange.isConnected(other.yRange);
    }
  }
}
