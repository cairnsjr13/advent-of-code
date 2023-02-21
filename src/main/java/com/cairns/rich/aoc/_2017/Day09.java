package com.cairns.rich.aoc._2017;

import java.util.List;

class Day09 extends Base2017 {
  @Override
  protected void run() {
    List<String> inputs = fullLoader.ml();
    for (String input : inputs) {
      State state = runCleanup(input);
      System.out.println(state.totalScore + ", " + state.removedNonCancelledGarbage);
    }
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
