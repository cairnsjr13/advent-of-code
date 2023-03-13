package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day17 extends Base2021 {
  @Override
  protected void run() {
    TargetArea targetArea = new TargetArea(fullLoader.sl());

    int highestMaxHeight = 0;
    int numValidVelocities = 0;
    MutablePoint velocity = new MutablePoint(0, 0);
    for (int vx = 1; vx <= 1000; ++vx) {
      for (int vy = -1000; vy <= 1000; ++vy) {
        velocity.x(vx);
        velocity.y(vy);
        int maxHeight = findMaxHeight(targetArea, velocity);
        if (maxHeight >= 0) {
          highestMaxHeight = Math.max(highestMaxHeight, maxHeight);
          ++numValidVelocities;
        }
      }
    }

    System.out.println(highestMaxHeight);
    System.out.println(numValidVelocities);
  }

  private int findMaxHeight(TargetArea targetArea, MutablePoint velocity) {
    int maxHeight = 0;
    MutablePoint location = new MutablePoint(0, 0);
    while (!targetArea.isPast(location)) {
      location.mutateX(velocity.x());
      location.mutateY(velocity.y());
      velocity.mutateX(-Integer.signum(velocity.x()));
      velocity.mutateY(-1);
      maxHeight = Math.max(maxHeight, location.y());
      if (targetArea.contains(location)) {
        return maxHeight;
      }
    }
    return -1;
  }

  private static class TargetArea {
    private static final Pattern pattern = Pattern.compile("^target area: x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)$");

    private final ImmutablePoint topLeft;
    private final ImmutablePoint bottomRight;

    private TargetArea(String input) {
      Matcher matcher = matcher(pattern, input);
      this.topLeft = new ImmutablePoint(num(matcher, 1), num(matcher, 4));
      this.bottomRight = new ImmutablePoint(num(matcher, 2), num(matcher, 3));
    }

    private boolean isPast(MutablePoint location) {
      return (bottomRight.x() < location.x()) || (location.y() < bottomRight.y());
    }

    private boolean contains(MutablePoint location) {
      return (topLeft.x() <= location.x()) && (location.x() <= bottomRight.x())
          && (bottomRight.y() <= location.y()) && (location.y() <= topLeft.y());
    }
  }
}
