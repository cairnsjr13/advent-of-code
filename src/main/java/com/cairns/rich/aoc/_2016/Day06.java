package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Santa's message is being jammed and we need to do some error correcting.
 */
class Day06 extends Base2016 {
  /**
   * Decodes the input message by finding the character in each position that is most frequent.
   */
  @Override
  protected Object part1(Loader loader) {
    return decode(loader, Base::getMax);
  }

  /**
   * Decodes the input message by finding the character in each position that is least frequent.
   */
  @Override
  protected Object part2(Loader loader) {
    return decode(loader, Base::getMin);
  }

  /**
   * Decodes the message by examining each position and picking the best (by using the given function) character from the input.
   */
  private String decode(Loader loader, BiFunction<Iterable<Character>, Function<Character, Integer>, Character> toBest) {
    List<String> lines = loader.ml();
    StringBuilder str = new StringBuilder();
    int numChars = lines.get(0).length();
    for (int i = 0; i < numChars; ++i) {
      Multiset<Character> chars = HashMultiset.create();
      for (String line : lines) {
        chars.add(line.charAt(i));
      }
      str.append(toBest.apply(chars.elementSet(), chars::count));
    }
    return str.toString();
  }
}
