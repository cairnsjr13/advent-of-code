package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day08 extends Base2022 {
  @Override
  protected Object part1(Loader2 loader) {
    List<String> lines = loader.ml();
    Table<ImmutablePoint, ReadDir, ReadDir> walks = buildWalks(lines);
    Set<ImmutablePoint> visible = new HashSet<>();
    walks.rowKeySet().forEach((from) -> walks.row(from).forEach((walk, look) -> {
      for (ImmutablePoint lineStart = from; inBounds(lines, lineStart); lineStart = lineStart.move(walk)) {
        viewAllTreesFrom(visible, lines, lineStart, look);
      }
    }));
    return visible.size();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<String> lines = loader.ml();
    int maxScenicScore = 0;
    for (ImmutablePoint start = new ImmutablePoint(0, 0); inBounds(lines, start); start = start.move(ReadDir.Down)) {
      for (ImmutablePoint from = start; inBounds(lines, from); from = from.move(ReadDir.Right)) {
        maxScenicScore = Math.max(maxScenicScore, findScenicScore(lines, from));
      }
    }
    return maxScenicScore;
  }

  private Table<ImmutablePoint, ReadDir, ReadDir> buildWalks(List<String> lines) {
    ImmutablePoint topLeft = new ImmutablePoint(0, 0);
    ImmutablePoint bottomRight = new ImmutablePoint(lines.get(0).length() - 1, lines.size() - 1);
    Table<ImmutablePoint, ReadDir, ReadDir> walks = HashBasedTable.create();
    walks.put(topLeft, ReadDir.Right, ReadDir.Down);
    walks.put(topLeft, ReadDir.Down, ReadDir.Right);
    walks.put(bottomRight, ReadDir.Up, ReadDir.Left);
    walks.put(bottomRight, ReadDir.Left, ReadDir.Up);
    return walks;
  }

  private void viewAllTreesFrom(Set<ImmutablePoint> visible, List<String> lines, ImmutablePoint start, ReadDir look) {
    int maxHeight = -1;
    for (ImmutablePoint current = start; inBounds(lines, current) && (maxHeight < 9); current = current.move(look)) {
      int height = height(lines, current);
      if (height > maxHeight) {
        visible.add(current);
        maxHeight = height;
      }
    }
  }

  private int findScenicScore(List<String> lines, ImmutablePoint from) {
    int target = height(lines, from);
    int scenicScore = 1;
    for (ReadDir look : EnumUtils.enumValues(ReadDir.class)) {
      int distance = 0;
      for (ImmutablePoint current = from.move(look); inBounds(lines, current); current = current.move(look)) {
        ++distance;
        if (height(lines, current) >= target) {
          break;
        }
      }
      scenicScore *= distance;
      if (scenicScore == 0) {
        break;
      }
    }
    return scenicScore;
  }

  private boolean inBounds(List<String> lines, ImmutablePoint point) {
    return (0 <= point.x()) && (point.x() < lines.get(0).length())
        && (0 <= point.y()) && (point.y() < lines.size());
  }

  private int height(List<String> lines, ImmutablePoint from) {
    return lines.get(from.y()).charAt(from.x()) - '0';
  }
}
