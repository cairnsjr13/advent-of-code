package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

class Day03 extends Base2019 {
  private static final Map<Character, RelDir> dirLookup = EnumUtils.getLookup(RelDir.class);

  @Override
  protected Object part1(Loader2 loader) {
    return getAnswer(
        loader,
        (stepsToForRoute, location) -> Math.abs(location.x()) + Math.abs(location.y())
    );
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getAnswer(
        loader,
        (stepsToForRoute, location) -> stepsToForRoute.column(location).values().stream().mapToInt(Integer::intValue).sum()
    );
  }

  private int getAnswer(Loader2 loader, BiFunction<Table<Integer, ImmutablePoint, Integer>, ImmutablePoint, Integer> toScore) {
    List<List<Pair<RelDir, Integer>>> routes = loader.ml(this::parse);
    List<ImmutablePoint> intersections = new ArrayList<>();
    Table<Integer, ImmutablePoint, Integer> stepsToForRoute = HashBasedTable.create();
    runRoutes(routes, intersections, stepsToForRoute);
    return toScore.apply(stepsToForRoute, getMin(intersections, (location) -> toScore.apply(stepsToForRoute, location)));
  }

  private void runRoutes(
      List<List<Pair<RelDir, Integer>>> routes,
      List<ImmutablePoint> intersections,
      Table<Integer, ImmutablePoint, Integer> stepsToForRoute
  ) {
    for (int routeI = 0; routeI < routes.size(); ++routeI) {
      List<Pair<RelDir, Integer>> route = routes.get(routeI);
      int numStepsForRoute = 0;
      ImmutablePoint cur = ImmutablePoint.origin;
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

  private List<Pair<RelDir, Integer>> parse(String spec) {
    return Arrays.stream(spec.split(","))
        .map((p) -> Pair.of(dirLookup.get(p.charAt(0)), Integer.parseInt(p.substring(1))))
        .collect(Collectors.toList());
  }
}
