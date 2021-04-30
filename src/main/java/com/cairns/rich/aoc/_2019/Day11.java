package com.cairns.rich.aoc._2019;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.cairns.rich.aoc._2019.IntCode.State;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.RelDir;

class Day11 extends Base2019 {
  private static final Map<Long, Function<RelDir, RelDir>> turns = Map.of(0L, RelDir::turnLeft, 1L, RelDir::turnRight);
  
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    System.out.println(runWithStartingSquare(program, 0).size());
    printWithStartingWhiteSquare(program);
  }
  
  private void printWithStartingWhiteSquare(List<Long> program) {
    Map<ImmutablePoint, Long> map = runWithStartingSquare(program, 1);
    int minX = getMin(map.keySet(), ImmutablePoint::x).x();
    int maxX = getMax(map.keySet(), ImmutablePoint::x).x();
    int minY = getMin(map.keySet(), ImmutablePoint::y).y();
    int maxY = getMax(map.keySet(), ImmutablePoint::y).y();
    char setPixel = 0x2588;
    System.out.println(StringUtils.repeat(setPixel, maxX - minX + 2));
    for (int y = maxY; y >= minY; --y) {
      System.out.print(setPixel);
      for (int x = minX; x <= maxX; ++x) {
        System.out.print((map.getOrDefault(new ImmutablePoint(x, y), 0L) == 0) ? 0x2588 : ' ');
      }
      System.out.println();
    }
    System.out.println(StringUtils.repeat(setPixel, maxX - minX + 2));
  }
  
  private Map<ImmutablePoint, Long> runWithStartingSquare(List<Long> program, long start) {
    State state = IntCode.run(program);
    RelDir facing = RelDir.Up;
    ImmutablePoint current = new ImmutablePoint(0, 0);
    Map<ImmutablePoint, Long> panels = new HashMap<>();
    panels.put(current, start);
    while (true) {
      state.blockUntilHaltOrWaitForInput();
      if (state.hasHalted()) {
        break;
      }
      state.programInput.put(panels.getOrDefault(current, 0L));
      panels.put(current, state.programOutput.take());
      facing = turns.get(state.programOutput.take()).apply(facing);
      current = current.move(facing);
    }
    return panels;
  }
}
