package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.Range;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day14 extends Base2017 {
  private static final int width = 128;
  private static final Range<Integer> valid = Range.closedOpen(0, width);

  @Override
  protected void run() {
    String input = "stpzcrnm";
    BitSet grid = buildGrid(input);
    System.out.println(grid.cardinality());
    System.out.println(countRegions(grid));
  }

  private int countRegions(BitSet grid) {
    Set<ImmutablePoint> visited = new HashSet<>();
    int numRegions = 0;
    for (int index = grid.nextSetBit(0); index != -1; index = grid.nextSetBit(index + 1)) {
      ImmutablePoint startFrom = indexToLocation(index);
      if (!visited.contains(startFrom)) {
        ++numRegions;
        walkFrom(grid, visited, startFrom);
      }
    }
    return numRegions;
  }

  private void walkFrom(BitSet grid, Set<ImmutablePoint> visited, ImmutablePoint startFrom) {
    bfs(
        startFrom,
        (s) -> false,
        SearchState::getNumSteps,
        (from, registrar) -> {
          visited.add(from);
          for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
            ImmutablePoint next = from.move(dir);
            if (valid.contains(next.x()) && valid.contains(next.y()) && grid.get(locationToIndex(next))) {
              registrar.accept(next);
            }
          }
        }
    );
  }

  private int locationToIndex(ImmutablePoint location) {
    return location.y() * width + location.x();
  }

  private ImmutablePoint indexToLocation(int index) {
    return new ImmutablePoint(index % width, index / width);
  }

  private BitSet buildGrid(String input) {
    BitSet grid = new BitSet();
    for (int row = 0; row < width; ++row) {
      List<Integer> lengths = KnotHash.getLengthsFromString(input + "-" + row);
      String knotHash = KnotHash.getKnotHash(256, lengths);
      for (int i = 0; i < knotHash.length(); ++i) {
        char ch = knotHash.charAt(i);
        int bits = Integer.parseInt(Character.toString(ch), 16);
        for (int j = 0; j < 4; ++j) {
          if (0 != (bits & (1 << (3 - j)))) {
            grid.set((row * width) + (i * 4) + j);
          }
        }
      }
    }
    return grid;
  }
}
