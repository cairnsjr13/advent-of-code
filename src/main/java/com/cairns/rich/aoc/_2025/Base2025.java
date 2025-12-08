package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Base;
import java.util.function.Supplier;

/**
 * Base class for all advent of code puzzles from 2025.
 * Because all of the days are package protected, the {@link #day} variable can be used to run explicit puzzles.
 */
public abstract class Base2025 extends Base {
  public static final Supplier<Base> day = () -> new Day07();
}
