package com.cairns.rich.aoc._2017;

import java.util.HashMap;
import java.util.Map;

class Day03 extends Base2017 {
  private static final Direction[] directions = Direction.values();
  
  @Override
  protected void run() {
    int target = 347991;
    
    System.out.println(computeManhattanDistanceFromCenter(target));
    System.out.println(part2(target));
  }
  
  private int computeManhattanDistanceFromCenter(int target) {
    Point location = computeLocationOfIndex(target);
    return Math.abs(location.x) + Math.abs(location.y);
  }
  
  private Point computeLocationOfIndex(int target) {
    int sizeOfEdge = computeEdge(target);
    int numsLeft = target - (sizeOfEdge * sizeOfEdge);
    Point location = new Point(sizeOfEdge / 2, -sizeOfEdge / 2);
    if (numsLeft > 0) {
      location = location.move(Direction.Right);
      --numsLeft;
      Direction direction = Direction.Up;
      int numsInDirectionLeft = sizeOfEdge;
      while (numsLeft > 0) {
        location = location.move(direction);
        --numsLeft;
        --numsInDirectionLeft;
        if (numsInDirectionLeft == 0) {
          direction = direction.turnLeft();
          numsInDirectionLeft = sizeOfEdge + 1;
        }
      }
    }
    return location;
  }
  
  private int computeEdge(int target) {
    int sizeOfEdge = 1;
    for (; true; sizeOfEdge += 2) {
      int sizeOfNextBox = (sizeOfEdge + 2) * (sizeOfEdge + 2);
      if (sizeOfNextBox >= target) {
        return sizeOfEdge;
      }
    }
  }
  
  private int part2(int target) {
    Map<Point, Integer> cache = new HashMap<>();
    Point location = new Point(0, 0);
    computeSumOfNeighbors(cache, location);
    cache.put(location, 1);
    for (int i = 2; true; ++i) {
      location = computeLocationOfIndex(i);
      int sumOfNeighbors = computeSumOfNeighbors(cache, location);
      if (sumOfNeighbors > target) {
        return sumOfNeighbors;
      }
      cache.put(location, sumOfNeighbors);
    }
  }
  
  private int computeSumOfNeighbors(Map<Point, Integer> cache, Point location) {
    int sum = 0;
    for (Direction direction : directions) {
      Point neighbor = location.move(direction);
      Point compliment = neighbor.move(direction.turnLeft());
      sum += cache.getOrDefault(neighbor, 0)
           + cache.getOrDefault(compliment, 0);
    }
    return sum;
  }
  
  private class Point {
    private final int x;
    private final int y;
    
    private Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    private Point move(Direction direction) {
      return new Point(x + direction.dx, y + direction.dy);
    }
    
    @Override
    public boolean equals(Object obj) {
      return (x == ((Point) obj).x)
          && (y == ((Point) obj).y);
    }
    
    @Override
    public int hashCode() {
      return x ^ y;
    }
    
    @Override
    public String toString() {
      return "(" + x + "," + y + ")";
    }
  }
  
  private enum Direction {
    Up(0, 1),
    Left(-1, 0),
    Down(0, -1),
    Right(1, 0);
    
    private final int dx;
    private final int dy;
    
    private Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }
    
    private Direction turnLeft() {
      return directions[(ordinal() + 1) % directions.length];
    }
  }
}
