package com.cairns.rich.aoc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This api makes reading puzzle input from a file less verbose.  File handling and parsing are done at this level.
 */
public class Loader2 extends QuietCapable {
  private final String file;
  
  public Loader2(String file) {
    this.file = file;
  }
  
  /**
   * Multi-line parser that returns each line verbatim.
   */
  public List<String> ml() {
    return ml(Function.identity());
  }
  
  /**
   * Multi-line parser that transforms each line using the given map fn.
   */
  public <T> List<T> ml(Function<String, T> map) {
    return load(map);
  }
  
  /**
   * Single line parser that returns the first line verbatim.
   */
  public String sl() {
    return ml().get(0);
  }
  
  /**
   * Single line parser that splits the first line using the given regex and returns them verbatim.
   */
  public List<String> sl(String sepRegex) {
    return sl(sepRegex, Function.identity());
  }
  
  /**
   * Single line parser that splits the first line using the given regex and transforms each using the given map fn.
   */
  public <T> List<T> sl(String sepRegex, Function<String, T> map) {
    return Arrays.stream(ml().get(0).split(sepRegex)).map(map).collect(Collectors.toList());
  }
  
  /**
   * Helper method that reads the entire file, transforms each line using the given map fn, and returns them in a list.
   */
  private <T> List<T> load(Function<String, T> map) {
    return quietly(() -> {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)))) {
        List<T> input = new ArrayList<>();
        while (true) {
          String line = in.readLine();
          if (line == null) {
            return input;
          }
          input.add(map.apply(line));
        }
      }
    });
  }
}
