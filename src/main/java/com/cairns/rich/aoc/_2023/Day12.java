package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Damage reports are themselves damaged.  We need to figure out how many different possible arrangements the damage can be in.
 * Memoization is critical for any reasonable solution since we will be computing the same result over and over for large specs.
 */
class Day12 extends Base2023 {
  private static final Set<Character> boundaryChars = Set.of('.', '?');
  private static final Set<Character> damageChars = Set.of('#', '?');

  /**
   * Computes the total number of arrangements with no unfolding (factor 1).
   */
  @Override
  protected Object part1(Loader loader) {
    return findTotalArrangements(loader, 1);
  }

  /**
   * Computes the total number of arrangements with unfolding (factor 5).
   */
  @Override
  protected Object part2(Loader loader) {
    return findTotalArrangements(loader, 5);
  }

  /**
   * Helper method to compute the total number of arrangements with the given unfolding factor.
   */
  private long findTotalArrangements(Loader loader, int unfoldFactor) {
    return loader.ml((line) -> new Spec(line, unfoldFactor)).stream()
        .mapToLong((spec) -> findNumArrangements(HashBasedTable.create(), spec, 0, 0))
        .sum();
  }

  /**
   * Computes the number of valid arrangements possible for the given spec from the given count index and
   * damageStates index.  This is done using memoization by seeing that the answer will always be the same
   * from the unique pair of those indexes.  We take each contiguous stretch of expected damaged springs
   * (the counts array), and attempt to place them into valid  positions along the damageStates string.
   * This allows us to compute the answer in O(c*d) time and space, where c is the number of counts and d is
   * the length of the damageStates spec.  The base case of the recursion is reaching the end of either spec.
   */
  private long findNumArrangements(Table<Integer, Integer, Long> cache, Spec spec, Integer countI, Integer damageStatesI) {
    if (damageStatesI >= spec.damageStates.length()) {
      return (countI >= spec.counts.length) ? 1 : 0;
    }
    if (countI >= spec.counts.length) {
      return (spec.damageStates.indexOf('#', damageStatesI) == -1) ? 1 : 0;
    }
    if (!cache.contains(countI, damageStatesI)) {
      char ch = spec.damageStates.charAt(damageStatesI);
      long withDot = (boundaryChars.contains(ch)) ? findNumArrangements(cache, spec, countI, damageStatesI + 1) : 0;
      long withHash = (canBeContiguousDamage(spec, spec.counts[countI], damageStatesI))
                    ? findNumArrangements(cache, spec, countI + 1, damageStatesI + spec.counts[countI] + 1)
                    : 0;
      cache.put(countI, damageStatesI, withDot + withHash);
    }
    return cache.get(countI, damageStatesI);
  }

  /**
   * Returns true if the given index can be the beginning of a contiguous stretch of damaged springs.  All spots in that
   * width must be either explicitly damaged ('#') or possibly damaged ('?').  Additionally, the spot  following the
   * contiguous width must either be explicitly undamaged ('.') or possibly undamaged ('?') or past the end of the spec.
   */
  private boolean canBeContiguousDamage(Spec spec, int width, int damageStatesI) {
    int damageCount = 0;
    for (int i = 0; (i < width) && (damageStatesI + i) < spec.damageStates.length(); ++i) {
      if (damageChars.contains(spec.damageStates.charAt(damageStatesI + i))) {
        ++damageCount;
      }
    }
    return (damageCount == width) && isBoundary(spec, damageStatesI + width);
  }

  /**
   * Returns true if the given index can be considered a boundary between damage counts.
   * There is guaranteed to be at least some undamaged springs between the contiguous damaged ones, so a boundary
   * is either an undamaged spot (or unknown that we can assign to be undamaged) or past the end of the spec.
   */
  private boolean isBoundary(Spec spec, int damageStatesI) {
    return (damageStatesI >= spec.damageStates.length()) || boundaryChars.contains(spec.damageStates.charAt(damageStatesI));
  }

  /**
   * Descriptor class for a damage record.  The spec will include two parts separated by a space:
   *   1) damageState: string of states where '.' means ok, '#' means damaged, and '?' means unknown
   *   2) contiguousCounts: commas separated list of the lengths of contiguous damaged sections
   */
  private static class Spec {
    private static final Pattern pattern = Pattern.compile("^([^ ]+) ([^ ]+)$");

    private final String damageStates;
    private final int[] counts;

    private Spec(String line, int unfoldFactor) {
      Matcher matcher = matcher(pattern, line);
      this.damageStates = unfold(matcher.group(1), unfoldFactor, "?");
      this.counts = Arrays.stream(unfold(matcher.group(2), unfoldFactor, ",").split(",")).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Helper method to unfold the given input by repeating it the given factor of times and joining them with the given string.
     */
    private String unfold(String in, int unfoldFactor, String join) {
      return IntStream.range(0, unfoldFactor).mapToObj((i) -> in).collect(Collectors.joining(join));
    }
  }
}
