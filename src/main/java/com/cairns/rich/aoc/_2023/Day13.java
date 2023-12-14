package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.List;
import java.util.function.IntFunction;

/**
 * We need to find the lines of reflections in a list of images.  The reflection lines can be either
 * horizontal or vertical and may be allowed to have 0 or 1 mismatches depending on the part.
 */
class Day13 extends Base2023 {
  /**
   * Returns the reflection summary for the images when we are looking for perfect lines of reflection.
   */
  @Override
  protected Object part1(Loader loader) {
    return getTotalReflectionValues(loader, 0);
  }

  /**
   * Returns the reflection summary for the images when we are looking for exactly one mismatch.
   */
  @Override
  protected Object part2(Loader loader) {
    return getTotalReflectionValues(loader, 1);
  }

  /**
   * Helper method to return the reflection summary for the images with the given number of mismatches.
   */
  private long getTotalReflectionValues(Loader loader, int expectedMismatches) {
    return loader.gDelim("").stream().mapToLong((image) -> getReflectionValue(image, expectedMismatches)).sum();
  }

  /**
   * Returns the reflection summary for the given image when expecting the given number of mismatches.
   * If the line is vertical, the summary is number of columns to the left of the line of reflection.
   * If the line is horizontal, the summary is 100 times the number of columns above the line of reflection.
   */
  private long getReflectionValue(List<String> image, int expectedMismatches) {
    for (int reflectAfterCol = 0; reflectAfterCol < image.get(0).length() - 1; ++reflectAfterCol) {
      int mismatches =
          sequenceMismatches(image, reflectAfterCol, image.get(0).length(), (c) -> new MutablePoint(c, 0), ReadDir.Down);
      if (expectedMismatches == mismatches) {
        return reflectAfterCol + 1;
      }
    }
    for (int reflectAfterRow = 0; reflectAfterRow < image.size() - 1; ++reflectAfterRow) {
      int mismatches =
          sequenceMismatches(image, reflectAfterRow, image.size(), (r) -> new MutablePoint(0, r), ReadDir.Right);
      if (expectedMismatches == mismatches) {
        return 100 * (reflectAfterRow + 1);
      }
    }
    throw fail("No reflection found");
  }

  /**
   * Finds the number of mismatches across the entire image with the given reflection line described
   * by the inputs.  The reflection line will be between the given afterI param and the next higher
   * index.  From their the comparison sequences are moved apart by one on each loop.
   */
  private int sequenceMismatches(List<String> image, int afterI, int limit, IntFunction<MutablePoint> toInit, ReadDir dir) {
    int mismatches = 0;
    for (int delta = 0; true; ++delta) {
      int lowI = afterI - delta;
      int highI = afterI + 1 + delta;
      if ((lowI < 0) || (limit <= highI)) {
        return mismatches;
      }
      MutablePoint low = toInit.apply(lowI);
      MutablePoint high = toInit.apply(highI);
      for (; high.y() < image.size() && high.x() < image.get(0).length(); low.move(dir), high.move(dir)) {
        if (image.get(low.y()).charAt(low.x()) != image.get(high.y()).charAt(high.x())) {
          ++mismatches;
        }
      }
    }
  }
}
