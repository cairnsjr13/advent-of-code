package com.cairns.rich.aoc._2016;

import java.util.TreeMap;
import java.util.stream.Collectors;

class Day05 extends Base2016 {
  @Override
  protected void run() {
    String input = "cxdnnyjw";
    System.out.println(computeSimple(input));
    System.out.println(computeComplex(input));
  }
  
  private String computeSimple(String seed) {
    StringBuilder str =  new StringBuilder();
    State state = new State();
    for (int i = 0; i < 8; ++i) {
      str.append(nextHash(seed, state).charAt(5));
    }
    return str.toString();
  }
  
  private String computeComplex(String seed) {
    TreeMap<Integer, Character> positions = new TreeMap<>();
    State state = new State();
    while (positions.size() != 8) {
      String hash = nextHash(seed, state);
      int position = hash.charAt(5) - '0';
      if ((0 <= position) && (position < 8)) {
        positions.putIfAbsent(position, hash.charAt(6));
      }
    }
    return positions.values().stream().map(Object::toString).collect(Collectors.joining());
  }
  
  private String nextHash(String seed, State state) {
    return quietly(() -> {
      while (true) {
        ++state.i;
        String hash = md5(seed + state.i);
        if (hash.startsWith("00000")) {
          return hash;
        }
      }
    });
  }
  
  private static class State {
    private int i = 0;
  }
}
