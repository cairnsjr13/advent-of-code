package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * We need to inspect a fire blacklist and determine characteristics about addresses allowed.
 *
 * Note: Inputs are assumed to contain a range [0-x].
 */
class Day20 extends Base2016 {
  /**
   * Returns the lowest allowed address by sorting and collapsing blacklist ranges.
   * The lowest allowed address is one larger than the lowest ranges upper endpoint
   * because if there wasn't a gap the first two would have been collapsed.
   */
  @Override
  protected Object part1(Loader loader) {
    List<Range<Long>> blacklist = loader.ml(this::parse);
    List<Range<Long>> disjointBlacklist = getDisjointBlacklists(blacklist);
    return disjointBlacklist.get(0).upperEndpoint() + 1;
  }

  /**
   * Returns the number of addresses allowed by the blacklist.  This can be
   * computed by seeing the size of the gaps between disjoint blacklists.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Range<Long>> blacklist = loader.ml(this::parse);
    List<Range<Long>> disjointBlacklist = getDisjointBlacklists(blacklist);
    long numAllowed = 0;
    for (int i = 0; i < disjointBlacklist.size() - 1; ++i) {
      Range<Long> left = disjointBlacklist.get(i);
      Range<Long> right = disjointBlacklist.get(i + 1);
      numAllowed += (right.lowerEndpoint() - left.upperEndpoint() - 1);
    }
    return numAllowed;
  }

  /**
   * Creates a sorted disjoint list of collapsed blacklist ranges.
   * By sorting based on lower endpoint and then collapsing ranges with no gaps
   * between them, we can easily compute various characteristics of the router.
   */
  private List<Range<Long>> getDisjointBlacklists(List<Range<Long>> blacklist) {
    Collections.sort(blacklist, Comparator.comparing((r) -> r.lowerEndpoint()));
    List<Range<Long>> condensed = new ArrayList<>();
    Range<Long> current = blacklist.get(0);
    for (int i = 1; i < blacklist.size(); ++i) {
      Range<Long> inspect = blacklist.get(i);
      if (canBeCollapsed(current, inspect)) {
        current = Range.closed(current.lowerEndpoint(), Math.max(current.upperEndpoint(), inspect.upperEndpoint()));
      }
      else {
        condensed.add(current);
        current = inspect;
      }
    }
    condensed.add(current);
    return condensed;
  }

  /**
   * Two ranges can be collapsed if they overlap or have no gap between them.
   * We can check this by seeing if the left range contains the right ranges lower endpoint
   * or the left range's upper endpoint is right next to the right ranges lower endpoint.
   * This assumes that the ranges are passed in sorted based on lower endpoint.
   */
  private boolean canBeCollapsed(Range<Long> left, Range<Long> right) {
    return left.contains(right.lowerEndpoint())
        || (left.upperEndpoint() == right.lowerEndpoint() - 1);
  }

  /**
   * Parses a {@link Range} from a line in the input.  The low and high are separated by a dash.
   */
  private Range<Long> parse(String spec) {
    String[] parts = spec.split("-");
    return Range.closed(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
  }
}
