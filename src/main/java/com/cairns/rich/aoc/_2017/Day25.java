package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

class Day25 extends Base2017 {  // TODO: candidate for multi group parsing
  @Override
  protected Object part1(Loader2 loader) {
    List<String> lines = loader.ml();
    int numSteps = Integer.parseInt(lines.get(1).split(" ")[5]);
    Table<Character, Boolean, Action> machine = parseMachine(lines.subList(3, lines.size()));

    char state = 'A';
    Set<Integer> setCells = new HashSet<>();
    Integer location = 0;
    for (int i = 0; i < numSteps; ++i) {
      Action action = machine.get(state, setCells.contains(location));
      action.write.accept(setCells, location);
      location += action.move;
      state = action.next;
    }
    return setCells.size();
  }

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

  private static class Action {
    private final BiConsumer<Set<Integer>, Integer> write;
    private final int move;
    private final char next;

    private Action(boolean writeValue, int move, char next) {
      this.write = (writeValue) ? Set::add : Set::remove;
      this.move = move;
      this.next = next;
    }
  }
}
