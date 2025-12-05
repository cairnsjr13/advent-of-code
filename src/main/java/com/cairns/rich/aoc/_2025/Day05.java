package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import java.util.List;

/**
 * Ranges of ingredient ids are considered fresh.  We need to gather various statistics about these ranges.
 *
 * NOTE: It feels a little bit like cheating to use {@link RangeSet}, but it is what it is.
 *       There is alternative algorithm in the comment of each part that is confirmed to work.
 */
class Day05 extends Base2025 {
  /**
   * Once the ranges are parsed, we simply need to know how many of our ingredients are in any of the fresh ranges.
   * Using the {@link RangeSet} is overkill because we don't need disjoint ranges for this to work, but it works.
   *
   * NOTE: An alternative algorithm can skip the {@link RangeSet} and just use a list of {@link Range}s
   *       since there are not that many range (O(100s)).  So O(n) is not that much faster than O(nm).
   */
  @Override
  protected Object part1(Loader loader) {
    List<List<String>> lineGroups = loader.gDelim("");
    RangeSet<Long> freshRanges = buildFreshRanges(lineGroups.get(0));
    return lineGroups.get(1).stream().map(Long::parseLong).filter(freshRanges::contains).count();
  }

  /**
   * The total count of ingredient ids need to be computed.  The ranges being dealt with are massive, so iteration to
   * check for inclusion in the ranges is not practical.  The {@link RangeSet} consolidates all added ranges to the
   * minimal, disjoint set of {@link Range}s as it is built.  This can be leveraged to sum of the sizes of each range
   * to get the answer as the disjoint nature guarantees there will be no double counting.
   *
   * NOTE: An alternative algorithm can skip the {@link RangeSet} and just use a highWatermark.  After sorting
   *       the ranges by their lowEndpoint, we only add subRanges that are strictly above the highWatermark.
   */
  @Override
  protected Object part2(Loader loader) {
    RangeSet<Long> freshRanges = buildFreshRanges(loader.gDelim("").get(0));
    return freshRanges.asRanges().stream()
        .mapToLong((freshRange) -> freshRange.upperEndpoint() - freshRange.lowerEndpoint() + 1)
        .sum();
  }

  /**
   * Helper method to compute a {@link RangeSet} representing all of the freshRanges in the given specs.
   * The {@link RangeSet#asRanges()} method can be used to access a reduced, minimal set of disjoint {@link Range}s.
   */
  private RangeSet<Long> buildFreshRanges(List<String> specs) {
    RangeSet<Long> freshRanges = TreeRangeSet.create();
    specs.stream()
        .map((line) -> line.split("-"))
        .map((pieces) -> Range.closed(Long.parseLong(pieces[0]), Long.parseLong(pieces[1])))
        .forEach(freshRanges::add);
    return freshRanges;
  }
}
