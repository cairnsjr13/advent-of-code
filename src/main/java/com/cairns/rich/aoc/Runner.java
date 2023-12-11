package com.cairns.rich.aoc;

import com.cairns.rich.aoc._2023.Base2023;
import java.nio.file.Path;
import org.apache.commons.lang3.NotImplementedException;

/**
 * This class is used for day-of development.  The goal is to reduce barriers to finding the answer quickly.
 * Changing the loader between test/full is simple at this level.  It will also provide timing stats.
 */
final class Runner {
  private static final Path resourceFolderPath =
      Path.of("src/main/resources/" + Runner.class.getPackageName().replace('.', '/'));

  private Runner() { }

  public static void main(String[] args) throws Throwable {
    Base day = Base2023.day.get();
    Loader loader = new Loader(resourceFolderPath.resolve("full.txt"));
    //loader = new Loader(resourceFolderPath.resolve("test.txt"));

    long mark = System.currentTimeMillis();
    Object part1Answer = "NOT RUN";
    part1Answer = day.part1(loader);  // comment this line out to skip part 1
    System.out.println("part1: '" + part1Answer + "' - " + (System.currentTimeMillis() - mark) + "ms");

    try {
      mark = System.currentTimeMillis();
      Object part2Answer = day.part2(loader);
      System.out.println("part2: '" + part2Answer + "' - " + (System.currentTimeMillis() - mark) + "ms");
    }
    catch (NotImplementedException e) {
      System.out.println("part2 NOT IMPLEMENTED");
    }
  }
}
