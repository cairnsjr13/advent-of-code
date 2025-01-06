package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Towels need to be arranged in specific stripe orders.  There are an infinite
 * number of each towel type and a list of designs that need to be created.
 */
class Day19 extends Base2024 {
  /**
   * A simple regex where all patterns are joined with "|" and have a one-or-more matcher (+) will detect matches.
   */
  @Override
  protected Object part1(Loader loader) {
    List<String> lines = loader.ml();
    Pattern pattern = Pattern.compile("(" + lines.get(0).replace(", ", "|") + ")+");
    return lines.stream().skip(2).map(pattern::matcher).filter(Matcher::matches).count();
  }

  /**
   * Using a cache from each index allows the algorithm to avoid recomputing seen states for towel orders.
   */
  @Override
  protected Object part2(Loader loader) {
    List<String> lines = loader.ml();
    Set<String> patterns = Arrays.stream(lines.get(0).split(", ")).collect(Collectors.toSet());
    Function<String, HashMap<Integer, Long>> cacheGen = (design) -> new HashMap<>(Map.of(design.length(), 1L));
    return lines.stream().skip(2).mapToLong((design) -> numWays(cacheGen.apply(design), patterns, design, 0)).sum();
  }

  /**
   * Recursive method to compute the number of unique ways to arrange towels from
   * the given design index.  The cache will avoid recomputing from the same index.
   * It is important that the given cache already has length mapping to 1.
   */
  private long numWays(Map<Integer, Long> cache, Set<String> patterns, String design, Integer from) {
    if (cache.containsKey(from)) {
      return cache.get(from);
    }
    long numWays = patterns.stream()
        .filter((pattern) -> design.substring(from).startsWith(pattern))
        .mapToLong((pattern) -> numWays(cache, patterns, design, from + pattern.length()))
        .sum();
    cache.put(from, numWays);
    return numWays;
  }
}
