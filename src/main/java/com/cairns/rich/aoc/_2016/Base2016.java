package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Base;

/**
 * Base class for all advent of code puzzles from 2016.
 * Because all of the actual days are package protected, the {@link #day} variable can be used to run explicit puzzles.
 */
public abstract class Base2016 extends Base {
  public static final Base2016 day = new Day25();
}
