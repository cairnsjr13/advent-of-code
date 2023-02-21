package com.cairns.rich.aoc._2017;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

class Day25 extends Base2017 {
  @Override
  protected void run() {
    System.out.println(checksum(simpleMachine(), 'A', 6));
    System.out.println(checksum(complexMachine(), 'A', 12683008));
  }

  private int checksum(Table<Character, Boolean, Action> machine, char state, int numSteps) {
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

  private Table<Character, Boolean, Action> simpleMachine() {
    Table<Character, Boolean, Action> machine = HashBasedTable.create();
    putActions(machine, 'A', new Action(true, 1, 'B'), new Action(false, -1, 'B'));
    putActions(machine, 'B', new Action(true, -1, 'A'), new Action(true, 1, 'A'));
    return machine;
  }

  private Table<Character, Boolean, Action> complexMachine() {
    Table<Character, Boolean, Action> machine = HashBasedTable.create();
    putActions(machine, 'A', new Action(true,  1, 'B'), new Action(false, -1, 'B'));
    putActions(machine, 'B', new Action(true, -1, 'C'), new Action(false, 1, 'E'));
    putActions(machine, 'C', new Action(true, 1, 'E'), new Action(false, -1, 'D'));
    putActions(machine, 'D', new Action(true, -1, 'A'), new Action(true, -1, 'A'));
    putActions(machine, 'E', new Action(false, 1, 'A'), new Action(false, 1, 'F'));
    putActions(machine, 'F', new Action(true, 1, 'E'), new Action(true, 1, 'A'));
    return machine;
  }

  private void putActions(Table<Character, Boolean, Action> machine, char state, Action ifFalse, Action ifTrue) {
    machine.put(state, false, ifFalse);
    machine.put(state, true, ifTrue);
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
