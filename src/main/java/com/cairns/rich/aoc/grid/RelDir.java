package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.grid.Dir.EvenDir;

/**
 * A relative direction can be thought of as a direction on a standard x-y grid.
 * Axis (and thus directions) increase in value as they go up (right), and decease as they go down (left).
 */
public enum RelDir implements EvenDir<RelDir, Character> {
  Up(0, 1),
  Right(1, 0),
  Down(0, -1),
  Left(-1, 0);
  
  private final char id = name().charAt(0);
  private final int dx;
  private final int dy;
  
  private RelDir(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }
  
  @Override
  public Character getId() {
    return id;
  }
  
  @Override
  public int dx() {
    return dx;
  }
  
  @Override
  public int dy() {
    return dy;
  }
}
