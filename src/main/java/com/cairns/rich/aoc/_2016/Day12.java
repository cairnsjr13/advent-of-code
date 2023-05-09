package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2016.AssemBunny.Inst;
import java.util.Map;

/**
 * We are going to use {@link AssemBunny} to figure out some passwords.
 * By setting register a to various values, we can get answers to each part.
 */
class Day12 extends Base2016 {
  /**
   * Returns the value in register a after running the program with register c starting at 0.
   */
  @Override
  protected Object part1(Loader loader) {
    return executeWithRegisterC(loader, 0);
  }

  /**
   * Returns the value in register a after running the program with register c starting at 1.
   */
  @Override
  protected Object part2(Loader loader) {
    return executeWithRegisterC(loader, 1);
  }

  /**
   * Executes the input {@link AssemBunny} program with the given initial c register.
   */
  private int executeWithRegisterC(Loader loader, int initialC) {
    return AssemBunny.execute(loader.ml(Inst::new), Map.of('c', initialC));
  }
}
