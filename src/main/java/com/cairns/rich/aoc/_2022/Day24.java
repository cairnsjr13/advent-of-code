package com.cairns.rich.aoc._2022;

import java.util.List;
import java.util.Map;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Day24 extends Base2022 {
  @Override
  protected void run() throws Throwable {
    Init init = new Init(fullLoader.ml());
    long mark = System.currentTimeMillis();
    int there = minTimeNeeded(init, init.start, init.end, 0);
    int back = minTimeNeeded(init, init.end, init.start, there);
    int finish = minTimeNeeded(init, init.start, init.end, back);
    System.out.println(there);
    System.out.println(finish);
    System.out.println((System.currentTimeMillis() - mark) + "ms");
  }
  
  private int minTimeNeeded(Init init, ImmutablePoint from, ImmutablePoint to, int startingTime) {
    return (int) bfs(
        new State(startingTime, from),
        (s) -> to.equals(s.location),
        (ss) -> ss.getNumSteps() + manDist(ss.state.location, to),
        (s, registrar) -> {
          if (canMove(init, s.location, s.numSteps + 1)) {
            registrar.accept(new State(s.numSteps + 1, s.location));
          }
          for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
            ImmutablePoint proposed = s.location.move(dir);
            if (canMove(init, proposed, s.numSteps + 1)) {
              registrar.accept(new State(s.numSteps + 1, proposed));
            }
          }
        }
    ).orElseThrow().state.numSteps;
  }

  private int manDist(ImmutablePoint from, ImmutablePoint to) {
    return Math.abs(from.x() - to.x()) + Math.abs(from.y() - to.y());
  }
  
  private boolean canMove(Init init, ImmutablePoint proposed, int steps) {
    if (init.end.equals(proposed) || init.start.equals(proposed)) {
      return true;
    }
    if ((proposed.x() < 0) || (init.maxX <= proposed.x()) || (proposed.y() < 0) || (init.maxY <= proposed.y())) {
      return false;
    }
    for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
      int neededToStartAtX = (proposed.x() + steps * (init.maxX + dir.turnAround().dx())) % init.maxX;
      int neededToStartAtY = (proposed.y() + steps * (init.maxY + dir.turnAround().dy())) % init.maxY;
      if (dir == init.blizzardStarts.get(neededToStartAtX, neededToStartAtY)) {
        return false;
      }
    }
    return true;
  }
  
  private static class State {
    private final int numSteps;
    private final ImmutablePoint location;
    
    private State(int numSteps, ImmutablePoint location) {
      this.numSteps = numSteps;
      this.location = location;
    }
    
    @Override
    public boolean equals(Object other) {
      return (numSteps == ((State) other).numSteps)
          && location.equals(((State) other).location);
    }
    
    @Override
    public int hashCode() {
      return (location.hashCode() * 0b11_1111_1111) + numSteps;
    }
  }
  
  private static class Init {
    private static final Map<Character, ReadDir> dirLookup =
        Map.of('>', ReadDir.Right, '<', ReadDir.Left, 'v', ReadDir.Down, '^', ReadDir.Up);
    
    private final int maxX;
    private final int maxY;
    private final ImmutablePoint start;
    private final ImmutablePoint end;
    private final Table<Integer, Integer, ReadDir> blizzardStarts = HashBasedTable.create();
    
    private Init(List<String> lines) {
      this.maxX = lines.get(0).length() - 2;
      this.maxY = lines.size() - 2;
      this.start = new ImmutablePoint(lines.get(0).indexOf('.') - 1, -1);
      this.end = new ImmutablePoint(lines.get(lines.size() - 1).indexOf('.') - 1, lines.size() - 2);
      for (int y = 0; y < maxY; ++y) {
        String line = lines.get(y + 1);
        for (int x = 0; x < maxX; ++x) {
          char ch = line.charAt(x + 1);
          if (dirLookup.containsKey(ch)) {
            blizzardStarts.put(x, y, dirLookup.get(ch));
          }
        }
      }
    }
  }
}
