package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We found a very large file that needs decompressing.  Let's see how large it is.
 */
class Day09 extends Base2016 {
  private static final Pattern pattern = Pattern.compile("^(\\((\\d+)x(\\d+)\\)).*$");

  /**
   * Computes the decompressed length of the file with no recursion.
   */
  @Override
  protected Object part1(Loader loader) {
    return getDecompressedLength(loader.sl(), false);
  }

  /**
   * Computes the decompressed length of the file with recursion.
   */
  @Override
  protected Object part2(Loader loader) {
    return getDecompressedLength(loader.sl(), true);
  }

  /**
   * Returns the decompressed length of the input file with recursion based on the flag.
   * Decompression is done by using markers, which are contained in parenthesis.  A marker
   * contains 2 pieces: a number of characters and a number of repetitions.  These pieces
   * are separated by an x.  A marker indicates that the given number of characters after
   * the marker should be repeated in the output the given number of times.  The markers
   * may or may not be processed recursively based on the passed in flag.  Note that we
   * dont actually need to compute the output here, just the length (memory optimization).
   */
  private long getDecompressedLength(String input, boolean recursive) {
    long length = 0;
    for (int i = 0; i < input.length(); ++i) {
      char ch = input.charAt(i);
      if (ch == '(') {
        Matcher matcher = pattern.matcher(input.substring(i));
        if (matcher.matches()) {
          int strLength = matcher.group(1).length();
          int numChars = Integer.parseInt(matcher.group(2));
          int numCopies = Integer.parseInt(matcher.group(3));
          length += numCopies * ((recursive)
              ? getDecompressedLength(input.substring(i + strLength, i + strLength + numChars), true)
              : numChars);
          i += strLength + numChars - 1;
        }
        else {
          ++length;
        }
      }
      else {
        ++length;
      }
    }
    return length;
  }
}
