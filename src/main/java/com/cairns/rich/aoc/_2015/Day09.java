package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day09 extends Base2015 {

  @Override
  protected Object part1(Loader2 loader) {
    return extremeRoute(loader, Integer.MAX_VALUE, Math::min);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return extremeRoute(loader, Integer.MIN_VALUE, Math::max);
  }

  private int extremeRoute(Loader2 loader, int seed, IntBinaryOperator getExtreme) {
    List<Leg> legs = loader.ml(Leg::new);
    Table<String, String, Integer> routes = HashBasedTable.create();
    for (Leg leg : legs) {
      routes.put(leg.start, leg.end, leg.distance);
      routes.put(leg.end, leg.start, leg.distance);
    }
    List<String> dsts = new ArrayList<>(routes.rowKeySet());
    for (String dst : dsts) {
      routes.put("", dst, 0);
      routes.put(dst, "", 0);
    }
    return extremeRoute(seed, getExtreme, routes, dsts, "");
  }

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
