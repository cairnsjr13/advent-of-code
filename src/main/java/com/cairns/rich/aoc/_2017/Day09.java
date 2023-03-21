package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;

class Day09 extends Base2017 {
  @Override
  protected Object part1(Loader loader) {
    return runCleanup(loader.sl()).totalScore;
  }

  @Override
  protected Object part2(Loader loader) {
    return runCleanup(loader.sl()).removedNonCancelledGarbage;
  }

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

  private static class State {
    private int nest = 1;
    private boolean inGarbage = false;
    private int totalScore = 0;
    private int removedNonCancelledGarbage = 0;
  }
}
