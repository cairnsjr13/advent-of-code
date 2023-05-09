package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2016.AssemBunny.Inst;
import java.util.Map;

/**
 * A keypad for a wallsafe has been crushed and we need in.  We can use
 * {@link AssemBunny} to figure out what code needs to be sent to the safe.
 */
class Day23 extends Base2016 {
  /**
   * Executes the {@link AssemBunny} code with register a set to 7.
   */
  @Override
  protected Object part1(Loader loader) {
    return execute(loader, 7);
  }

  /**
   * Executes the {@link AssemBunny} code with register a set to 12.
   * TODO: part of the input code is using inc to implement multiply.
   */
  @Override
  protected Object part2(Loader loader) {
    return execute(loader, 12);
  }

  /**
   * Executes the {@link AssemBunny} code with the given register a.
   */
  private int execute(Loader loader, int initA) {
    return AssemBunny.execute(loader.ml(Inst::new), Map.of('a', initA));
  }
}
