package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.List;

class Day08 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<Entry> entries = loader.ml(Entry::new);
    result.part1(entries.stream().mapToInt(Entry::computePart1Value).sum());
    result.part2(entries.stream().mapToInt(Entry::computePart2Value).sum());
  }

  private static class Entry {
    private final String code;

    private Entry(String code) {
      this.code = code;
    }

    private int computePart1Value() {
      return code.length() - memoryBytes();
    }

    private int computePart2Value() {
      return encodedCodeBytes() - code.length();
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
