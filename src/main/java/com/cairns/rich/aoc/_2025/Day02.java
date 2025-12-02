package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.Range;
import java.util.function.Predicate;

/**
 * Invalid id patterns are spread throughout ranges that we must inspect/detect.
 * The rules for what constitutes an invalid id is different for each part however
 * the pattern remains the same: repeated characters.
 */
class Day02 extends Base2025 {
  /**
   * The rule for an invalid id is simply that the first half exactly matches the second half.
   */
  @Override
  protected Object part1(Loader loader) {
    return findInvalidsSum(
        loader,
        (rep) -> (rep.length() % 2 == 0) && isMadeUpOf(rep, rep.length() / 2)
    );
  }

  /**
   * The rule for an invalid id is that it is completely made up of a repeating sequence.
   * A sequence must only be considered if it can completely divide the id.
   */
  @Override
  protected Object part2(Loader loader) {
    return findInvalidsSum(loader, (rep) -> {
      int len = rep.length();
      for (int subLen = 1; subLen <= len / 2; ++subLen) {
        if (len % subLen != 0) {
          continue;
        }
        if (isMadeUpOf(rep, subLen)) {
          return true;
        }
      }
      return false;
    });
  }

  /**
   * Helper function to loop over all ranges and find ones that match the given invalid test.
   * The returned value is the sum of all invalid ids that lie within the ranges.
   */
  private long findInvalidsSum(Loader loader, Predicate<String> isInvalid) {
    long invalidsSum = 0;
    for (Range<Long> range : loader.sl(",", Day02::parse)) {
      for (long id = range.lowerEndpoint(); range.contains(id); ++id) {
        if (isInvalid.test(Long.toString(id))) {
          invalidsSum += id;
        }
      }
    }
    return invalidsSum;
  }

  /**
   * Returns true if the sequence of characters at the beginning of the id rep of given length
   * matches every following group of characters.  It is assumed that the rep's length is
   * perfectly divisible by the given subLen.  If any char does not match, false is returned.
   */
  private boolean isMadeUpOf(String rep, int subLen) {
    int repititions = rep.length() / subLen;
    for (int group = 1; group < repititions; ++ group) {
      for (int i = 0; i < subLen; ++i) {
        if (rep.charAt(group * subLen + i) != rep.charAt(i)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Each ranges is a closed range of two longs.  Uses standard long parsing split on a single dash.
   */
  private static Range<Long> parse(String spec) {
    String[] pieces = spec.split("-");
    return Range.closed(Long.parseLong(pieces[0]), Long.parseLong(pieces[1]));
  }
}
