package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.HashMap;
import java.util.Map;

/**
 * An infinite spiral grid is used for storage.  We need to compute distances and sums.
 */
class Day03 extends Base2017 {
  /**
   * Computes the manhattan distance from the middle (start) of our input.
   */
  @Override
  protected Object part1(Loader loader) {
    int target = Integer.parseInt(loader.sl());
    ImmutablePoint location = computeLocationOfIndex(target);
    return Math.abs(location.x()) + Math.abs(location.y());
  }

  /**
   * A process walks the spiral and inserts numbers based on adjacent cells.
   * This will compute the first value that is larger than our input.
   * We do this by caching the sum of neighbors for each cell as we walk the spiral.
   */
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

  /**
   * Computes the {@link ImmutablePoint} that the given target index will lie at.  Instead of walking the entire spiral,
   * we jump to the outside edge of the spiral that the given target is on.  The outer ring is started by  moving to the
   * right from the last position in the previous ring and turning left when we are out of spots on that edge.
   */
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

  /**
   * Computes the size of an edge that would yield a box such that the area of a box one larger would be above or at our target.
   */
  private int computeEdge(int target) {
    for (int sizeOfEdge = 1; true; sizeOfEdge += 2) {
      int sizeOfNextBox = (sizeOfEdge + 2) * (sizeOfEdge + 2);
      if (sizeOfNextBox >= target) {
        return sizeOfEdge;
      }
    }
  }

  /**
   * Computes the sum of the neighbors of the given location.  A location has 8 neighbors, which
   * can be enumerated by looking at each {@link RelDir} as well as the left turn cell next to it.
   */
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
