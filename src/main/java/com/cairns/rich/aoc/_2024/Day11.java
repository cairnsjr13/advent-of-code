package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.HashMap;
import java.util.Map;

/**
 * Rocks expand when blinking.  We need to "simulate" the changes and count the stones.
 */
class Day11 extends Base2024 {
  private static final ConfigToken<Integer> numTicksToken = ConfigToken.of("numTicks", Integer::parseInt);

  /**
   * Computes the total number of stones after the configured number of ticks.
   */
  @Override
  protected Object part1(Loader loader) {
    return countTotalStonesAfterTicks(loader);
  }

  /**
   * Computes the total number of stones after the configured number of ticks.
   */
  @Override
  protected Object part2(Loader loader) {
    return countTotalStonesAfterTicks(loader);
  }

  /**
   * Runs the configured number of {@link #tick(Map)}s and returns the final number of stones.
   */
  private long countTotalStonesAfterTicks(Loader loader) {
    int numTicks = loader.getConfig(numTicksToken);
    Map<Long, Long> cur = new HashMap<>();
    for (long stone : loader.sl(" +", Long::parseLong)) {
      addStones(cur, stone, 1);
    }
    for (int i = 0; i < numTicks; ++i) {
      cur = tick(cur);
    }
    return cur.values().stream().mapToLong(Long::longValue).sum();
  }

  /**
   * Returns a new {@link Map} describing the next stone gen after performing a tick on the given cur gen.
   * Each stone is independently changed/expanded according to these rules (first matching):
   *   - 0 changes in place to 1
   *   - even number of digits become 2 stones (first half and second half)
   *   - odd number of digits changes in place to 2048 * origValue
   * The key optimization is treating all stones with the same value as the same.
   */
  private Map<Long, Long> tick(Map<Long, Long> cur) {
    Map<Long, Long> next = new HashMap<>();
    cur.forEach((stone, count) -> {
      if (stone == 0) {
        addStones(next, 1, count);
      }
      else {
        String str = Long.toString(stone);
        if (str.length() % 2 == 0) {
          addStones(next, Long.parseLong(str.substring(str.length() / 2)), count);
          addStones(next, Long.parseLong(str.substring(0, str.length() / 2)), count);
        }
        else {
          addStones(next, stone * 2024, count);
        }
      }
    });
    return next;
  }

  /**
   * Helper method to merge the given count of stones into the tracker map.
   */
  private void addStones(Map<Long, Long> stones, long stone, long count) {
    stones.merge(stone, count, (l, r) -> l + r);
  }
}
