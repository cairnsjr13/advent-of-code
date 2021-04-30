package com.cairns.rich.aoc._2020;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

class Day24 extends Base2020 {
  private static final Direction[] directions = Direction.values();
  private static final Map<Boolean, BiConsumer<Set<Point>, Point>> flipActions = Map.of(
      true, Set::remove,
      false, Set::add
  );
  
  @Override
  protected void run() {
    List<List<Direction>> tileSpecs = fullLoader.ml(this::parse);
    Set<Point> initialBlackTiles = getInitialBlackTiles(tileSpecs);
    System.out.println(initialBlackTiles.size());
    System.out.println(getNumBlackTilesAfter100Days(initialBlackTiles));
  }
  
  private int getNumBlackTilesAfter100Days(Set<Point> blackTiles) {
    Set<Point> temp = new HashSet<>();
    for (int i = 0; i < 100; ++i) {
      temp.clear();
      for (Point candidatePoint : getAllCandidatePoints(blackTiles)) {
        int numNeighbors = countNeighbors(blackTiles, candidatePoint);
        if ((numNeighbors == 2) || ((numNeighbors == 1) && blackTiles.contains(candidatePoint))) {
          temp.add(candidatePoint);
        }
      }
      Set<Point> cache = blackTiles;
      blackTiles = temp;
      temp = cache;
    }
    return blackTiles.size();
  }
  
  private Set<Point> getInitialBlackTiles(List<List<Direction>> tileSpecs) {
    Set<Point> blackTiles = new HashSet<>();
    for (List<Direction> tileSpec : tileSpecs) {
      int x = 0;
      int y = 0;
      for (Direction direction : tileSpec) {
        x += direction.dx;
        y += direction.dy;
      }
      Point flip = new Point(x, y);
      flipActions.get(blackTiles.contains(flip)).accept(blackTiles, flip);
    }
    return blackTiles;
  }
  
  private int countNeighbors(Set<Point> blackTiles, Point inspect) {
    int numNeighbors = 0;
    for (Direction dir : directions) {
      if (blackTiles.contains(new Point(inspect.x + dir.dx, inspect.y + dir.dy))) {
        ++numNeighbors;
      }
    }
    return numNeighbors;
  }
  
  private Set<Point> getAllCandidatePoints(Set<Point> blackTiles) {
    Set<Point> candidatePoints = new HashSet<>();
    for (Point blackTile : blackTiles) {
      for (Direction direction : directions) {
        candidatePoints.add(new Point(blackTile.x + direction.dx, blackTile.y + direction.dy));
      }
      candidatePoints.add(blackTile);
    }
    return candidatePoints;
  }
  
  private List<Direction> parse(String spec) {
    List<Direction> directions = new ArrayList<>();
    for (int i = 0; i < spec.length(); ++i) {
      char ch = spec.charAt(i);
      if (ch == 'e') {
        directions.add(Direction.East);
      }
      else if (ch == 'w') {
        directions.add(Direction.West);
      }
      else if (ch == 'n') {
        directions.add((spec.charAt(i + 1) == 'e') ? Direction.NorthEast : Direction.NorthWest);
        ++i;
      }
      else if (ch == 's') {
        directions.add((spec.charAt(i + 1) == 'e') ? Direction.SouthEast : Direction.SouthWest);
        ++i;
      }
    }
    return directions;
  }
  
  private enum Direction {
    NorthEast(1, 1),
    East(2, 0),
    SouthEast(1, -1),
    NorthWest(-1, 1),
    West(-2, 0),
    SouthWest(-1, -1);
    
    private final int dx;
    private final int dy;
    
    private Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }
  }
  
  private static class Point {
    private int x;
    private int y;
    
    private Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
      return (x == ((Point) obj).x)
          && (y == ((Point) obj).y);
    }
    
    @Override
    public int hashCode() {
      return x + (y << 8);
    }
    
    @Override
    public String toString() {
      return "(" + x + "," + y + ")";
    }
  }
}
