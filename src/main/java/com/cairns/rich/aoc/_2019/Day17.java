package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc._2019.IntCode.IO;
import com.cairns.rich.aoc._2019.IntCode.State;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

class Day17 extends Base2019 {
  private static final Map<Character, ReadDir> dirLookup =
      Map.of('^', ReadDir.Up, '>', ReadDir.Right, 'v', ReadDir.Down, '<', ReadDir.Left);

  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    program.set(0, 2L);
    State state = IntCode.run(program);

    MapState mapState = parseScaffolding(state.programOutput);
    System.out.println(getSumAlignmentParams(mapState));
    System.out.println(getDustCollected(state));
  }

  private int getSumAlignmentParams(MapState mapState) {
    int sumAlignmentParams = 0;
    Set<ImmutablePoint> visited = new HashSet<>();
    int numMoveForwards = 0;
    while (true) {
      if (mapState.droneCanMove(mapState.droneFacing)) {
        if (!visited.add(mapState.drone)) {
          sumAlignmentParams += Math.abs(mapState.drone.x() * mapState.drone.y());
        }
        mapState.drone = mapState.drone.move(mapState.droneFacing);
        ++numMoveForwards;
      }
      else {
        System.out.print(numMoveForwards);
        numMoveForwards = 0;
        ReadDir turn = mapState.droneFacing.turnLeft();
        char turnCh = 'L';
        if (!mapState.droneCanMove(turn)) {
          turn = mapState.droneFacing.turnRight();
          turnCh = 'R';
          if (!mapState.droneCanMove(turn)) {
            System.out.println();
            return sumAlignmentParams;
          }
        }
        System.out.print(turnCh);
        mapState.droneFacing = turn;
      }
    }
  }

  private long getDustCollected(State state) {
    Consumer<String> giveInstructions = (inst) -> inst.chars().forEach(state.programInput::put);
    giveInstructions.accept("A,B,A,B,C,B,C,A,B,C\n"); // Main
    giveInstructions.accept("R,4,R,10,R,8,R,4\n");    // A
    giveInstructions.accept("R,10,R,6,R,4\n");        // B
    giveInstructions.accept("R,4,L,12,R,6,L,12\n");   // C
    giveInstructions.accept("n\n");                   // no live updating
    state.blockUntilHalt();
    while (true) {
      long val = state.programOutput.take();
      if (!state.programOutput.hasMoreToTake()) {
        return val;
      }
    }
  }

  private MapState parseScaffolding(IO programOutput) {
    MapState mapState = new MapState();
    int y = 0;
    int x = 0;
    long lastRead = -1;
    while (true) {
      long read = programOutput.take();
      if (read == '.') { } // dont do anything for empty
      else if (read == '\n') {
        if (lastRead == '\n') {
          return mapState;
        }
        x = -1;
        ++y;
      }
      else {  // scaffolding and drone
        mapState.scaffolding.put(x, y);
        if (read != '#') {
          mapState.droneFacing = dirLookup.get((char) read);
          mapState.drone = new ImmutablePoint(x, y);
        }
      }
      lastRead = read;
      ++x;
    }
  }

  private static class MapState {
    private final Multimap<Integer, Integer> scaffolding = HashMultimap.create();
    private ImmutablePoint drone;
    private ReadDir droneFacing;

    private boolean droneCanMove(ReadDir dir) {
      return scaffolding.containsEntry(drone.x() + dir.dx(), drone.y() + dir.dy());
    }
  }
}
