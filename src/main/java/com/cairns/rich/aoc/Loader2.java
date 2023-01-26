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
  public final String file;
  
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
   * Grouped parser that consumes all lines of input using the given function until exhausted.
   */
  public <G> List<G> g(Function<GroupParserData, G> toGroup) {
    List<G> groups = new ArrayList<>();
    GroupParserData groupParserData = new GroupParserData(ml());
    while (groupParserData.peek() != null) {
      groups.add(toGroup.apply(groupParserData));
    }
    return groups;
  }
  
  public List<List<String>> gDelim(String delim) {
    return gDelim(delim, Function.identity());
  }
  
  public <T> List<T> gDelim(String delim, Function<List<String>, T> map) {
    return g((gpd) -> {
      List<String> group = new ArrayList<>();
      while (gpd.peek() != null) {
        String line = gpd.next();
        if (delim.equals(line)) {
          break;
        }
        group.add(line);
      }
      return map.apply(group);
    });
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
  
  public static class GroupParserData {
    private final List<String> lines;
    private int position = 0;
    
    private GroupParserData(List<String> lines) {
      this.lines = lines;
    }
    
    public String peek() {
      return (lines.size() == position) ? null : lines.get(position);
    }
    
    public String next() {
      return (lines.size() == position) ? null : lines.get(position++);
    }
  }
}
