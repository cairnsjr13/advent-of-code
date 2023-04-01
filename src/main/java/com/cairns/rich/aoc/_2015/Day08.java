package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;

/**
 * Santa needs to know the length of his list based on special character encoding.
 */
class Day08 extends Base2015 {
  /**
   * Finds the sum of each line's bytes for code string literals minus the sum of each line's memory usage.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Entry::new).stream().mapToInt((e) -> e.code.length() - e.memoryBytes()).sum();
  }

  /**
   * Finds the sum of each lines
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    return loader.ml(Entry::new).stream().mapToInt((e) -> e.encodedCodeBytes() - e.code.length()).sum();
  }

  /**
   * Input class that represents a line in the list as well as a way to compute
   * its memory bytes and its encoded code bytes (the two parts of the day).
   */
  private static class Entry {
    private final String code;

    private Entry(String code) {
      this.code = code;
    }

    /**
     * The memory size of a line is computed by seeing how many final bytes are needed after decoding.
     * If an inspected character is:
     *   - a backslash, followed by:
     *     - another backslash: skip both backslashes and add a byte to the memory bytes
     *     - a quote: skip the backslash and the quote and add a byte to the memory bytes
     *     - an x: skip the backslash, the x, and the two hex chars add a byte to the memory bytes
     *   - anything else: add a byte to the memory bytes
     * The total count starts at -2 to remove the enclosing quotes.
     */
    private int memoryBytes() {
      int memoryBytes = -2;
      for (int i = 0; i < code.length();) {
        if (code.charAt(i) == '\\') {
          switch (code.charAt(i + 1)) {
            case '\\':
            case '"':
              i += 2;
              break;
            case 'x':
              i += 4;
              break;
            default:
              throw new RuntimeException(code);
          }
        } else {
          ++i;
        }
        ++memoryBytes;
      }
      return memoryBytes;
    }

    /**
     * The encoded code bytes of a line is computed by seeing how many bytes would been needed to encode it as code.
     * Each character counts as one except the quote and backslash which count as two.
     * We need to also add two to the final number for the enclosing quotes.
     */
    private int encodedCodeBytes() {
      int encodedCodeBytes = 0;
      for (int i = 0; i < code.length(); ++i) {
        if ((code.charAt(i) == '"') || (code.charAt(i) == '\\')) {
          encodedCodeBytes += 2;
        } else {
          ++encodedCodeBytes;
        }
      }
      return 2 + encodedCodeBytes;
    }
  }
}
