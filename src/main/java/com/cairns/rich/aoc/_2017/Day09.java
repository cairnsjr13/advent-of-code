package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;

/**
 * We need to cross a stream, but it is unfortunately full of garbage.
 */
class Day09 extends Base2017 {
  /**
   * Returns the total score of the stream after cleaning up the garbage.
   * The score is the sum of all group scores where a group's score is one
   * more than the group containing it with the outtermost group scoring 1.
   */
  @Override
  protected Object part1(Loader loader) {
    return runCleanup(loader.sl()).totalScore;
  }

  /**
   * Returns the number of non-canceled characters within all of the garbage.
   * Does NOT include the leading '<', the trailing '>', any canceled characters,
   * or the '!' doing the canceling.
   */
  @Override
  protected Object part2(Loader loader) {
    return runCleanup(loader.sl()).removedNonCancelledGarbage;
  }

  /**
   * Processes the stream and returns the resultant state.  A stream is a nested group structure
   * where each group contains a number of sub groups.  Within the stream there is potentially garbage
   * data which is indicated by being surrounded by '<' and '>'.  Garbage canNOT be nested.  on top
   * of this, there are '!' throughout the stream which serve to "cancel" the following character
   * (including another '!').  The canceled character should be ignored and not open new groups or garbage.
   */
  private State runCleanup(String input) {
    State state = new State();
    for (int i = 0; i < input.length(); ++i) {
      char ch = input.charAt(i);
      if (state.inGarbage) {
        if (ch == '>') {
          state.inGarbage = false;
        }
        else if (ch == '!') {
          ++i;
        }
        else {
          ++state.removedNonCancelledGarbage;
        }
      }
      else if (ch == '{') {
        state.totalScore += state.nest;
        ++state.nest;
      }
      else if (ch == '}') {
        --state.nest;
      }
      else if (ch == '<') {
        state.inGarbage = true;
      }
    }
    return state;
  }

  /**
   * State container describing where we are at in the stream processing.
   */
  private static class State {
    private int nest = 1;
    private boolean inGarbage = false;
    private int totalScore = 0;
    private int removedNonCancelledGarbage = 0;
  }
}
