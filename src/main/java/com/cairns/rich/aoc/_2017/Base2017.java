package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Base;

/**
 * Base class for all advent of code puzzles from 2017.
 * Because all of the actual days are package protected, the {@link #day} variable can be used to run explicit puzzles.
 */
public abstract class Base2017 extends Base {
  public static final Base day = new Day25();
}
