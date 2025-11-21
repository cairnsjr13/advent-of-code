package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * To unlock the chief historian's door, locks and keys must be lined up.
 * Tumblers and pins are represented by heights and overlaps are not allowed.
 */
class Day25 extends Base2024 {
  /**
   * Computes the number of key/lock combinations that would work.  A combination works
   * if every column has height bounded by the max height detected (no overlap).
   */
  @Override
  protected Object part1(Loader loader) {
    List<List<String>> schemas = loader.gDelim("");
    int maxHeight = schemas.get(0).size() - 2;
    Function<Character, List<int[]>> parser = (indicator) ->
        schemas.stream().filter((s) -> s.get(0).charAt(0) == indicator).map(this::parse).collect(Collectors.toList());
    List<int[]> keys = parser.apply('.');
    List<int[]> locks = parser.apply('#');
    return keys.stream().mapToLong((key) -> locks.stream().filter(
        (lock) -> IntStream.range(0, key.length).allMatch((col) -> key[col] + lock[col] <= maxHeight)
    ).count()).sum();
  }

  /**
   * Computes a height array from the given input lock/key schema.  The height is just the number of '#'
   * in each column minus one.  THe reduction of 1 is to remove the indicator row specifying lock or key.
   */
  private int[] parse(List<String> schema) {
    int[] heights = new int[schema.get(0).length()];
    for (int col = 0; col < heights.length; ++col) {
      for (int row = 0; row < schema.size(); ++row) {
        if (schema.get(row).charAt(col) == '#') {
          ++heights[col];
        }
      }
      --heights[col];
    }
    return heights;
  }
}
