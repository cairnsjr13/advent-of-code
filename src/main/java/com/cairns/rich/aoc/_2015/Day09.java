package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Santa needs to be a traveling presentsman.  Need to find good/bad routes.
 */
class Day09 extends Base2015 {
  /**
   * Finds the shortest full path.
   */
  @Override
  protected Object part1(Loader loader) {
    return extremeRoute(loader, Integer.MAX_VALUE, Math::min);
  }

  /**
   * Finds the longest full path.
   */
  @Override
  protected Object part2(Loader loader) {
    return extremeRoute(loader, Integer.MIN_VALUE, Math::max);
  }

  /**
   * Finds the "best" route that is determined by the given seed and comparator (min for shortest,
   * max for longest).  To do this we create a new imaginary route from a new location "" to every
   * destination with cost 0.  At this point we can start an ordering search starting from the new
   * location "".  This will take care of the "start from anywhere" part of the puzzle.
   */
  private int extremeRoute(Loader loader, int seed, IntBinaryOperator getExtreme) {
    List<Leg> legs = loader.ml(Leg::new);
    Table<String, String, Integer> routes = HashBasedTable.create();
    for (Leg leg : legs) {
      routes.put(leg.start, leg.end, leg.distance);
      routes.put(leg.end, leg.start, leg.distance);
    }
    List<String> dsts = new ArrayList<>(routes.rowKeySet());
    dsts.forEach((dst) -> routes.put("", dst, 0));
    return extremeRoute(seed, getExtreme, routes, dsts, "");
  }

  /**
   * Recursive ordering search to find the extreme path specified by the given seed (the worst value) and the given
   * extreme operator (min/max).  We do this by trying each destination left for the next step and then recursing.
   */
  private int extremeRoute(
      int seed,
      IntBinaryOperator getExtreme,
      Table<String, String, Integer> routes,
      List<String> dstsLeft,
      String current
  ) {
    if (dstsLeft.size() == 1) {
      return routes.get(current, dstsLeft.get(0));
    }
    int extreme = seed;
    for (int i = 0; i < dstsLeft.size(); ++i) {
      String nextHop = dstsLeft.remove(i);
      int path = routes.get(current, nextHop) + extremeRoute(seed, getExtreme, routes, dstsLeft, nextHop);
      extreme = getExtreme.applyAsInt(extreme, path);
      dstsLeft.add(i, nextHop);
    }
    return extreme;
  }

  /**
   * Input class that describes a route (start/end) and the distance it takes.
   */
  private static class Leg {
    private static final Pattern pattern = Pattern.compile("^(.+) to (.+) = (\\d+)$");

    private final String start;
    private final String end;
    private final int distance;

    private Leg(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.start = matcher.group(1);
      this.end = matcher.group(2);
      this.distance = Integer.parseInt(matcher.group(3));
    }
  }
}
