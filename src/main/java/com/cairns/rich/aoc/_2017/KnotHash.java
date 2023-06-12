package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.SafeAccessor;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Centralization of the KnotHash protocol used in various days of this year.
 * It is meant as a new cryptography approach and is achieved in the following way:
 *   - start with a circular list from [0-255]
 *   - start with a current position of 0
 *   - start with a current skip of 0
 *   - input of lengths: for each ->
 *     - reverse the order of that length from the current position
 *     - increase the current position by the length PLUS the skip
 *     - increase the skip size by 1
 */
class KnotHash extends SafeAccessor {
  private static final List<Integer> lengthsSuffix = Arrays.asList(17, 31, 73, 47, 23);

  private KnotHash() { }

  /**
   * Computes the appropriate input lengths from the given string.  This is
   * done by converting each character to its ascii value as an integer.  The
   * returned list has the sequence (17, 31, 73, 47, 23) per the algorithm spec.
   */
  static List<Integer> getLengthsFromString(String input) {
    List<Integer> lengths = input.chars().boxed().collect(Collectors.toList());
    lengths.addAll(lengthsSuffix);
    return lengths;
  }

  /**
   * Computes the final dense knot hash for a list of given size and given input lengths.
   * This is done by:
   *   - Running the {@link State#knot(List)} algorithm 64 times
   *     - current position and skip distance is preserved across runs
   *   - The order of the [0-255] numers is considered the "sparse hash"
   *   - We compress this into a dense hash by xoring each block of 16 numbers together
   *     - This will result in 16 numbers between [0-255]
   *     - Convert these to (left-0-padded) 2 digit hex numbers
   *     - Combine into a 32 character string
   */
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

  /**
   * Container object tracking the various state of our algorithm as it runs.
   */
  static class State {
    final int[] list;
    private int current = 0;
    private int skip = 0;

    State(int size) {
      this.list = IntStream.range(0, size).toArray();
    }

    /**
     * Performs the {@link KnotHash} algorithm for the given input lengths.
     * The final result will be in the {@link #list} instance variable.
     */
    void knot(List<Integer> lengths) {
      for (int length : lengths) {
        rev(current, current + length - 1);
        current += length + skip;
        ++skip;
      }
    }

    /**
     * Helper function to reverse a range of the list.
     * The current and end params are INCLUSIVE.
     */
    private void rev(int current, int end) {
      for (; current < end; ++current, --end) {
        int oldCurrent = safeGet(list, current);
        safeSet(list, current, safeGet(list, end));
        safeSet(list, end, oldCurrent);
      }
    }
  }
}
