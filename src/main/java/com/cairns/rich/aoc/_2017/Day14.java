package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.Range;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A defrag process is running and needs to use our {@link KnotHash} algorithm to inspect the disk.
 */
class Day14 extends Base2017 {
  private static final int width = 128;
  private static final Range<Integer> valid = Range.closedOpen(0, width);

  /**
   * Returns the number of used spaces on the grid.
   * This is simply the cardinality (number of 1s) in our bitset.
   */
  @Override
  protected Object part1(Loader loader) {
    return buildGrid(loader).cardinality();
  }

  /**
   * Returns the number of contiguous but disjoint regions on our grid.
   * We compute this by doing a bfs over the grid from each non-explored used location.
   * Each location that has not been visited represents a new region, and the bfs disqualifies any connected locations.
   */
  @Override
  protected Object part2(Loader loader) {
    BitSet grid = buildGrid(loader);
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

  /**
   * Walks the grid in all of the {@link RelDir} directions from the given startFrom location.
   * Every reachable location will be marked as visited and thus part of this contiguous region.
   */
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

  /**
   * Converts a location into its {@link BitSet} index.
   */
  private int locationToIndex(ImmutablePoint location) {
    return locationToIndex(location.x(), location.y());
  }

  /**
   * Converts an (x,y) coordinate to a {@link BitSet} index.
   */
  private int locationToIndex(int x, int y) {
    return x + y * width;
  }

  /**
   * Converts a {@link BitSet} index into a location.
   */
  private ImmutablePoint indexToLocation(int index) {
    return new ImmutablePoint(index % width, index / width);
  }

  /**
   * Computes the disk grid according to the input.  Each row's knotHash is computed by appending the row
   * number to the input string.  A {@link KnotHash} contains 32 characters which each correspond to 4 bits.
   * These 4 bits are arranged in a high-bit first alignment (hence the 3 - j in the implementation).
   */
  private BitSet buildGrid(Loader loader) {
    String input = loader.sl();
    BitSet grid = new BitSet();
    for (int row = 0; row < width; ++row) {
      List<Integer> lengths = KnotHash.getLengthsFromString(input + "-" + row);
      String knotHash = KnotHash.getKnotHash(256, lengths);
      for (int i = 0; i < knotHash.length(); ++i) {
        char ch = knotHash.charAt(i);
        int bits = Integer.parseInt(Character.toString(ch), 16);
        for (int j = 0; j < 4; ++j) {
          if (0 != (bits & (1 << (3 - j)))) {
            grid.set(locationToIndex((i * 4) + j, row));
          }
        }
      }
    }
    return grid;
  }
}
