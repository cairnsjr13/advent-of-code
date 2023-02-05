package com.cairns.rich.aoc._2016;

import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.mutable.MutableInt;

class Day05 extends Base2016 {
  @Override
  protected void run() {
    String input = "cxdnnyjw";
    System.out.println(computeSimple(input));
    System.out.println(computeComplex(input));
  }

  private String computeSimple(String seed) {
    StringBuilder str =  new StringBuilder();
    MutableInt state = new MutableInt();
    for (int i = 0; i < 8; ++i) {
      str.append(nextHash(seed, state).charAt(5));
    }
    return str.toString();
  }

  private String computeComplex(String seed) {
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

  private String nextHash(String seed, MutableInt state) {
    return quietly(() -> {
      while (true) {
        state.increment();
        String hash = md5(seed + state.getValue());
        if (hash.startsWith("00000")) {
          return hash;
        }
      }
    });
  }
}
