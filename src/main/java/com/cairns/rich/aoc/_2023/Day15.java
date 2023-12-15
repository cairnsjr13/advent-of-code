package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * We need to follow an initialization sequence by focusing light through boxes with a bunch of lens.
 * A simple hash algorithm and table structure allows us to simulate everything to build a hashmap!
 */
class Day15 extends Base2023 {
  private static final int HASH_FACTOR = 17;
  private static final int HASH_RANGE = 256;

  /**
   * Computes the sum of the hash values of each instruction in the input.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.sl(",", this::hash).stream().mapToInt(Integer::intValue).sum();
  }

  /**
   * Computes the focusing power of the final lens configuration after following the input instructions.
   * Each box operates exactly like a {@link LinkedHashMap} and we can use that to handle removes and adds.
   */
  @Override
  protected Object part2(Loader loader) {
    List<LinkedHashMap<String, Integer>> boxes =
        IntStream.range(0, HASH_RANGE).mapToObj((i) -> new LinkedHashMap<String, Integer>()).collect(Collectors.toList());
    for (String inst : loader.sl(",")) {
      if (inst.charAt(inst.length() - 1) == '-') {
        String label = inst.substring(0, inst.length() - 1);
        boxes.get(hash(label)).remove(label);
      }
      else {
        String label = inst.substring(0, inst.indexOf('='));
        int focalLength = Integer.parseInt(inst.substring(label.length() + 1));
        boxes.get(hash(label)).put(label, focalLength);
      }
    }
    return computeFocusingPower(boxes);
  }

  /**
   * Returns the total focusing power of the boxes.  This is found by computing the sum of computing powers for each lens.
   * Each lens' focusing power is found by multiplying its (1 based) box number, its (1 based) lens position, and focal power.
   */
  private long computeFocusingPower(List<LinkedHashMap<String, Integer>> boxes) {
    long focusingPower = 0;
    int boxNum = 1;
    for (LinkedHashMap<String, Integer> box : boxes) {
      int lensPosition = 1;
      for (String label : box.keySet()) {
        focusingPower += (boxNum * lensPosition * box.get(label));
        ++lensPosition;
      }
      ++boxNum;
    }
    return focusingPower;
  }

  /**
   * Computes the hash code of the given string input which is guaranteed to be in the range [0, {@link #HASH_RANGE}).
   * This is done char by char by adding the char, multiplying by {@link #HASH_FACTOR} and then mod'ing by {@link #HASH_RANGE}.
   */
  private int hash(String input) {
    int current = 0;
    for (int i = 0; i < input.length(); ++i) {
      current = ((current + input.charAt(i)) * HASH_FACTOR) % HASH_RANGE;
    }
    return current;
  }
}
