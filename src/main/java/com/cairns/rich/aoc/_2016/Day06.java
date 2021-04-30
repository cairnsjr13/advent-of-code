package com.cairns.rich.aoc._2016;

import java.util.List;
import java.util.function.BiPredicate;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

class Day06 extends Base2016 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    System.out.println(decode(lines, (l, r) -> l > r));
    System.out.println(decode(lines, (l, r) -> l < r));
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
