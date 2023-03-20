package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Day23 extends Base2022 {
  private static final CardDir[] dirs = { CardDir.North, CardDir.South, CardDir.West, CardDir.East };

  @Override
  protected Object part1(Loader2 loader) {
    List<Elf> elves = parse(loader.ml());
    Set<Point<?>> map = elves.stream().map((e) -> e.location).collect(Collectors.toSet());
    for (int round = 0; round <10; ++round) {
      computeProposals(elves, map, round);
      tryMovesHadAnyMoves(elves, map);
    }
    return numEmptyTiles(map);
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Elf> elves = parse(loader.ml());
    Set<Point<?>> map = elves.stream().map((e) -> e.location).collect(Collectors.toSet());
    for (int round = 0; true; ++round) {
      computeProposals(elves, map, round);
      if (!tryMovesHadAnyMoves(elves, map)) {
        return round + 1;
      }
    }
  }

  private void computeProposals(List<Elf> elves, Set<Point<?>> map, int round) {
    for (Elf elf : elves) {
      elf.proposal = elf.location;
      if (needsToMove(elf, map)) {
        for (int d = 0; d < dirs.length; ++d) {
          CardDir dir = safeGet(dirs, round + d);
          if (isDirClear(elf, map, dir)) {
            elf.proposal = elf.location.move(dir);
            break;
          }
        }
      }
    }
  }

  private boolean tryMovesHadAnyMoves(List<Elf> elves, Set<Point<?>> map) {
    boolean hadMoves = false;
    Multiset<ImmutablePoint> proposals = HashMultiset.create();
    elves.forEach((e) -> proposals.add(e.proposal));
    for (Elf elf : elves) {
      if (proposals.count(elf.proposal) == 1) {
        if (!elf.location.equals(elf.proposal)) {
          hadMoves = true;
          map.remove(elf.location);
          map.add(elf.proposal);
        }
        elf.location = elf.proposal;
      }
    }
    return hadMoves;
  }

  private boolean needsToMove(Elf elf, Set<Point<?>> map) {
    MutablePoint test = MutablePoint.origin();
    for (int dx = -1; dx <= 1; ++dx) {
      test.x(elf.location.x() + dx);
      for (int dy = -1; dy <= 1; ++dy) {
        if ((dx != 0) || (dy != 0)) {
          test.y(elf.location.y() + dy);
          if (map.contains(test)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean isDirClear(Elf elf, Set<Point<?>> map, CardDir dir) {
    MutablePoint test = new MutablePoint(elf.location.move(dir));
    if (map.contains(test)) {
      return false;
    }
    test.move(dir.turnLeft());
    if (map.contains(test)) {
      return false;
    }
    test.move(dir.turnRight(), 2);
    if (map.contains(test)) {
      return false;
    }
    return true;
  }

  private int numEmptyTiles(Set<Point<?>> map) {
    int minX = getMin(map, Point::x).x();
    int maxX = getMax(map, Point::x).x();
    int minY = getMin(map, Point::y).y();
    int maxY = getMax(map, Point::y).y();
    return ((maxX - minX + 1) * (maxY - minY + 1)) - map.size();
  }

  private List<Elf> parse(List<String> lines) {
    List<Elf> elves = new ArrayList<>();
    for (int y = 0; y < lines.size(); ++y) {
      String line = lines.get(lines.size() - y - 1);
      for (int x = 0; x < line.length(); ++x) {
        if (line.charAt(x) == '#') {
          elves.add(new Elf(x, y));
        }
      }
    }
    return elves;
  }

  private static class Elf {
    private ImmutablePoint location;
    private ImmutablePoint proposal;

    private Elf(int x, int y) {
      this.location = new ImmutablePoint(x, y);
    }
  }
}
