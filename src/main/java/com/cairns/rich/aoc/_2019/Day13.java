package com.cairns.rich.aoc._2019;

import java.util.List;
import java.util.Map;

import com.cairns.rich.aoc._2019.IntCode.IO;
import com.cairns.rich.aoc._2019.IntCode.State;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

class Day13 extends Base2019 {
  private static final int width = 35;
  private static final int height = 21;
  private static final Map<Long, Character> display = Map.of(
      0L, ' ',
      1L, (char) 0x2588,
      2L, 'X',
      3L, '-',
      4L, 'o'
  );
  
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    program.set(0, 2L);
    State state = IntCode.run(program);
    state.blockUntilHaltOrWaitForInput();
    
    GameState gameState = new GameState();
    updateGrid(gameState, state.programOutput);
    System.out.println(gameState.getNumBlocks());
    playGame(state, gameState);
    print(gameState);
  }
  
  private void playGame(State state, GameState gameState) {
    while (!state.hasHalted()) {
      //print(gameState);
      //quietly(() -> Thread.sleep(100));
      state.programInput.put(computeJoystick(gameState));
      state.blockUntilHaltOrWaitForInput();
      updateGrid(gameState, state.programOutput);
    }
  }
  
  private long computeJoystick(GameState gameState) {
    return (int) Math.signum(Long.compare(gameState.currentBallX, gameState.currentPaddleX));
  }
  
  private void updateGrid(GameState gameState, IO programOutput) {
    while (programOutput.hasMoreToTake()) {
      long x = programOutput.take();
      long y = programOutput.take();
      long type = programOutput.take();
      gameState.grid.put(x, y, type);
      if (type == 3) {
        gameState.currentPaddleX = x;
      }
      else if (type == 4) {
        gameState.currentBallX = x;
      }
    }
  }
  
  private void print(GameState gameState) {
    for (long y = 0; y < height; ++y) {
      for (long x = 0; x < width; ++x) {
        System.out.print(display.get(gameState.grid.get(x, y)));
      }
      System.out.println();
    }
    System.out.println("Score: " + gameState.getScore());
    System.out.println();
  }
  
  private static class GameState {
    private final Table<Long, Long, Long> grid = TreeBasedTable.create();
    private long currentPaddleX;
    private long currentBallX;
    
    private long getScore() {
      return grid.get(-1L, 0L);
    }
    
    private long getNumBlocks() {
      return grid.values().stream().filter((tile) -> 2 == tile).count();
    }
  }
}
