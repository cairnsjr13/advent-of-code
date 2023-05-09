package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;

/**
 * After tampering with the network we need to hide our tracks.  This involves generating randomish
 * data according to a pattern and then computing a checksum to verify that we did it correctly.
 */
class Day16 extends Base2016 {
  private static final ConfigToken<Integer> diskSize = ConfigToken.of("diskSize", Integer::parseInt);

  /**
   * Computes the checksum of the data needed to fill a disk.
   */
  @Override
  protected Object part1(Loader loader) {
    return getChecksumFromInitial(loader);
  }

  /**
   * Computes the checksum of the data needed to fill a disk.
   */
  @Override
  protected Object part2(Loader loader) {
    return getChecksumFromInitial(loader);
  }

  /**
   * Computes the final checksum of a filled disk of the configured size.
   */
  private CharSequence getChecksumFromInitial(Loader loader) {
    CharSequence data = getData(loader.sl(), loader.getConfig(diskSize));
    while (data.length() % 2 == 0) {
      data = computeChecksum(data);
    }
    return data;
  }

  /**
   * Computes the dragon curve data needed to fill the disk.  We take an iterative approach
   * to filling the data until we have enough, and then trim it to be the exact length.
   * Each iteration is computed as follows:
   *   - all of the old data
   *   - a zero
   *   - the old data reversed and flipped (1 => 0, 0 => 1)
   */
  private CharSequence getData(CharSequence data, int toFill) {
    while (data.length() < toFill) {
      StringBuilder newData = new StringBuilder(data);
      newData.append('0');
      for (int i = data.length() - 1; i >= 0; --i) {
        newData.append((data.charAt(i) == '0') ? '1' : '0');
      }
      data = newData;
    }
    return data.subSequence(0, toFill);
  }

  /**
   * Computes the checksum of the given data.
   * This is done by collapsing pairs of characters to '1' on match and '0' on mismatch.
   */
  private CharSequence computeChecksum(CharSequence data) {
    StringBuilder checksum = new StringBuilder();
    for (int i = 0; i < data.length(); i += 2) {
      checksum.append((data.charAt(i) == data.charAt(i + 1)) ? '1' : '0');
    }
    return checksum;
  }
}
