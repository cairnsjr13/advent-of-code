package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Base;

/**
 * Base class for all advent of code puzzles from 2018.
 * Because all of the actual days are package protected, the {@link #day} variable can be used to run explicit puzzles.
 */
public abstract class Base2018 extends Base {
  public static final Base day = new Day25();
}
