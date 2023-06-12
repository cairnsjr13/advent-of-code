package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

/**
 * A new passphrase system is in place and we have various rules we must apply and then see how many passphrases are valid.
 */
class Day04 extends Base2017 {
  /**
   * Returns the count of passphrases that contain no duplicate words.
   */
  @Override
  protected Object part1(Loader loader) {
    return countValids(loader, Function.identity());
  }

  /**
   * Returns the count of passphrases that contain no duplicate words when their characters are sorted.
   * This is effectively checking if each word is an anagram of the others.
   */
  @Override
  protected Object part2(Loader loader) {
    return countValids(loader, (word) -> {
      char[] chs = word.toCharArray();
      Arrays.sort(chs);
      return new String(chs);
    });
  }

  /**
   * Counts the number of valid passphrases (no duplicate words) after applying the given transformation.
   */
  private long countValids(Loader loader, Function<String, String> transform) {
    return loader.ml((line) -> line.split(" +")).stream()
        .filter((passphrase) -> Arrays.stream(passphrase).map(transform).allMatch((new HashSet<>())::add))
        .count();
  }
}
