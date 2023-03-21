package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;

class Day08 extends Base2015 {
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Entry::new).stream().mapToInt((e) -> e.code.length() - e.memoryBytes()).sum();
  }

  @Override
  protected Object part2(Loader loader) {
    return loader.ml(Entry::new).stream().mapToInt((e) -> e.encodedCodeBytes() - e.code.length()).sum();
  }

  private static class Entry {
    private final String code;

    private Entry(String code) {
      this.code = code;
    }

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
