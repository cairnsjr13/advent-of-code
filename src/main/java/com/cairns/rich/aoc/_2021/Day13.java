package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day13 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return getPointsAfterFolds(loader, (folds) -> 1).size();
  }

  @Override
  protected Object part2(Loader2 loader) {
    Set<ImmutablePoint> points = getPointsAfterFolds(loader, List::size);
    StringBuilder out = new StringBuilder();
    int minR = getMin(points, ImmutablePoint::y).y();
    int maxR = getMax(points, ImmutablePoint::y).y();
    int minC = getMin(points, ImmutablePoint::x).x();
    int maxC = getMax(points, ImmutablePoint::x).x();
    out.append("\n");
    for (int r = minR; r <= maxR; ++r) {
      for (int c = minC; c <= maxC; ++c) {
        out.append((points.contains(new ImmutablePoint(c, r)) ? 0x2588 : ' ')); // TODO: centralize dark pixel
      }
      out.append("\n");
    }
    return out;
  }

  private Set<ImmutablePoint> getPointsAfterFolds(Loader2 loader, ToIntFunction<List<Fold>> numFolds) {
    List<String> lines = loader.ml();   // TODO: multi group
    int breakIndex = lines.indexOf("");
    Set<ImmutablePoint> points = lines.subList(0, breakIndex).stream().map(this::toPoint).collect(Collectors.toSet());
    List<Fold> folds = lines.subList(breakIndex + 1, lines.size()).stream().map(Fold::new).collect(Collectors.toList());
    folds = folds.subList(0, numFolds.applyAsInt(folds));
    for (Fold fold : folds) {
      Set<ImmutablePoint> after = new HashSet<>();
      if ("y".equals(fold.fold)) {
        for (ImmutablePoint point : points) {
          after.add((point.y() < fold.mag) ? point : new ImmutablePoint(point.x(), 2 * fold.mag - point.y()));
        }
      }
      else if ("x".equals(fold.fold)) {
        for (ImmutablePoint point : points) {
          after.add((point.x() < fold.mag) ? point : new ImmutablePoint(2 * fold.mag - point.x(), point.y()));
        }
      }
      else {
        throw fail(fold);
      }
      points = after;
    }
    return points;
  }

  private ImmutablePoint toPoint(String line) {
    String[] pieces = line.split(",");
    return new ImmutablePoint(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]));
  }

  private static class Fold {
    private static final Pattern pattern = Pattern.compile("^fold along (x|y)=(\\d+)$");

    private final String fold;
    private final int mag;

    private Fold(String line) {
      Matcher matcher = matcher(pattern, line);
      this.fold = matcher.group(1);
      this.mag = num(matcher, 2);
    }
  }
}
