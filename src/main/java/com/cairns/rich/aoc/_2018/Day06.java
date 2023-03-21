package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

class Day06 extends Base2018 {
  @Override
  protected Object part1(Loader loader) {
    State state = new State(loader.ml(this::parse));
    Multiset<ImmutablePoint> closestCounts = HashMultiset.create();
    Set<ImmutablePoint> infinites = new HashSet<>();
    for (int x = state.minX; x <= state.maxX; ++x) {
      for (int y = state.minY; y <= state.maxY; ++y) {
        Multimap<Integer, ImmutablePoint> distToPoints = HashMultimap.create();
        for (ImmutablePoint point : state.points) {
          distToPoints.put(manhattan(point, x, y), point);
        }
        int minDist = getMin(distToPoints.keySet(), Function.identity());
        Collection<ImmutablePoint> minPoints = distToPoints.get(minDist);
        if (minPoints.size() == 1) {
          ImmutablePoint winningPoint = minPoints.iterator().next();
          closestCounts.add(winningPoint);
          if ((x == state.minX) || (x == state.maxX) || (y == state.minY) || (y == state.maxY)) {
            infinites.add(winningPoint);
          }
        }
      }
    }
    closestCounts.removeAll(infinites);
    return closestCounts.count(getMax(closestCounts.elementSet(), closestCounts::count));
  }

  @Override
  protected Object part2(Loader loader) {
    State state = new State(loader.ml(this::parse));
    int maxExclusive = 10_000;
    int size = 0;
    for (int x = state.minX; x <= state.maxX; ++x) {
      for (int y = state.minY; y <= state.maxY; ++y) {
        int sum = 0;
        for (ImmutablePoint point : state.points) {
          sum += manhattan(point, x, y);
        }
        if (sum < maxExclusive) {
          ++size;
        }
      }
    }
    return size;
  }

  private int manhattan(ImmutablePoint point, int x, int y) {
    return Math.abs(point.x() - x) + Math.abs(point.y() - y);
  }

  private ImmutablePoint parse(String spec) {
    String[] parts = spec.split(", +");
    return new ImmutablePoint(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
  }

  private static class State {
    private final List<ImmutablePoint> points;
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    private State(List<ImmutablePoint> points) {
      this.points = points;
      this.minX = getMin(points, ImmutablePoint::x).x();
      this.minY = getMin(points, ImmutablePoint::y).y();
      this.maxX = getMax(points, ImmutablePoint::x).x();
      this.maxY = getMax(points, ImmutablePoint::y).y();
    }
  }
}
