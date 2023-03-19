package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.collect.TreeMultimap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

class Day10 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    return findStationAndVisibleAsts(loader, (station, visibleAsteriods) -> visibleAsteriods.size());
  }

  @Override
  protected Object part2(Loader2 loader) {
    return findStationAndVisibleAsts(loader, (station, visibleAsteroids) -> {
      TreeMultimap<String, Pair<ImmutablePoint, Double>> quadrantsToAsteroidsAndSlopes =
          TreeMultimap.create(Comparator.naturalOrder(), Comparator.comparing(Pair::getRight));
      visibleAsteroids.stream().forEach((asteroid) -> {
        double slope = computeSlope(station, asteroid);
        quadrantsToAsteroidsAndSlopes.put(computeQuadrant(station, asteroid, slope), Pair.of(asteroid, slope));
      });
      int index = visibleAsteroids.size() - quadrantsToAsteroidsAndSlopes.get("TL").size();
      Iterator<Pair<ImmutablePoint, Double>> itr = quadrantsToAsteroidsAndSlopes.get("TL").iterator();
      for (; index < 199; ++index) {
        itr.next();
      }
      ImmutablePoint explosion200 = itr.next().getLeft();
      return 100 * explosion200.x() + explosion200.y();
    });
  }

  private int findStationAndVisibleAsts(Loader2 loader, BiFunction<ImmutablePoint, Set<ImmutablePoint>, Integer> toAnswer) {
    Set<ImmutablePoint> asteroids = findAsteroids(loader.ml());
    ImmutablePoint station = null;
    Set<ImmutablePoint> visibleAsteroids = Set.of();
    for (ImmutablePoint candidate : asteroids) {
      Set<ImmutablePoint> visibleFrom =
          asteroids.stream().filter((check) -> isVisible(asteroids, candidate, check)).collect(Collectors.toSet());
      if (visibleAsteroids.size() < visibleFrom.size()) {
        station = candidate;
        visibleAsteroids = visibleFrom;
      }
    }
    return toAnswer.apply(station, visibleAsteroids);
  }

  private boolean isVisible(Set<ImmutablePoint> asteroids, ImmutablePoint candidate, ImmutablePoint check) {
    if (candidate == check) {
      return false;
    }
    double checkSlope = computeSlope(candidate, check);
    for (ImmutablePoint blocker : asteroids) {
      if ((candidate != blocker) && (check != blocker)) {
        double blockerSlope = computeSlope(candidate, blocker);
        if ((checkSlope == blockerSlope) && isBetween(candidate, check, blocker)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean isBetween(ImmutablePoint candidate, ImmutablePoint check, ImmutablePoint blocker) {
    double candidateXCmp = Math.signum(Integer.compare(candidate.x(), blocker.x()));
    double candidateYCmp = Math.signum(Integer.compare(candidate.y(), blocker.y()));
    return ((candidateXCmp != Math.signum(Integer.compare(check.x(), blocker.x()))) || (candidateXCmp == 0))
        && ((candidateYCmp != Math.signum(Integer.compare(check.y(), blocker.y()))) || (candidateYCmp == 0));
  }

  private double computeSlope(ImmutablePoint left, ImmutablePoint right) {
    return ((double) left.y() - right.y()) / ((double) left.x() - right.x());
  }

  private String computeQuadrant(ImmutablePoint station, ImmutablePoint asteroid, double slope) {
    if (Double.isInfinite(slope)) {
      return (0 < slope) ? "TR" : "BL";
    }
    else if (0 < slope) {
      return (Integer.compare(station.x(), asteroid.x()) <= 0) ? "BR" : "TL";
    }
    return (Integer.compare(station.y(), asteroid.y()) <= 0) ? "BL" : "TR";
  }

  private Set<ImmutablePoint> findAsteroids(List<String> map) {
    Set<ImmutablePoint> asteroids = new HashSet<>();
    for (int row = 0; row < map.size(); ++row) {
      for (int col = 0; col < map.get(0).length(); ++col) {
        if (map.get(row).charAt(col) == '#') {
          asteroids.add(new ImmutablePoint(col, row));
        }
      }
    }
    return asteroids;
  }
}
