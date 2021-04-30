package com.cairns.rich.aoc._2018;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;

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
    for (int i = 0; i < claims.size(); ++i) {
      Claim claim = claims.get(i);
      boolean hasConflict = false;
      for (int j = 0; j < claims.size(); ++j) {
        if (i != j) {
          Claim inspect = claims.get(j);
          if (claim.overlaps(inspect)) {
            hasConflict = true;
          }
        }
      }
      if (!hasConflict) {
        return claim;
      }
    }
    throw fail();
  }
  
  private static class Claim {
    private static final Pattern pattern = Pattern.compile("^#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)$");
    
    private final int id;
    private final Range<Integer> xRange;
    private final Range<Integer> yRange;
    
    private Claim(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.id = Integer.parseInt(matcher.group(1));
      int x = Integer.parseInt(matcher.group(2));
      int y = Integer.parseInt(matcher.group(3));
      this.xRange = Range.closedOpen(x, x + Integer.parseInt(matcher.group(4)));
      this.yRange = Range.closedOpen(y, y + Integer.parseInt(matcher.group(5)));
    }
    
    private boolean overlaps(Claim other) {
      return rangeOverlap(xRange, other.xRange)
          && rangeOverlap(yRange, other.yRange);
    }
    
    private static boolean rangeOverlap(Range<Integer> first, Range<Integer> second) {
      return first.contains(second.lowerEndpoint()) || second.contains(first.lowerEndpoint());
    }
  }
}
