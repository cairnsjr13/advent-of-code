package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cairns.rich.aoc.SafeAccessor;
import com.google.common.base.Strings;

class KnotHash extends SafeAccessor {
  private KnotHash() { }
  
  static List<Integer> getLengthsFromString(String input) {
    List<Integer> lengths = input.chars().boxed().collect(Collectors.toList());
    lengths.addAll(Arrays.asList(17, 31, 73, 47, 23));
    return lengths;
  }
  
  static String getKnotHash(int size, List<Integer> lengths) {
    State state = new State(size);
    for (int i = 0; i < 64; ++i) {
      state.knot(lengths);
    }
    StringBuilder hexHash = new StringBuilder();
    for (int i = 0; i < 16; ++i) {
      int denseHash = 0;
      for (int j = 0; j < 16; ++j) {
        denseHash ^= state.list[i * 16 + j];
      }
      hexHash.append(Strings.padStart(Integer.toHexString(denseHash), 2, '0'));
    }
    return hexHash.toString();
  }
  
  private static void rev(int[] list, int current, int end) {
    for (; current < end; ++current, --end) {
      int oldCurrent = safeGet(list,current);
      safeSet(list, current, safeGet(list, end));
      safeSet(list, end, oldCurrent);
    }
  }
  
  static class State {
    final int[] list;
    private int current = 0;
    private int skip = 0;
    
    State(int size) {
      this.list = IntStream.range(0, size).toArray();
    }
    
    void knot(List<Integer> lengths) {
      for (int length : lengths) {
        rev(list, current, current + length - 1);
        current += length + skip;
        ++skip;
      }
    }
  }
}
