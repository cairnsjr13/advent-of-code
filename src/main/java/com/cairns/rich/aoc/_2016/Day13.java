package com.cairns.rich.aoc._2016;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

class Day13 extends Base2016 {
  private static final int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
  
  @Override
  protected void run() {
    System.out.println(findMinSteps(new State(10, 7, 4)));
    System.out.println(findMinSteps(new State(1350, 31, 39)));
    System.out.println(numIn50(new State(1350, -1, -1)));
  }
  
  private int numIn50(State state) {
    int numLocations = 0;
    Set<Integer> seenPoints = new HashSet<>();
    Queue<Integer> candidates = new ArrayDeque<>();
    
    seenPoints.add(coordsToId(1, 1));
    candidates.offer(coordsToCandidate(1, 1, 0));
    while (!candidates.isEmpty()) {
      int candidate = candidates.poll();
      int x = (candidate >> 0) & 0xff;
      int y = (candidate >> 8) & 0xff;
      int moves = (candidate >> 16) & 0xff;
      if (moves <= 50) {
        ++numLocations;
        for (int[] dir : dirs) {
          doStep(state, seenPoints, candidates, x + dir[0], y + dir[1], moves + 1);
        }
      }
    }
    return numLocations;
  }
  
  private int findMinSteps(State state) {
    Set<Integer> seenPoints = new HashSet<>();
    Queue<Integer> candidates = new ArrayDeque<>();
    
    seenPoints.add(coordsToId(1, 1));
    candidates.add(coordsToCandidate(1, 1, 0));
    while (!candidates.isEmpty()) {
      int candidate = candidates.poll();
      int x = (candidate >> 0) & 0xff;
      int y = (candidate >> 8) & 0xff;
      int moves = (candidate >> 16) & 0xff;
      for (int[] dir : dirs) {
        if (doStep(state, seenPoints, candidates, x + dir[0], y + dir[1], moves + 1)) {
          return moves + 1;
        }
      }
    }
    throw fail();
  }
  
  private boolean doStep(
      State state,
      Set<Integer> seenPoints,
      Queue<Integer> candidates,
      int x,
      int y,
      int moves
  ) {
    if ((x == state.targetX) && (y == state.targetY)) {
      return true;
    }
    if ((0 <= x) && (0 <= y) && state.isOpen(x, y)) {
      int id = coordsToId(x, y);
      if (!seenPoints.contains(id)) {
        seenPoints.add(id);
        candidates.offer(coordsToCandidate(x, y, moves));
      }
    }
    return false;
  }
  
  private int coordsToId(int x, int y) {
    return x + (y << 8);
  }
  
  private int coordsToCandidate(int x, int y, int moves) {
    return coordsToId(x, y) + (moves << 16);
  }
  
  private static class State {
    private final int seed;
    private final int targetX;
    private final int targetY;
    
    private State(int seed, int targetX, int targetY) {
      this.seed = seed;
      this.targetX = targetX;
      this.targetY = targetY;
    }
    
    private boolean isOpen(int x, int y) {
      int numBits = Integer.bitCount((x * x) + (3 * x) + (2 * x * y) + (y) + (y * y) + seed);
      return numBits % 2 == 0;
    }
  }
}
