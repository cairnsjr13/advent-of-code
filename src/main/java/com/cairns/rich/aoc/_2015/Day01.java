package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;

/**
 * This is a simple nested parenthesis parser. Open paren '(' moves
 * santa up one floor, while close paren ')' moves santa down a floor.
 */
class Day01 extends Base2015 {
  /**
   * We need to figure out which floor santa ends on.  Summing each paren as +/- 1 will give us the answer.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.sl().chars().map((ch) -> (ch == '(') ? 1 : -1).sum();
  }

  /**
   * We need to figure out how many moves it takes santa to visit the basement (-1) the first time.
   * Tracking the current floor is sufficient.  Need to make sure to add one to answer since it is one based.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    String input = loader.sl();
    int floor = 0;
    for (int i = 0; i < input.length(); ++i) {
      floor += (input.charAt(i) == '(') ? 1 : -1;
      if (floor == -1) {
        return i + 1;
      }
    }
    throw fail();
  }
}
