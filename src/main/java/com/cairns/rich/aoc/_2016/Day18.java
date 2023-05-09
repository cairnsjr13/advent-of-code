package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * Traps everywhere!  We need to cross the tiled room and not get hit with a trap.
 * Luckily each tile is based off the tiles touching it in the previous row.
 */
class Day18 extends Base2016 {
  private static final ConfigToken<Integer> numRowsToken = ConfigToken.of("numRows", Integer::parseInt);

  /**
   * Returns the number of safe tiles in a room derived from the given input.
   */
  @Override
  protected Object part1(Loader loader) {
    return getSafeCount(loader);
  }

  /**
   * Returns the number of safe tiles in a room derived from the given input.
   */
  @Override
  protected Object part2(Loader loader) {
    return getSafeCount(loader);
  }

  /**
   * Generates the room one row at a time (keeping just the previous one around)
   * and counts the number of safe tiles in the configured number of rows.
   */
  private long getSafeCount(Loader loader) {
    String init = loader.sl();
    BitSet initRow = new BitSet();
    IntStream.range(0, init.length()).filter((i) -> init.charAt(i) == '^').map(this::toIndex).forEach(initRow::set);
    int numRows = loader.getConfig(numRowsToken);

    BitSet previousRow = initRow;
    BitSet currentRow = new BitSet();
    long numSafe = init.length() - previousRow.cardinality();

    for (int i = 1; i < numRows; ++i) {
      for (int col = 0; col < init.length(); ++col) {
        boolean isTrap = isCurrentTrap(previousRow, col);
        currentRow.set(toIndex(col), isTrap);
        if (!isTrap) {
          ++numSafe;
        }
      }
      BitSet swap = previousRow;
      previousRow = currentRow;
      currentRow = swap;
    }
    return numSafe;
  }

  /**
   * Returns true if the column in the current row should be a trap based on the previous row.
   */
  private boolean isCurrentTrap(BitSet previousRow, int col) {
    boolean left = previousRow.get(toIndex(col - 1));
    boolean middle = previousRow.get(toIndex(col + 0));
    boolean right = previousRow.get(toIndex(col + 1));
    return (left && middle && !right)
         | (!left && middle && right)
         | (left && !middle && !right)
         | (!left && !middle && right);
  }

  /**
   * Returns the {@link BitSet} index that should be used for a column index.
   * Since the {@link BitSet}s have a dummy col at position 0, accesses must be +1.
   */
  private int toIndex(int col) {
    return col + 1;
  }
}
