package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.List;
import java.util.function.BiPredicate;

class Day06 extends Base2016 {
  @Override
  protected Object part1(Loader2 loader) {
    return decode(loader.ml(), (l, r) -> l > r);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return decode(loader.ml(), (l, r) -> l < r);
  }

  private String decode(List<String> lines, BiPredicate<Integer, Integer> better) {
    StringBuilder str = new StringBuilder();
    int numChars = lines.get(0).length();
    for (int i = 0; i < numChars; ++i) {
      Multiset<Character> chars = HashMultiset.create();
      for (String line : lines) {
        chars.add(line.charAt(i));
      }
      char maxCh = lines.get(0).charAt(0);
      for (char ch : chars) {
        if (better.test(chars.count(ch), chars.count(maxCh))) {
          maxCh = ch;
        }
      }
      str.append(maxCh);
    }
    return str.toString();
  }
}
