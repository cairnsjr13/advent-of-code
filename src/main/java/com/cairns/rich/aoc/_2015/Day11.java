package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day11 extends Base2015 {
  private static final Pattern allLegalPattern = Pattern.compile("^[^iol]+$");
  private static final Pattern twoPairsPattern = Pattern.compile("^.*(.)\\1.*(.)\\2.*$");

  @Override
  protected Object part1(Loader loader) {
    return findNextPassword(loader.sl());
  }

  @Override
  protected Object part2(Loader loader) {
    String next = findNextPassword(loader.sl());
    return findNextPassword(next);
  }

  private String findNextPassword(String input) {
    do {
      input = nextOption(input);
    }
    while (!isValid(input));
    return input;
  }

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
