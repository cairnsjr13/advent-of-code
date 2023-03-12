package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.grid.Dir.EvenDir;

/**
 * A cardinal direction can be thought of as a direction on a compass.
 * Values increase as you go North or East, and decrease as you go South or West.
 */
public enum CardDir implements EvenDir<CardDir, Character> {
  North(0, 1),
  East(1, 0),
  South(0, -1),
  West(-1, 0);

  private final char id = name().charAt(0);
  private final int dx;
  private final int dy;

  private CardDir(int dx, int dy) {
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
