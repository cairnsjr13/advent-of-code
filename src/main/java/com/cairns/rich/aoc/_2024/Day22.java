package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * By selling hiding spots to monkeys we can acquire bananas.  However, because of a language barrier,
 * we can only trigger sales by using seller sequences computed based on the monkeys' secret numbers.
 */
class Day22 extends Base2024 {
  private static final ConfigToken<Integer> numExtraValuesToken = ConfigToken.of("numExtraValues", Integer::parseInt);

  @Override
  protected Object part1(Loader loader) {
    /**
     * Sums the configured secret number for each buyer.  There is no need to mod the secrets as we need the whole thing.
     */
    int numExtraValues = loader.getConfig(numExtraValuesToken);
    return loader.ml(Long::parseLong).stream()
        .map((initial) -> computeSecretNumbers(initial, numExtraValues, Long.MAX_VALUE))
        .mapToLong((secretNumbers) -> secretNumbers.get(secretNumbers.size() - 1))
        .sum();
  }

  /**
   * Computes the maximum number of bananas that can be acquired through a common seller sequence.
   * Since all buyers must use the same seller sequence, we can register every single 4 delta window
   * for every buyer with how many bananas that would yield.  By using a {@link Multiset} across
   * buyers, we can determine which seller sequence is the most valuable.  It is important to only
   * consider the first time a seller sequence appears for each buyer as later sales wont happen.
   */
  @Override
  protected Object part2(Loader loader) {
    int numExtraValues = loader.getConfig(numExtraValuesToken);
    List<Long> initials = loader.ml(Long::parseLong);

    Multiset<Integer> totalBananasPossibleWithWindowId = HashMultiset.create();
    for (long initial : initials) {
      Map<Integer, Integer> sellerBananasBuyableAt = new HashMap<>();
      List<Integer> secretNumbers = computeSecretNumbers(initial, numExtraValues, 10);

      int windowId = shiftWindowId(0, secretNumbers.get(1) - secretNumbers.get(0));
      windowId = shiftWindowId(windowId, secretNumbers.get(2) - secretNumbers.get(1));
      windowId = shiftWindowId(windowId, secretNumbers.get(3) - secretNumbers.get(2));
      windowId = shiftWindowId(windowId, secretNumbers.get(4) - secretNumbers.get(3));
      sellerBananasBuyableAt.put(windowId, secretNumbers.get(4));

      for (int i = 5; i < secretNumbers.size(); ++i) {
        windowId = shiftWindowId(windowId, secretNumbers.get(i) - secretNumbers.get(i - 1));
        sellerBananasBuyableAt.putIfAbsent(windowId, secretNumbers.get(i));
      }
      sellerBananasBuyableAt.forEach(totalBananasPossibleWithWindowId::add);
    }
    return getMax(totalBananasPossibleWithWindowId.entrySet(), Entry::getCount).getCount();
  }

  /**
   * Computes a monkey's secret numbers based its initial value and significant mod.
   *
   * Note:
   *   - multiplying by 64 is equivalent to left shifting by 6
   *   - dividing by 32 is equivalent to right shifting by 5
   *   - multiplying by 2048 is equivalent to left shifting by 11
   *   - moding by 16777216 (1 << 24) is equivalent to keeping the 24 least sig bits.
   */
  private List<Integer> computeSecretNumbers(long initial, int numExtraValues, long finalMod) {
    List<Integer> secretNumbers = new ArrayList<>();
    long secretNumber = initial;
    secretNumbers.add((int) (initial % finalMod));
    for (int i = 0; i < numExtraValues; ++i) {
      secretNumber = (secretNumber ^ (secretNumber <<  6)) & 0b11111111_11111111_11111111;
      secretNumber = (secretNumber ^ (secretNumber >>  5)) & 0b11111111_11111111_11111111;
      secretNumber = (secretNumber ^ (secretNumber << 11)) & 0b11111111_11111111_11111111;
      secretNumbers.add((int) (secretNumber % finalMod));
    }
    return secretNumbers;
  }

  /**
   * Shifts the given windowId to make room for the given delta at the most significant bit level.
   *
   * A window id is a unique identifier for four delta values.  Each delta is 8 bits of significance
   * with the oldest at the least significant position and newest at the most significant position.
   */
  private int shiftWindowId(int windowId, int delta) {
    return (windowId >>> 8)
         + ((delta & 0b1111_1111) << 24);
  }
}
