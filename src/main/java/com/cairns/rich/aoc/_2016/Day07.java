package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * After snooping the network, we need to figure out how many ips support tls and ssl.
 */
class Day07 extends Base2016 {
  /**
   * Computes the number of ipv7 addresses that support tls.
   * An address supports tls if if it has a sequence xyyx outside of brackets and not one inside.
   */
  @Override
  protected Object part1(Loader loader) {
    return countIpsThatSupport(loader, (line) -> {
      boolean inBrackets = false;
      boolean foundGood = false;
      for (int i = 0; i < line.length() - 3; ++i) {
        char chi = line.charAt(i);
        inBrackets = getUpdatedInBrackets(chi, inBrackets);
        char chj = line.charAt(i + 1);
        if ((chi != chj) && (chi == line.charAt(i + 3)) && (chj == line.charAt(i + 2))) {
          if (inBrackets) {
            return false;
          }
          foundGood = true;
        }
      }
      return foundGood;
    });
  }

  /**
   * Computes the number of ipv7 addresses that support ssl.
   * An address supports ssl if it has a sequence aba outside of square brackets and a sequence bab inside of square brackets.
   */
  @Override
  protected Object part2(Loader loader) {
    return countIpsThatSupport(loader, (line) -> {
      boolean inBrackets = false;
      Map<Boolean, Set<String>> inBracketsToSeen = Map.of(true, new HashSet<>(), false, new HashSet<>());
      for (int i = 0; i < line.length() - 2; ++i) {
        char chi = line.charAt(i);
        inBrackets = getUpdatedInBrackets(chi, inBrackets);
        char chj = line.charAt(i + 1);
        if ((chi != chj) && (chi == line.charAt(i + 2))) {
          String seen = chi + "" + chj;
          String need = chj + "" + chi;
          if (inBracketsToSeen.get(!inBrackets).contains(need)) {
            return true;
          }
          inBracketsToSeen.get(inBrackets).add(seen);
        }
      }
      return false;
    });
  }

  /**
   * Helper function to compute the number of ipv7 addresses that pass the given test.
   */
  private long countIpsThatSupport(Loader loader, Predicate<String> supports) {
    return loader.ml().stream().filter(supports).count();
  }

  /**
   * Computes the inBrackets state based on the given character and current inBrackets state.  A '[' or ']' will flip it.
   */
  private boolean getUpdatedInBrackets(char ch, boolean inBrackets) {
    return ((ch == '[') || (ch == ']')) ? !inBrackets : inBrackets;
  }
}
