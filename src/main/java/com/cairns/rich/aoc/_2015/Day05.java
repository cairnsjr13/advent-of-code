package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Santa needs to find naughty and nice strings.  We need to apply rules and count.
 */
class Day05 extends Base2015 {
  private static final Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
  private static final Set<String> illegals = Set.of("ab", "cd", "pq", "xy");
  private static final Pattern twoCharRepeat = Pattern.compile("^.*(..).*\\1.*$");
  private static final Pattern oneCharRepeat = Pattern.compile("^.*(.).\\1.*$");

  /**
   * The number of nice strings with the original rules.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml().stream().filter(this::isOriginallyNice).count();
  }

  /**
   * The number of nice strings with the new rules.
   */
  @Override
  protected Object part2(Loader loader) {
    return loader.ml().stream().filter(this::isNewNice).count();
  }

  /**
   * The original rules for being nice are:
   *   1) contains at least 3 vowels (aeiou only)
   *   2) contains at least one letter that appears twice in a row
   *   3) does not contain any of {@link #illegals}
   */
  private boolean isOriginallyNice(String input) {
    int numVowels = (vowels.contains(input.charAt(0))) ? 1 : 0;
    boolean hasRepeat = false;
    for (int pi = 0, ci = 1; ci < input.length(); ++pi, ++ci) {
      char pCh = input.charAt(pi);
      char cCh = input.charAt(ci);
      if (vowels.contains(cCh)) {
        ++numVowels;
      }
      if (pCh == cCh) {
        hasRepeat = true;
      }
      if (illegals.contains(pCh + "" + cCh)) {
        return false;
      }
    }
    return (numVowels >= 3) && hasRepeat;
  }

  /**
   * The new rules for being nice are (regexes will help):
   *   1) contains pair of letters that appear twice in a string without overlapping
   *   2) contains at least one letter that repeats with exactly one letter between the instances
   */
  private boolean isNewNice(String input) {
    Matcher twoCharMatcher = twoCharRepeat.matcher(input);
    Matcher oneCharMatcher = oneCharRepeat.matcher(input);
    return twoCharMatcher.matches() && oneCharMatcher.matches();
  }
}
