package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.grid.Dir.EvenDir;

/**
 * A read direction can be thought of as a direction on the page of a book.
 * Values increase as you go to the right and down (in reading order).
 */
public enum ReadDir implements EvenDir<ReadDir, Character> {
  Up(0, -1),
  Right(1, 0),
  Down(0, 1),
  Left(-1, 0);
  
  private final char id = name().charAt(0);
  private final int dx;
  private final int dy;
  
  private ReadDir(int dx, int dy) {
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
