package com.cairns.rich.aoc._2016;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day15 extends Base2016 {
  @Override
  protected void run() {
    List<DiscDesc> discDescs = fullLoader.ml(DiscDesc::new);
    System.out.println(getTWhenFalls(discDescs));
    
    discDescs.add(new DiscDesc(11, 0));
    System.out.println(getTWhenFalls(discDescs));
  }
  
  private int getTWhenFalls(List<DiscDesc> discDescs) {
    for (int t = 0; true; ++t) {
      if (doesFall(discDescs, t)) {
        return t;
      }
    }
  }
  
  private boolean doesFall(List<DiscDesc> discDescs, int t) {
    for (int disc = 0; disc < discDescs.size(); ++disc) {
      DiscDesc discDesc = discDescs.get(disc);
      int discPosition = (t + (disc + 1) + discDesc.startPosition) % discDesc.numPositions;
      if (discPosition != 0) {
        return false;
      }
    }
    return true;
  }
  
  private static class DiscDesc {
    private static final Pattern pattern =
        Pattern.compile("^Disc #\\d has (\\d+) positions; at time=0, it is at position (\\d+)\\.$");
    
    private final int numPositions;
    private final int startPosition;
    
    private DiscDesc(int numPositions, int startPosition) {
      this.numPositions = numPositions;
      this.startPosition = startPosition;
    }
    
    private DiscDesc(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.numPositions = Integer.parseInt(matcher.group(1));
      this.startPosition = Integer.parseInt(matcher.group(2));
    }
  }
}
