package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * We need to do some password cracking.  Leading zeros from md5 to find the next character.
 */
class Day05 extends Base2016 {
  /**
   * Creates an 8 character password by finding progressively next valid hashes
   * (5 leading zeros) and using the next (6th) character as the next password character.
   */
  @Override
  protected Object part1(Loader loader) {
    String seed = loader.sl();
    StringBuilder str =  new StringBuilder();
    MutableInt state = new MutableInt();
    for (int i = 0; i < 8; ++i) {
      str.append(nextHash(seed, state).charAt(5));
    }
    return str;
  }

  /**
   * Creates an 8 character password by finding progressively next valid hashes (5 leading zeros) and using
   * the 6th character as a position and the 7th character as the next password character.  Invalid positions
   * (6th) will lead to the hash being disallowed.  Only the first character at each position is considered.
   */
  @Override
  protected Object part2(Loader loader) {
    String seed = loader.sl();
    TreeMap<Integer, Character> positions = new TreeMap<>();
    MutableInt state = new MutableInt();
    while (positions.size() != 8) {
      String hash = nextHash(seed, state);
      int position = hash.charAt(5) - '0';
      if ((0 <= position) && (position < 8)) {
        positions.putIfAbsent(position, hash.charAt(6));
      }
    }
    return positions.values().stream().map(Object::toString).collect(Collectors.joining());
  }

  /**
   * Computes the next hash by incrementing the given state until a hash is found with five leading zeros.
   */
  private String nextHash(String seed, MutableInt state) {
    while (true) {
      state.increment();
      String hash = md5(seed + state.getValue());
      if (hash.startsWith("00000")) {
        return hash;
      }
    }
  }
}
