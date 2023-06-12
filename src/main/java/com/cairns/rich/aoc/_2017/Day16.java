package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Programs are "dancing" and we need to simulate complicated sets of moves and numerous loops.
 */
class Day16 extends Base2017 {
  private static final ConfigToken<String> init = ConfigToken.of("init", Function.identity());
  private static final ConfigToken<Integer> dances = ConfigToken.of("dances", Integer::parseInt);

  /**
   * Computes the final positions after a single loop of the dance.
   */
  @Override
  protected Object part1(Loader loader) {
    List<Consumer<StringBuilder>> moves = loader.sl(",", this::parse);
    StringBuilder state = new StringBuilder(loader.getConfig(init));
    moves.forEach((move) -> move.accept(state));
    return state;
  }

  /**
   * Computes the final positions after a configured number of loops of the dance.
   * It turns out that the dance loops repeat themselves, so we don't need to simulate every single one.
   * We need to compute the number of loops required to repeat and then simulate the remainder.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Consumer<StringBuilder>> moves = loader.sl(",", this::parse);
    StringBuilder state = new StringBuilder(loader.getConfig(init));
    int loopSize = getLoopSize(moves, state);
    int numModedDances = loader.getConfig(dances) % loopSize;
    for (int i = 0; i < numModedDances; ++i) {
      moves.forEach((move) -> move.accept(state));
    }
    return state;
  }

  /**
   * Computes the number of full dance loops that are required to return back to the inital state.
   */
  private int getLoopSize(List<Consumer<StringBuilder>> moves, StringBuilder state) {
    String initial = state.toString();
    for (int size = 1; true; ++size) {
      moves.forEach((move) -> move.accept(state));
      if (initial.equals(state.toString())) {
        return size;
      }
    }
  }

  /**
   * Parses a line of the input into a mutation consumer action.
   *   - spin: sX will move X programs from the end to the beginning
   *   - exchange: xA/B will swap the programs at index A and B (0 based indexing)
   *   - partner: pA/B will swap the programs named A and B
   */
  private Consumer<StringBuilder> parse(String spec) {
    if ('s' == spec.charAt(0)) {
      int spinLength = Integer.parseInt(spec.substring(1));
      return (state) -> {
        int tailIndex = state.length() - spinLength;
        String tail = state.substring(tailIndex);
        state.replace(tailIndex, state.length(), "");
        state.insert(0, tail);
      };
    }
    else if ('x' == spec.charAt(0)) {
      int indexOfSlash = spec.indexOf("/");
      int first = Integer.parseInt(spec.substring(1, indexOfSlash));
      int second = Integer.parseInt(spec.substring(indexOfSlash + 1));
      return (state) -> {
        char firstCh = state.charAt(first);
        state.setCharAt(first, state.charAt(second));
        state.setCharAt(second, firstCh);
      };
    }
    else if ('p' == spec.charAt(0)) {
      String first = spec.substring(1, 2);
      String second = spec.substring(3, 4);
      return (state) -> {
        int firstI = state.indexOf(first);
        int secondI = state.indexOf(second);
        state.setCharAt(firstI, second.charAt(0));
        state.setCharAt(secondI, first.charAt(0));
      };
    }
    throw fail(spec);
  }
}
