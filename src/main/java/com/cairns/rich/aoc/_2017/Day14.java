package com.cairns.rich.aoc._2017;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.Range;

class Day14 extends Base2017 {
  private static final Range<Integer> valid = Range.closedOpen(0, 128);
  
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
    for (int row = 0; row < 128; ++row) {
      for (int col = 0; col < 128; ++col) {
        if (anyNewVisitedFrom(grid, visited, new ImmutablePoint(row, col))) {
          ++numRegions;
        }
      }
    }
    return numRegions;
  }
  
  private boolean anyNewVisitedFrom(BitSet grid, Set<ImmutablePoint> visited, ImmutablePoint location) {
    if (!valid.contains(location.x()) || !valid.contains(location.y()) ||
        visited.contains(location) || !grid.get(locationToIndex(location)))
    {
      return false;
    }
    visited.add(location);
    for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
      anyNewVisitedFrom(grid, visited, location.move(dir));
    }
    return true;
  }
  
  private int locationToIndex(ImmutablePoint location) {
    return location.y() * 128 + location.x();
  }
  
  private BitSet buildGrid(String input) {
    BitSet grid = new BitSet();
    for (int row = 0; row < 128; ++row) {
      List<Integer> lengths = KnotHash.getLengthsFromString(input + "-" + row);
      String knotHash = KnotHash.getKnotHash(256, lengths);
      for (int i = 0; i < knotHash.length(); ++i) {
        char ch = knotHash.charAt(i);
        int bits = Integer.parseInt(Character.toString(ch), 16);
        for (int j = 0; j < 4; ++j) {
          if (0 != (bits & (1 << (3 - j)))) {
            grid.set((row * 128) + (i * 4) + j);
          }
        }
      }
    }
    return grid;
  }
}
