package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Santa has an expiring password problem.  He literally just increments his password until it is valid.
 * Validity is defined by a few preset rules.  Let's find a couple for him.
 */
class Day11 extends Base2015 {
  private static final Pattern allLegalPattern = Pattern.compile("^[^iol]+$");
  private static final Pattern twoPairsPattern = Pattern.compile("^.*(.)\\1.*(.)\\2.*$");

  /**
   * Finds the first valid password after the puzzle input.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    return findNextPassword(loader.sl());
  }

  /**
   * Finds the second valid password after the puzzle input.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    String next = findNextPassword(loader.sl());
    return findNextPassword(next);
  }

  /**
   * Computes the next valid password after the given input.
   * We do this by incrementing the password and running the validity checks.
   */
  private String findNextPassword(String input) {
    do {
      input = nextOption(input);
    }
    while (!isValid(input));
    return input;
  }

  /**
   * Finds the next potential password by incrementing it as a char array with the rightmost char
   * the least sig digit.  Skips over known failure characters (iol).  Rolls over to the next digit
   * when we increment up to '{' which is the character after 'z' (effectively 10 in base 10).
   */
  private String nextOption(String password) {
    char[] chs = password.toCharArray();
    for (int i = chs.length - 1; i >= 0; --i) {
      char curCh = chs[i];
      char newCh = (char) (curCh + 1);
      if ((newCh == 'i') || (newCh == 'o') || (newCh == 'l')) {
        chs[i] = (char) (newCh + 1);
        break;
      }
      else if (newCh == '{') {
        chs[i] = 'a';
      }
      else {
        chs[i] = newCh;
        break;
      }
    }
    return new String(chs);
  }

  /**
   * Checks that the given option is a valid password.  A valid password needs to follow 3 rules:
   *   1) cannot contain 'i' or 'o' or 'l'
   *   2) must include an increasing straight of 3 characters
   *   3) must include two non-overlapping pairs
   */
  private boolean isValid(String password) {
    Matcher allLegalMatcher = allLegalPattern.matcher(password);
    Matcher twoPairsMatcher = twoPairsPattern.matcher(password);
    if (!allLegalMatcher.matches() || !twoPairsMatcher.matches()) {
      return false;
    }
    for (int i = 0; i < password.length() - 2; ++i) {
      char ich = password.charAt(i);
      char jch = password.charAt(i + 1);
      char kch = password.charAt(i + 2);
      if ((ich + 1 == jch) && (jch + 1 == kch)) {
        return true;
      }
    }
    return false;
  }
}
