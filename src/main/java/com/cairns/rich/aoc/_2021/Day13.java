package com.cairns.rich.aoc._2021;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.cairns.rich.aoc.grid.ImmutablePoint;

public class Day13 extends Base2021 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    int breakIndex = lines.indexOf("");
    Set<ImmutablePoint> points = lines.subList(0, breakIndex).stream().map(this::toPoint).collect(Collectors.toSet());
    List<Fold> folds = lines.subList(breakIndex + 1, lines.size()).stream().map(Fold::new).collect(Collectors.toList());
    
    for (int i = 0; i < folds.size(); ++i) {
      Fold fold = folds.get(i);
      Set<ImmutablePoint> after = new HashSet<>();
      if ("y".equals(fold.fold)) {
        for (ImmutablePoint point : points) {
          if (point.y() < fold.mag) {
            after.add(point);
          }
          else {
            after.add(new ImmutablePoint(point.x(), 2 * fold.mag - point.y()));
          }
        }
      }
      else if ("x".equals(fold.fold)) {
        for (ImmutablePoint point : points) {
          if (point.x() < fold.mag) {
            after.add(point);
          }
          else {
            after.add(new ImmutablePoint(2 * fold.mag - point.x(), point.y()));
          }
        }
      }
      else {
        throw fail(fold);
      }
      if (i == 0) {
        System.out.println(after.size());
      }
      points = after;
    }
    int minR = getMin(points, ImmutablePoint::y).y();
    int maxR = getMax(points, ImmutablePoint::y).y();
    int minC = getMin(points, ImmutablePoint::x).x();
    int maxC = getMax(points, ImmutablePoint::x).x();
    for (int r = minR; r <= maxR; ++r) {
      for (int c = minC; c <= maxC; ++c) {
        System.out.print((points.contains(new ImmutablePoint(c, r)) ? '#' : ' '));
      }
      System.out.println();
    }
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
    
    @Override
    public String toString() {
      return "[" + fold + ", " + mag + "]";
    }
  }
}
