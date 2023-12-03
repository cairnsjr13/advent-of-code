package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * We have a schematic of parts and gears to fix and then compute information.
 */
public class Day03 extends Base2023 {
  /**
   * Computes the sum of all part numbers of parts that touch a symbol (non '.' or digit).
   */
  @Override
  protected Object part1(Loader loader) {
    return Part.findAll(loader.ml()).stream()
        .filter((p) -> p.symbol.glyph != '.')
        .mapToLong((p) -> p.number)
        .sum();
  }

  /**
   * Computes the sum of all gear ratios in the schematic.  Gears are symbols that are '*' and attach exactly two parts.
   */
  @Override
  protected Object part2(Loader loader) {
    Multimap<Symbol, Part> symbolToParts = HashMultimap.create();
    Part.findAll(loader.ml()).forEach((p) -> symbolToParts.put(p.symbol, p));
    return symbolToParts.keySet().stream()
        .filter((s) -> s.glyph == '*')
        .map(symbolToParts::get)
        .filter((ps) -> ps.size() == 2)
        .mapToLong((ps) -> ps.stream().mapToLong((p) -> p.number).reduce(Math::multiplyExact).getAsLong())
        .sum();
  }

  /**
   * Descriptor class for a part on the schematic.  This is specified a horizontal string
   * of digits, which form a part number.  A part can also touch a {@link Symbol} which
   * needs to be kept track of.  The {@link #chWidth} field should only be used for parsing.
   */
  private static class Part {
    private final int number;
    private final int chWidth;
    private final Symbol symbol;

    /**
     * Helper method to parse through the schematic and find all parts.
     */
    private static List<Part> findAll(List<String> lines) {
      List<Part> parts = new ArrayList<>();
      int width = lines.get(0).length();
      for (int y = 0; y < lines.size(); ++y) {
        String line = lines.get(y);
        for (int x = 0; x < width; ) {
          char ch = line.charAt(x);
          if (Character.isDigit(ch)) {
            Part part = new Part(lines, y, x);
            parts.add(part);
            x += part.chWidth;
          }
          else {
            ++x;
          }
        }
      }
      return parts;
    }

    private Part(List<String> lines, int y, int start) {
      String line = lines.get(y);
      int length = 1;
      while ((start + length < line.length()) && Character.isDigit(line.charAt(start + length))) {
        ++length;
      }
      this.number = Integer.parseInt(line.substring(start, start + length));
      this.chWidth = length;
      this.symbol = Symbol.of(lines, y, start, length);
    }
  }

  /**
   * Descriptor class for a symbol on the schematic.  This is specified by a character that isn't a decimal and isn't a digit.
   */
  private static final class Symbol {
    private static final Symbol non = new Symbol(List.of("."), 0, 0);

    private final char glyph;
    private final ImmutablePoint location;

    /**
     * Helper parsing method to find the symbol that is attached (possibly diagonal) to the part whose
     * information is passed in.  If the part has no symbol attached to it, {@link #non} will be returned.
     */
    private static Symbol of(List<String> lines, int y, int start, int length) {
      for (int dy = -1; dy <= 1; dy += 2) {
        int inspectY = y + dy;
        for (int x = start - 1; x < start + length + 1; ++x) {
          if (isGlyph(lines, x, inspectY)) {
            return new Symbol(lines, x, inspectY);
          }
        }
      }
      if (isGlyph(lines, start - 1, y)) {
        return new Symbol(lines, start - 1, y);
      }
      if (isGlyph(lines, start + length, y)) {
        return new Symbol(lines, start + length, y);
      }
      return non;
    }

    private Symbol(List<String> lines, int x, int y) {
      this.glyph = lines.get(y).charAt(x);
      this.location = new ImmutablePoint(x, y);
    }

    @Override
    public boolean equals(Object other) {
      return (glyph == ((Symbol) other).glyph)
          && Objects.equals(location, ((Symbol) other).location);
    }

    @Override
    public int hashCode() {
      return (31 * glyph) + Objects.hashCode(location);
    }

    /**
     * Returns true if the position is a valid location on the map and is a glyph (not a '.' and not a digit).
     */
    private static boolean isGlyph(List<String> lines, int x, int y) {
      if ((0 <= y) && (y < lines.size()) && (0 <= x) && (x < lines.get(y).length())) {
        char ch = lines.get(y).charAt(x);
        return (ch != '.') && !Character.isDigit(ch);
      }
      return false;
    }
  }
}
