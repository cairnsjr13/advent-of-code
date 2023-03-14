package com.cairns.rich.aoc._2022;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Day07 extends Base2022 {
  @Override
  protected void run() {
    List<String[]> lines = fullLoader.ml((line) -> line.split(" +"));
    Directory top = buildFS(lines);

    System.out.println(getSumOfDirsMaxedSize(100_000, top));
    long spaceNeeded = top.getSize() - 40_000_000;  // 70M total, need 30M
    System.out.println(findMinimumDeleteSize(spaceNeeded, top));
  }

  private Directory buildFS(List<String[]> lines) {
    Directory top = new Directory(null);
    top.getSubdir("/");
    Directory current = top;
    for (String[] line : lines) {
      if ("$".equals(line[0])) {
        if ("cd".equals(line[1])) {
          if ("..".equals(line[2])) {
            current = current.parent;
            continue;
          }
          else if ("/".equals(line[2])) {
            current = top;
          }
          current = current.getSubdir(line[2]);
        }
        else if (!"ls".equals(line[1])) {
          throw fail("Invalid line - " + Arrays.toString(line));
        }
      }
      else if ("dir".equals(line[0])) {
        current.getSubdir(line[1]);
      }
      else {
        current.files.put(line[1], Long.parseLong(line[0]));
      }
    }
    return top;
  }

  private long getSumOfDirsMaxedSize(long threshold, Directory from) {
    if (from == null) {
      return 0;
    }
    return ((from.getSize() <= threshold) ? from.getSize() : 0)
         + from.subdirs.values().stream().mapToLong((subDir) -> getSumOfDirsMaxedSize(threshold, subDir)).sum();
  }

  private long findMinimumDeleteSize(long spaceNeeded, Directory from) {
    if (from.getSize() < spaceNeeded) {
      return Long.MAX_VALUE;
    }
    return Math.min(
        from.getSize(),
        from.subdirs.values().stream().mapToLong((subDir) -> findMinimumDeleteSize(spaceNeeded, subDir))
            .min().orElse(Long.MAX_VALUE)
    );
  }

  private static class Directory {
    private final Directory parent;
    private final Map<String, Directory> subdirs = new HashMap<>();
    private final Map<String, Long> files = new HashMap<>();
    private long size = -1;

    private Directory(Directory parent) {
      this.parent = parent;
    }

    private Directory getSubdir(String name) {
      return subdirs.computeIfAbsent(name, (i) -> new Directory(this));
    }

    private long getSize() {
      if (size == -1) {
        size = subdirs.values().stream().mapToLong(Directory::getSize).sum()
             + files.values().stream().mapToLong(Long::longValue).sum();
      }
      return size;
    }
  }
}
