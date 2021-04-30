package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class Day16 extends Base2017 {
  @Override
  protected void run() {
    List<Consumer<StringBuilder>> moves = Arrays.stream(fullLoader.ml().get(0).split(",")).map(this::parse).collect(Collectors.toList());
    StringBuilder state = new StringBuilder("abcdefghijklmnop");
    moves.forEach((move) -> move.accept(state));
    System.out.println(state);
    
    int loopSize = getLoopSize(moves, state);
    for (int i = 1; i < 1_000_000_000 % loopSize; ++i) {
      moves.forEach((move) -> move.accept(state));
    }
    System.out.println(state);
  }
  
  private int getLoopSize(List<Consumer<StringBuilder>> moves, StringBuilder state) {
    Set<String> seen = new HashSet<>();
    seen.add(state.toString());
    while (true) {
      moves.forEach((move) -> move.accept(state));
      if (!seen.add(state.toString())) {
        return seen.size();
      }
    }
  }
  
  private Consumer<StringBuilder> parse(String spec) {
    switch (spec.charAt(0)) {
      case 's' : return (state) -> {
        int spinLength = Integer.parseInt(spec.substring(1));
        int tailIndex = state.length() - spinLength;
        String tail = state.substring(tailIndex);
        state.replace(tailIndex, state.length(), "");
        state.insert(0, tail);
      };
      case 'x' : return (state) -> {
        int indexOfSlash = spec.indexOf("/");
        int first = Integer.parseInt(spec.substring(1, indexOfSlash));
        int second = Integer.parseInt(spec.substring(indexOfSlash + 1));
        char firstCh = state.charAt(first);
        state.setCharAt(first, state.charAt(second));
        state.setCharAt(second, firstCh);
      };
      case 'p' : return (state) -> {
        String first = spec.substring(1, 2);
        String second = spec.substring(3, 4);
        int firstI = state.indexOf(first);
        int secondI = state.indexOf(second);
        state.setCharAt(firstI, second.charAt(0));
        state.setCharAt(secondI, first.charAt(0));
      };
    }
    throw fail(spec);
  }
}
