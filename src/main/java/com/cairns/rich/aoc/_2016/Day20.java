package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Day20 extends Base2016 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Range<Long>> blacklist = loader.ml(this::parse);
    List<Range<Long>> disjointBlacklist = getDisjointBlacklists(blacklist);
    return disjointBlacklist.get(0).upperEndpoint() + 1;
  }

  @Override
  protected Object part2(Loader2 loader) {
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

  private boolean canBeCollapsed(Range<Long> left, Range<Long> right) {
    return left.contains(right.lowerEndpoint())
        || (left.upperEndpoint() == right.lowerEndpoint() - 1);
  }

  private Range<Long> parse(String spec) {
    String[] parts = spec.split("-");
    return Range.closed(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
  }
}
