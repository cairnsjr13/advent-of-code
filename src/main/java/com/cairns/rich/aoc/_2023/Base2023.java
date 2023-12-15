package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Base;
import java.util.function.Supplier;

/**
 * Base class for all advent of code puzzles from 2023.
 * Because all of the actual days are package protected, the {@link #day} variable can be used to run explicit puzzles.
 */
public abstract class Base2023 extends Base {
  public static final Supplier<Base> day = () -> new Day14();
}
