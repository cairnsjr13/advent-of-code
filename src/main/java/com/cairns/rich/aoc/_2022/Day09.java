package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day09 extends Base2022 {
  @Override
  protected Object part1(Loader loader) {
    return numTailVists(loader, 2);
  }

  @Override
  protected Object part2(Loader loader) {
    return numTailVists(loader, 10);
  }

  private int numTailVists(Loader loader, int numKnots) {
    Set<ImmutablePoint> tailVisited = new HashSet<>();
    List<ImmutablePoint> knots =
        IntStream.range(0, numKnots).mapToObj((i) -> ImmutablePoint.origin).collect(Collectors.toList());
    tailVisited.add(knots.get(knots.size() - 1));
    for (Inst inst : loader.ml(Inst::new)) {
      for (int i = 0; i < inst.magnitude; ++i) {
        knots.set(0, knots.get(0).move(inst.dir));
        for (int k = 1; k < knots.size(); ++k) {
          knots.set(k, drag(knots.get(k - 1), knots.get(k)));
        }
        tailVisited.add(knots.get(knots.size() - 1));
      }
    }
    return tailVisited.size();
  }

  private ImmutablePoint drag(ImmutablePoint leader, ImmutablePoint follower) {
    int dx = follower.x() - leader.x();
    int dy = follower.y() - leader.y();
    if (1 < Math.max(Math.abs(dx), Math.abs(dy))) {
      if (dx != 0) {
        follower = follower.move((dx < 0) ? RelDir.Right : RelDir.Left);
      }
      if (dy != 0) {
        follower = follower.move((dy < 0) ? RelDir.Up : RelDir.Down);
      }
    }
    return follower;
  }

  private static class Inst {
    private static final Map<Character, RelDir> dirLookup = EnumUtils.getLookup(RelDir.class);

    private final RelDir dir;
    private final int magnitude;

    private Inst(String line) {
      this.dir = dirLookup.get(line.charAt(0));
      this.magnitude = Integer.parseInt(line.substring(2));
    }
  }
}
