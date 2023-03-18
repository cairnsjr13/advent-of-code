package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day15 extends Base2016 {
  @Override
  protected Object part1(Loader2 loader) {
    return getTWhenFalls(loader.ml(DiscDesc::new));
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<DiscDesc> discDescs = loader.ml(DiscDesc::new);
    discDescs.add(new DiscDesc(11, 0));
    return getTWhenFalls(discDescs);
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
      this.numPositions = num(matcher, 1);
      this.startPosition = num(matcher, 2);
    }
  }
}
