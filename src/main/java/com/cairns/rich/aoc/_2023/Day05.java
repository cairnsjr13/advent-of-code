package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * We need to plant seeds and determine where the closest location is.  Each seed
 * progresses through various requirements specified by our input almanac.
 */
class Day05 extends Base2023 {
  /**
   * Computes the closest seed location if the input seeds are taken as single seeds.
   */
  @Override
  protected Object part1(Loader loader) {
    return findSmallestLocation(loader, (lookup, spec) -> spec.forEach((seed) -> lookup.put(Range.singleton(seed), seed)));
  }

  /**
   * Computes the closest seed location if the input seeds are taken as ranges of seeds.
   */
  @Override
  protected Object part2(Loader loader) {
    return findSmallestLocation(loader, (lookup, spec) -> {
      for (int i = 0; i < spec.size(); i += 2) {
        Long start = spec.get(i);
        lookup.put(Range.closed(start, start + spec.get(i + 1) - 1), start);
      }
    });
  }

  /**
   * Starts with each of the initial seed ranges and finds the nearest final location.
   */
  private long findSmallestLocation(Loader loader, BiConsumer<RangeMap<Long, Long>, List<Long>> initialSeedHandler) {
    Map<String, AlmanacLookup> almanacLookups =
        getLookup(loader.gDelim("", (lines) -> new AlmanacLookup(lines, initialSeedHandler)));
    AlmanacLookup almanacLookup = almanacLookups.get("");
    long lowestLocation = Integer.MAX_VALUE;
    for (Range<Long> seeds : almanacLookup.lookup.asMapOfRanges().keySet()) {
      List<Range<Long>> finalRanges = new ArrayList<>();
      findAllFinalRanges(finalRanges, almanacLookups, "seed", seeds);
      lowestLocation = Math.min(lowestLocation, finalRanges.stream().mapToLong(Range::lowerEndpoint).min().getAsLong());
    }
    return lowestLocation;
  }

  /**
   * Recursively finds all of the final location ranges for the given input ids.  The range is iteratively broken
   * into a contiguous range and then recursed on until we land on the "location" type.  Doing this seed by seed
   * would take an unreasonable amount of time and we must do the complexity of contiguous ranges.
   */
  private void findAllFinalRanges(
      List<Range<Long>> finalRanges,
      Map<String, AlmanacLookup> almanacLookups,
      String currentType,
      Range<Long> ids
  ) {
    if ("location".equals(currentType)) {
      finalRanges.add(ids);
      return;
    }
    AlmanacLookup almanacLookup = almanacLookups.get(currentType);
    while (true) {
      Range<Long> contiguous = findContiguousRange(almanacLookup, ids);
      long delta = almanacLookup.lookup.get(contiguous.lowerEndpoint());
      findAllFinalRanges(
          finalRanges,
          almanacLookups,
          almanacLookup.toType,
          Range.closed(contiguous.lowerEndpoint() + delta, contiguous.upperEndpoint() + delta)
      );
      if (ids == contiguous) {
        break;
      }
      ids = Range.closed(contiguous.upperEndpoint() + 1, ids.upperEndpoint());
    }
  }

  /**
   * Finds the first contiguous piece of the input range that will all map to the same delta.
   * This is done using binary search and returning the input range if the entire range is included.
   */
  private Range<Long> findContiguousRange(AlmanacLookup almanacLookup, Range<Long> range) {
    Long left = range.lowerEndpoint();
    Long right = range.upperEndpoint();
    Range<Long> mapRange = almanacLookup.lookup.getEntry(left).getKey();
    if (mapRange == almanacLookup.lookup.getEntry(right).getKey()) {
      return range;
    }
    while (left + 1 < right) {
      Long inspect = (left + right) / 2;
      if (mapRange == almanacLookup.lookup.getEntry(right).getKey()) {
        left = inspect;
      }
      else {
        right = inspect;
      }
    }
    return Range.closed(range.lowerEndpoint(), left);
  }

  /**
   * Representation of one almanac entry for mappings of one type to another.   At its core, we store
   * a {@link RangeMap} where each input range maps to its output delta.  The initial seed spec will
   * be treated as having input "" and output "seed".  The initial seed specs will be provided to the
   * given handler for processing.  Note that after the ranges are computed, a final all encompassing
   * range with 0 delta is add to fill the gaps, making lookups of simple mappings easier.
   */
  private static class AlmanacLookup implements HasId<String> {
    private static final Pattern titlePattern = Pattern.compile("^([^-]+)-to-([^-]+) map:$");
    private static final Pattern lookupPattern = Pattern.compile("^(\\d+) (\\d+) (\\d+)$");

    private final String fromType;
    private final String toType;
    private final RangeMap<Long, Long> lookup = TreeRangeMap.create();

    private AlmanacLookup(List<String> lines, BiConsumer<RangeMap<Long, Long>, List<Long>> initialSeedHandler) {
      if (lines.size() == 1) {
        this.fromType = "";
        this.toType = "seeds";
        initialSeedHandler.accept(
            lookup,
            Arrays.stream(lines.get(0).split(" +")).skip(1).map(Long::parseLong).collect(Collectors.toList())
        );
      }
      else {
        Matcher titleMatcher = matcher(titlePattern, lines.get(0));
        this.fromType = titleMatcher.group(1);
        this.toType = titleMatcher.group(2);
        lines.subList(1, lines.size()).stream().map((line) -> matcher(lookupPattern, line)).forEach((m) -> {
          long from = lnum(m, 2);
          long delta = lnum(m, 1) - from;
          long length = lnum(m, 3);
          lookup.put(Range.closed(from, from + length - 1), delta);
        });
        lookup.merge(Range.closed(Long.MIN_VALUE, Long.MAX_VALUE), 0L, (existing, zero) -> existing);
      }
    }

    @Override
    public String getId() {
      return fromType;
    }
  }
}
