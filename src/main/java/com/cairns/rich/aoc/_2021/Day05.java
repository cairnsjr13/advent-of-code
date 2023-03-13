package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day05 extends Base2021 {
  @Override
  protected void run() {
    List<LineSeg> lineSegs = fullLoader.ml(LineSeg::new);
    System.out.println(findNumOverlaps(lineSegs.stream().filter(LineSeg::isHorizOrVert).collect(Collectors.toList())));
    System.out.println(findNumOverlaps(lineSegs));
  }

  private long findNumOverlaps(List<LineSeg> lineSegs) {
    Multiset<ImmutablePoint> points = HashMultiset.create();
    for (LineSeg lineSeg : lineSegs) {
      EnumSet<RelDir> dirs = getDirsToMove(lineSeg);
      Point<?> cur = new MutablePoint(lineSeg.start);
      while (!cur.equals(lineSeg.end)) {
        points.add(new ImmutablePoint(cur));
        dirs.forEach(cur::move);
      }
      points.add(lineSeg.end);
    }
    return points.elementSet().stream().filter((e) -> points.count(e) > 1).count();
  }

  private EnumSet<RelDir> getDirsToMove(LineSeg lineSeg) {
    EnumSet<RelDir> dirs = EnumSet.noneOf(RelDir.class);
    if (lineSeg.start.x() != lineSeg.end.x()) {
      dirs.add((lineSeg.start.x() < lineSeg.end.x()) ? RelDir.Right : RelDir.Left);
    }
    if (lineSeg.start.y() != lineSeg.end.y()) {
      dirs.add((lineSeg.start.y() < lineSeg.end.y()) ? RelDir.Up : RelDir.Down);
    }
    return dirs;
  }

  private static class LineSeg {
    private static final Pattern pattern = Pattern.compile("^(\\d+),(\\d+) -> (\\d+),(\\d+)$");

    private final ImmutablePoint start;
    private final ImmutablePoint end;

    private LineSeg(String line) {
      Matcher matcher = matcher(pattern, line);
      this.start = new ImmutablePoint(num(matcher, 1), num(matcher, 2));
      this.end = new ImmutablePoint(num(matcher, 3), num(matcher, 4));
    }

    private boolean isHorizOrVert() {
      return (start.x() == end.x()) || (start.y() == end.y());
    }
  }
}
