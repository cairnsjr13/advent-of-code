package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * We need to model a turing machine for the garbage collector and finish the star hunt.
 */
class Day25 extends Base2017 {  // TODO: candidate for multi group parsing
  /**
   * Returns the total number of set bits after simulating the input machine for the number of steps specified.
   * A turing machine has a cursor that updates the current location, moves in a direction, and updates its state
   * based on its current state and the value of the tape at the current location.
   */
  @Override
  protected Object part1(Loader loader) {
    List<String> lines = loader.ml();
    int numSteps = Integer.parseInt(lines.get(1).split(" ")[5]);
    Table<Character, Boolean, Action> machine = parseMachine(lines.subList(3, lines.size()));

    char state = 'A';
    BitSet tape = new BitSet();
    int location = 0;
    for (int i = 0; i < numSteps; ++i) {
      int tapeIndex = toTapeIndex(location);
      Action action = machine.get(state, tape.get(tapeIndex));
      tape.set(tapeIndex, action.write);
      location += action.move;
      state = action.next;
    }
    return tape.cardinality();
  }

  /**
   * Converts a linear index (consisting of zero, negatives, and positives) to an
   * index that can be used with a {@link BitSet} (which is not allowed to have negatives).
   * This is done by taking the absolute value to get it back to positive, doubling it to
   * create holes, and subtracting 1 if the original is negative to fill the holes.  The
   * upshot of this is that 0 stays at 0, positives go to evens, and negatives go to odds.
   */
  private int toTapeIndex(int linearIndex) {
    return Math.abs(2 * linearIndex) - ((linearIndex < 0) ? 1 : 0);
  }

  /**
   * Parses the machine from the given input lines.  The lines should NOT contain the beginning state and numSteps header.
   * Each state has a set of actions to take for each of the possible tape values (0 or 1).
   */
  private Table<Character, Boolean, Action> parseMachine(List<String> lines) {
    Table<Character, Boolean, Action> states = HashBasedTable.create();
    for (int i = 0; i < lines.size(); i += 10) {
      char currentState = safeCharAt(lines.get(i), -2);
      BiConsumer<Boolean, Integer> statesRegistrar = (isSet, offset) -> {
        boolean shouldWrite = '1' == safeCharAt(lines.get(offset), -2);
        int dir = (lines.get(offset + 1).contains("left")) ? -1 : 1;
        char nextState = safeCharAt(lines.get(offset + 2), -2);
        states.put(currentState, isSet, new Action(shouldWrite, dir, nextState));
      };
      statesRegistrar.accept(false, i + 2);
      statesRegistrar.accept(true, i + 6);
    }
    return states;
  }

  /**
   * Descriptor class for an action that the turing machine takes.
   * This consists of the value it will write, which direction to move, and the next state to proceed from.
   */
  private static class Action {
    private final boolean write;
    private final int move;
    private final char next;

    private Action(boolean write, int move, char next) {
      this.write = write;
      this.move = move;
      this.next = next;
    }
  }
}
