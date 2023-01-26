package com.cairns.rich.aoc._2022;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Day06 extends Base2022 {
  @Override
  protected void run() throws Throwable {
    String input = fullLoader.sl();
    System.out.println(lastIndexOfUniqueWindow(input, 4));
    System.out.println(lastIndexOfUniqueWindow(input, 14));
  }
  
  private int lastIndexOfUniqueWindow(String input, int windowSize) {
    Multiset<Character> window = HashMultiset.create();
    input.substring(0, windowSize).chars().forEach((ch) -> window.add((char) ch));
    if (window.elementSet().size() == windowSize) {
      return windowSize;
    }
    for (int i = windowSize; i < input.length(); ++i) {
      window.remove(input.charAt(i - windowSize));
      window.add(input.charAt(i));
      if (window.elementSet().size() == windowSize) {
        return i + 1;
      }
    }
    throw fail("couldnt find unique window");
  }
}