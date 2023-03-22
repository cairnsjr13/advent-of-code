package com.cairns.rich.aoc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This api makes reading puzzle input from a file less verbose.  File handling and parsing are done at this level.
 */
public class Loader extends QuietCapable {
  private static final String DELIM = "-------AdventOfCodeTestDelimiter-------";

  final String expected;
  private final Map<String, String> configRawStrings = new HashMap<>();
  private final List<String> lines;

  /**
   * Pre-loads the given path at construction time (load once instead of each usage).
   * The file given should contain 3 sections separated by a single {@link #DELIM} line:
   *   1) optional expected value
   *   2) optional {@link ConfigToken} bindings (key=value)
   *   3) required input lines
   */
  public Loader(Path path) {
    List<String> lines = quietly(() -> Files.readAllLines(path));
    int indexOfDelim = lines.indexOf(DELIM);
    if (indexOfDelim == -1) {
      this.expected = null;
      this.lines = lines;
    }
    else {
      this.expected = lines.subList(0, indexOfDelim).stream().collect(Collectors.joining("\n"));
      int indexOfLastDelim = lines.lastIndexOf(DELIM);
      if (indexOfDelim != indexOfLastDelim) {
        lines.subList(indexOfDelim + 1, indexOfLastDelim).stream()
            .map((line) -> line.split("=", 2))
            .forEach((part) -> configRawStrings.put(part[0], part[1]));
      }
      this.lines = lines.subList(indexOfLastDelim + 1, lines.size());
    }
  }

  /**
   * Returns the config value associated with the given token.
   * Will parse each time.
   */
  public <T> T getConfig(ConfigToken<T> token) {
    return token.parser.apply(configRawStrings.get(token.key));
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
    return lines.stream().map(map).collect(Collectors.toList());
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

  /**
   * Represents a strongly typed configuration value for a given day/part.
   * This is necessary because sometimes examples have different parameters that are not necessarily part of the input.
   */
  public static class ConfigToken<T> {
    private final String key;
    private final Function<String, T> parser;

    private ConfigToken(String key, Function<String, T> parser) {
      this.key = key;
      this.parser = parser;
    }

    /**
     * Convenience factory method to make creation simpler.
     */
    public static <T> ConfigToken<T> of(String key, Function<String, T> parser) {
      return new ConfigToken<>(key, parser);
    }
  }
}
