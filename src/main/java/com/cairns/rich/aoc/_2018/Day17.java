package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.TreeMultimap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

class Day17 extends Base2018 {
  private static final Pattern pattern = Pattern.compile("^(x|y)=(\\d+), (x|y)=(\\d+)\\.\\.(\\d+)$");
  private static final EnumSet<Status> supportStatuses = EnumSet.of(Status.Clay, Status.Water);
  private static final EnumSet<Status> moistStatuses = EnumSet.of(Status.Water, Status.Wet);

  @Override
  protected Object part1(Loader loader) {
    return countTilesAfterDrip(loader, moistStatuses::contains);
  }

  @Override
  protected Object part2(Loader loader) {
    return countTilesAfterDrip(loader, Status.Water::equals);
  }

  private long countTilesAfterDrip(Loader loader, Predicate<Status> filter) {
    TreeBasedTable<Integer, Integer, Status> yToXTileStatus = TreeBasedTable.create();
    TreeMultimap<Integer, Integer> yToXClay = TreeMultimap.create();
    loader.ml().forEach((line) -> addAllClayTiles(line, yToXClay));
    yToXClay.forEach((y, x) -> yToXTileStatus.put(y, x, Status.Clay));
    int yMin = yToXClay.keySet().first();

    drip(yToXTileStatus, yToXClay, 0, 500);
    IntStream.range(0, yMin).forEach((row) -> yToXTileStatus.row(row).clear());
    return yToXTileStatus.cellSet().stream().map(Cell::getValue).filter(filter).count();
  }

  private void drip(
      TreeBasedTable<Integer, Integer, Status> yToXTileStatus,
      TreeMultimap<Integer, Integer> yToXClay,
      int y,
      int x
  ) {
    if ((Status.Empty != getStatus(yToXTileStatus, y, x)) || (y > yToXTileStatus.rowKeySet().last())) {
      return;
    }
    yToXTileStatus.put(y, x, Status.Wet);
    drip(yToXTileStatus, yToXClay, y + 1, x);
    if (supportStatuses.contains(getStatus(yToXTileStatus, y + 1, x))) {
      Integer leftWall = findWall(yToXTileStatus, yToXClay, y, x, -1);
      Integer rightWall = findWall(yToXTileStatus, yToXClay, y, x, 1);
      if ((leftWall != null) && (rightWall != null)) {
        for (int cx = leftWall + 1; cx < rightWall; ++cx) {
          yToXTileStatus.put(y, cx, Status.Water);
        }
      }
    }
  }

  private Integer findWall(
      TreeBasedTable<Integer, Integer, Status> yToXTileStatus,
      TreeMultimap<Integer, Integer> yToXClay,
      int y,
      int x,
      int dx
  ) {
    for (int cx = x + dx; true; cx += dx) {
      if (Status.Clay == getStatus(yToXTileStatus, y, cx)) {
        return cx;
      }
      drip(yToXTileStatus, yToXClay, y, cx);
      if (!supportStatuses.contains(getStatus(yToXTileStatus, y + 1, cx))) {
        return null;
      }
    }
  }

  private void addAllClayTiles(String spec, TreeMultimap<Integer, Integer> yToXClay) {
    Matcher matcher = matcher(pattern, spec);
    Map<Character, Range<Integer>> ranges = new HashMap<>();
    ranges.put(matcher.group(1).charAt(0), Range.singleton(num(matcher, 2)));
    ranges.put(matcher.group(3).charAt(0), Range.closed(num(matcher, 4), num(matcher, 5)));

    Range<Integer> xRange = ranges.get('x');
    Range<Integer> yRange = ranges.get('y');
    for (int y = yRange.lowerEndpoint(); y <= yRange.upperEndpoint(); ++y) {
      for (int x = xRange.lowerEndpoint(); x <= xRange.upperEndpoint(); ++x) {
        yToXClay.put(y, x);
      }
    }
  }

  private Status getStatus(Table<Integer, Integer, Status> yToXTileStatus, int y, int x) {
    return (yToXTileStatus.contains(y, x)) ? yToXTileStatus.get(y, x) : Status.Empty;
  }

  private enum Status {
    Clay,
    Water,
    Wet,
    Empty;
  }
}
