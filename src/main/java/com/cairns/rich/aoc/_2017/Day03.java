package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.HashMap;
import java.util.Map;

class Day03 extends Base2017 {
  @Override
  protected Object part1(Loader loader) {
    int target = Integer.parseInt(loader.sl());
    ImmutablePoint location = computeLocationOfIndex(target);
    return Math.abs(location.x()) + Math.abs(location.y());
  }

  @Override
  protected Object part2(Loader loader) {
    int target = Integer.parseInt(loader.sl());
    Map<ImmutablePoint, Integer> cache = new HashMap<>();
    ImmutablePoint location = ImmutablePoint.origin;
    computeSumOfNeighbors(cache, location);
    cache.put(location, 1);
    for (int i = 2; true; ++i) {
      location = computeLocationOfIndex(i);
      int sumOfNeighbors = computeSumOfNeighbors(cache, location);
      if (sumOfNeighbors > target) {
        return sumOfNeighbors;
      }
      cache.put(location, sumOfNeighbors);
    }
  }

  private ImmutablePoint computeLocationOfIndex(int target) {
    int sizeOfEdge = computeEdge(target);
    int numsLeft = target - (sizeOfEdge * sizeOfEdge);
    ImmutablePoint location = new ImmutablePoint(sizeOfEdge / 2, -sizeOfEdge / 2);
    if (numsLeft > 0) {
      location = location.move(RelDir.Right);
      --numsLeft;
      RelDir dir = RelDir.Up;
      int numsInDirectionLeft = sizeOfEdge;
      while (numsLeft > 0) {
        location = location.move(dir);
        --numsLeft;
        --numsInDirectionLeft;
        if (numsInDirectionLeft == 0) {
          dir = dir.turnLeft();
          numsInDirectionLeft = sizeOfEdge + 1;
        }
      }
    }
    return location;
  }

  private int computeEdge(int target) {
    for (int sizeOfEdge = 1; true; sizeOfEdge += 2) {
      int sizeOfNextBox = (sizeOfEdge + 2) * (sizeOfEdge + 2);
      if (sizeOfNextBox >= target) {
        return sizeOfEdge;
      }
    }
  }

  private int computeSumOfNeighbors(Map<ImmutablePoint, Integer> cache, ImmutablePoint location) {
    int sum = 0;
    for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
      ImmutablePoint neighbor = location.move(dir);
      ImmutablePoint compliment = neighbor.move(dir.turnLeft());
      sum += cache.getOrDefault(neighbor, 0)
           + cache.getOrDefault(compliment, 0);
    }
    return sum;
  }
}
