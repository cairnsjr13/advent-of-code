package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class Day14 extends Base2022 {
  private static final Supplier<MutablePoint> newSandCreator = () -> new MutablePoint(500, 0);

  @Override
  protected Object part1(Loader loader) {
    return countSand(loader, 0, 2);
  }

  @Override
  protected Object part2(Loader loader) {
    return countSand(loader, 4, 2);
  }

  private int countSand(Loader loader, int voidOffset, int floorOffset) {
    Set<Point<?>> scan = buildScan(loader);
    int maxY = scan.stream().mapToInt(Point::y).max().getAsInt();
    int voidY = maxY + voidOffset;
    int floorY = maxY + floorOffset;
    int numSand = 0;
    while (placeSand(scan, voidY, floorY)) {
      ++numSand;
    }
    return numSand;
  }

  private boolean placeSand(Set<Point<?>> scan, int voidY, int floorY) {
    MutablePoint newSand = newSandCreator.get();
    if (scan.contains(newSand)) {
      return false;
    }
    Predicate<MutablePoint> contains = (p) -> scan.contains(p) || (p.y() == floorY);
    while (newSand.y() < voidY) {
      newSand.move(ReadDir.Down);
      if (contains.test(newSand)) {
        newSand.move(ReadDir.Left);
        if (contains.test(newSand)) {
          newSand.move(ReadDir.Right, 2);
          if (contains.test(newSand)) {
            scan.add(new ImmutablePoint(newSand.x() - 1, newSand.y() - 1));
            return true;
          }
        }
      }
    }
    return false;
  }

  private Set<Point<?>> buildScan(Loader loader) {
    List<List<ImmutablePoint>> rockDescs = loader.ml(this::parseRockDesc);
    Set<Point<?>> scan = new HashSet<>();
    for (List<ImmutablePoint> rockDesc : rockDescs) {
      for (int i = 0; i < rockDesc.size() - 1; ++i) {
        addAllRocks(scan, rockDesc.get(i), rockDesc.get(i + 1));
      }
    }
    return scan;
  }

  private void addAllRocks(Set<Point<?>> scan, ImmutablePoint start, ImmutablePoint end) {
    if ((start.x() > end.x()) || (start.y() > end.y())) {
      addAllRocks(scan, end, start);
    }
    else {
      ReadDir dir = (start.x() != end.x()) ? ReadDir.Right : ReadDir.Down;
      for (ImmutablePoint current = start; !current.equals(end); current = current.move(dir)) {
        scan.add(current);
      }
      scan.add(end);
    }
  }

  private List<ImmutablePoint> parseRockDesc(String line) {
    return Arrays.stream(line.split(" -> ")).map((point) -> {
      int comma = point.indexOf(',');
      return new ImmutablePoint(
          Integer.parseInt(point.substring(0, comma)),
          Integer.parseInt(point.substring(comma + 1))
      );
    }).collect(Collectors.toList());
  }
}
