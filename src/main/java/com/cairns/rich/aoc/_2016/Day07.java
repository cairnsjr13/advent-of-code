package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.HashSet;
import java.util.Set;

class Day07 extends Base2016 {
  @Override
  protected Object part1(Loader loader) {
    return loader.ml().stream().filter(this::supportsTls).count();
  }

  @Override
  protected Object part2(Loader loader) {
    return loader.ml().stream().filter(this::supportsSsl).count();
  }

  private boolean supportsTls(String line) {
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
  }

  private boolean supportsSsl(String line) {
    boolean inBrackets = false;
    Set<String> abasSeen = new HashSet<>();
    Set<String> babsSeen = new HashSet<>();
    for (int i = 0; i < line.length() - 2; ++i) {
      char chi = line.charAt(i);
      char chj = line.charAt(i + 1);
      inBrackets = getUpdatedInBrackets(chi, inBrackets);
      if ((chi == line.charAt(i + 2)) && (chi != chj)) {
        String seen = chi + "" + chj;
        String need = chj + "" + chi;
        if (inBrackets) {
          if (abasSeen.contains(need)) {
            return true;
          }
          babsSeen.add(seen);
        }
        else {
          if (babsSeen.contains(need)) {
            return true;
          }
          abasSeen.add(seen);
        }
      }
    }
    return false;
  }

  private boolean getUpdatedInBrackets(char ch, boolean inBrackets) {
    if ((ch == '[') || (ch == ']')) {
      inBrackets = !inBrackets;
    }
    return inBrackets;
  }
}
