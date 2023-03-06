package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

class Day03 extends Base2019 {
  private static final Map<Character, RelDir> dirLookup = EnumUtils.getLookup(RelDir.class);

  @Override
  protected void run() {
    List<List<Pair<RelDir, Integer>>> routes = fullLoader.ml(this::parse);
    List<ImmutablePoint> intersections = new ArrayList<>();
    Table<Integer, ImmutablePoint, Integer> stepsToForRoute = HashBasedTable.create();
    runRoutes(routes, intersections, stepsToForRoute);
    System.out.println(getAnswer(intersections, this::manhattan));
    System.out.println(getAnswer(intersections, (i) -> totalStepsTo(stepsToForRoute, i)));
  }

  private void runRoutes(
      List<List<Pair<RelDir, Integer>>> routes,
      List<ImmutablePoint> intersections,
      Table<Integer, ImmutablePoint, Integer> stepsToForRoute
  ) {
    for (int routeI = 0; routeI < routes.size(); ++routeI) {
      List<Pair<RelDir, Integer>> route = routes.get(routeI);
      int numStepsForRoute = 0;
      ImmutablePoint cur = new ImmutablePoint(0, 0);
      for (Pair<RelDir, Integer> move : route) {
        RelDir dir = move.getLeft();
        int numMoves = move.getRight();
        for (int i = 0; i < numMoves; ++i) {
          cur = cur.move(dir);
          ++numStepsForRoute;
          if (!stepsToForRoute.contains(routeI, cur)) {
            stepsToForRoute.put(routeI, cur, numStepsForRoute);
          }
          if (stepsToForRoute.column(cur).size() > 1) {
            intersections.add(cur);
          }
        }
      }
    }
  }

  private int getAnswer(List<ImmutablePoint> intersections, Function<ImmutablePoint, Integer> toScore) {
    return toScore.apply(getMin(intersections, toScore));
  }

  private int manhattan(ImmutablePoint location) {
    return Math.abs(location.x()) + Math.abs(location.y());
  }

  private int totalStepsTo(Table<Integer, ImmutablePoint, Integer> stepsToForRoute, ImmutablePoint location) {
    return stepsToForRoute.column(location).values().stream().mapToInt(Integer::intValue).sum();
  }

  private List<Pair<RelDir, Integer>> parse(String spec) {
    return Arrays.stream(spec.split(","))
        .map((p) -> Pair.of(dirLookup.get(p.charAt(0)), Integer.parseInt(p.substring(1))))
        .collect(Collectors.toList());
  }
}
