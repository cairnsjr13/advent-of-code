package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We are going to be turning lights on and off in a grid to display a decoration.
 * After applying all of the instructions from santa, we can return the total brightness.
 */
class Day06 extends Base2015 {
  private static final Map<Type, IntUnaryOperator> simple = Map.of(
      Type.On, (orig) -> 1,
      Type.Off, (orig) -> 0,
      Type.Toggle, (orig) -> 1 - orig
  );
  private static final Map<Type, IntUnaryOperator> complex = Map.of(
      Type.On, (orig) -> orig + 1,
      Type.Off, (orig) -> Math.max(0, orig - 1),
      Type.Toggle, (orig) -> orig + 2
  );

  /**
   * Sums the total brightness in the grid using the simple rules.
   *   on means turn the lights on
   *   off means turn the lights off
   *   toggle means flip the light from on/off
   */
  @Override
  protected Object part1(Loader loader) {
    return countLightsOn(loader.ml(Instruction::new), simple);
  }

  /**
   * Sums the total brightness in the grid using the complex rules.
   *   on means increase brightness by 1
   *   off means decrease brightness by 1 (minimum 0)
   *   toggle means increase brightness by 2
   */
  @Override
  protected Object part2(Loader loader) {
    return countLightsOn(loader.ml(Instruction::new), complex);
  }

  /**
   * Sums up all of the final brightness values for each bulb after all of the instructions.
   * We brute force it here with O(w*h*n) where w is the width, h is the height, and n is the number of instructions.
   * Each bulb will have all of the instructions that it falls into applied and then summed to the total.
   */
  private final int countLightsOn(List<Instruction> instructions, Map<Type, IntUnaryOperator> rules) {
    int lightsOn = 0;
    MutablePoint light = MutablePoint.origin();
    for (light.x(0); light.x() < 1000; light.move(ReadDir.Right)) {
      for (light.y(0); light.y() < 1000; light.move(ReadDir.Down)) {
        int bulb = 0;
        for (Instruction instruction : instructions) {
          if (instruction.range.contains(light)) {
            bulb = rules.get(instruction.type).applyAsInt(bulb);
          }
        }
        lightsOn += bulb;
      }
    }
    return lightsOn;
  }

  /**
   * Input class that specifies the type of instruction and the range (top left corner to bottom right corner inclusive).
   */
  private static class Instruction {
    private static final Pattern pattern = Pattern.compile("^(.+) (\\d+),(\\d+) through (\\d+),(\\d+)$");
    private static final Map<String, Type> types = EnumUtils.getLookup(Type.class);

    private final Type type;
    private final Range range;

    private Instruction(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.type = types.get(matcher.group(1));
      this.range = new Range(matcher);
    }
  }

  /**
   * The various instruction types we need to implement for instructions.
   */
  private enum Type implements HasId<String> {
    On("turn on"),
    Off("turn off"),
    Toggle("toggle");

    private final String indicator;

    private Type(String indicator) {
      this.indicator = indicator;
    }

    @Override
    public String getId() {
      return indicator;
    }
  }

  /**
   * Rectangle range class that is specified by its top-left and bottom right corners inclusive.
   */
  private static class Range {
    private final ImmutablePoint tl;
    private final ImmutablePoint br;

    private Range(Matcher matcher) {
      this.tl = new ImmutablePoint(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
      this.br = new ImmutablePoint(Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)));
    }

    private boolean contains(MutablePoint p) {
      return (tl.x() <= p.x()) && (p.x() <= br.x())
          && (tl.y() <= p.y()) && (p.y() <= br.y());
    }
  }
}
