package com.cairns.rich.aoc._2016;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class Day17 extends Base2016 {
  private static final Direction[] directions = Direction.values();
  private static final Set<Character> openChars = Set.of('b', 'c', 'd', 'e', 'f');
  
  @Override
  protected void run() {
    System.out.println("PART 1");
    System.out.println(getShortestPath("ihgpwlah"));
    System.out.println(getShortestPath("kglvqrro"));
    System.out.println(getShortestPath("ulqzkmiv"));
    System.out.println(getShortestPath("vkjiggvb"));
    
    System.out.println();
    System.out.println("PART 2");
    System.out.println(getLengthOfLongestPath("ihgpwlah"));
    System.out.println(getLengthOfLongestPath("kglvqrro"));
    System.out.println(getLengthOfLongestPath("ulqzkmiv"));
    System.out.println(getLengthOfLongestPath("vkjiggvb"));
  }
  
  private String getShortestPath(String prefix) {
    Tracking tracking = new Tracking(prefix);
    while (!tracking.candidates.isEmpty()) {
      State candidate = tracking.candidates.poll();
      for (Direction direction : directions) {
        if (direction.canGo(candidate)) {
          State newState = candidate.move(direction);
          if ((newState.row == 3) && (newState.col == 3)) {
            return newState.path.stream().map(Object::toString).collect(Collectors.joining());
          }
          tracking.registerNewState(newState);
        }
      }
    }
    throw fail();
  }
  
  private int getLengthOfLongestPath(String prefix) {
    Tracking tracking = new Tracking(prefix);
    int maxLength = 0;
    while (!tracking.candidates.isEmpty()) {
      State candidate = tracking.candidates.poll();
      for (Direction direction : directions) {
        if (direction.canGo(candidate)) {
          State newState = candidate.move(direction);
          if ((newState.row == 3) && (newState.col == 3)) {
            maxLength = Math.max(maxLength, newState.path.size());
          }
          else {
            tracking.registerNewState(newState);
          }
        }
      }
    }
    return maxLength;
  }
  
  private static class Tracking {
    private final Set<State> seenStates = new HashSet<>();
    PriorityQueue<State> candidates = new PriorityQueue<>(Comparator.comparing((state) -> state.path.size()));
    
    private Tracking(String prefix) {
      State initialState = new State(prefix, 0, 0, new ArrayList<>());
      seenStates.add(initialState);
      candidates.add(initialState);
    }
    
    private void registerNewState(State newState) {
      if (!seenStates.contains(newState)) {
        seenStates.add(newState);
        candidates.add(newState);
      }
    }
  }
  
  private static enum Direction {
    Up(-1, 0),
    Down(1, 0),
    Left(0, -1),
    Right(0, 1);
    
    private final int dr;
    private final int dc;
    
    private Direction(int dr, int dc) {
      this.dr = dr;
      this.dc = dc;
    }
    
    private boolean canGo(State current) {
      int newRow = current.row + dr;
      int newCol = current.col + dc;
      return (0 <= newRow) && (newRow < 4)
          && (0 <= newCol) && (newCol < 4)
          && current.doorsOpen[ordinal()];
    }
  }
  
  private static class State {
    private final String prefix;
    private final int row;
    private final int col;
    private final List<Character> path;
    private final boolean[] doorsOpen;
    
    private State(String prefix, int row, int col, List<Character> path) {
      this.prefix = prefix;
      this.row = row;
      this.col = col;
      this.path = path;
      this.doorsOpen = getDoorsOpen();
    }
    
    private State move(Direction go) {
      List<Character> newPath = new ArrayList<>(path);
      newPath.add(go.name().charAt(0));
      return new State(prefix, row + go.dr, col + go.dc, newPath);
    }
    
    private boolean[] getDoorsOpen() {
      return quietly(() -> {
        boolean[] doorsOpen = new boolean[4];
        String hash = md5(prefix + path.stream().map(Object::toString).collect(Collectors.joining()));
        for (int i = 0; i < doorsOpen.length; ++i) {
          doorsOpen[i] = openChars.contains(hash.charAt(i));
        }
        return doorsOpen;
      });
    }
    
    @Override
    public boolean equals(Object obj) {
      State other = (State) obj;
      return (row == other.row)
          && (col == other.col)
          && Arrays.equals(doorsOpen, other.doorsOpen);
    }

    @Override
    public int hashCode() {
      return ThreadLocalRandom.current().nextInt(); // TODO: this is broken....  basically caching doesnt work. lolz
      //return row ^ col;
    }
  }
}
