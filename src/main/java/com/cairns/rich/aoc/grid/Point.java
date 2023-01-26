package com.cairns.rich.aoc.grid;

/**
 * Base class for point objects.  This represents a location in a 2 dimensional grid.
 * Coupled with the {@link Dir} system, this simplifies handling and moving about an xy system.
 * 
 * @implNote The bizarre type param here allows the {@link #move(Dir)} methods to return the proper type.
 */
public abstract class Point<P extends Point<P>> {
  protected int x;
  protected int y;
  
  protected Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  /**
   * Returns the x-coordinate of this point.
   */
  public final int x() {
    return x;
  }
  
  /**
   * Returns the y-coordinate of this point.
   */
  public final int y() {
    return y;
  }
  
  /**
   * Convenience method to move in the given direction one unit.
   * Returns the point that is one unit in the given direction.
   */
  public final P move(Dir<?, ?> dir) {
    return move(dir, 1);
  }
  
  /**
   * Returns the point that is the given number of units in the given direction.
   */
  public abstract P move(Dir<?, ?> dir, int magnitude);
  
  @Override
  public boolean equals(Object other) {
    return (x == ((Point<?>) other).x)
        && (y == ((Point<?>) other).y);
  }
  
  @Override
  public int hashCode() {
    return (x * 0b11_1111) + y;
  }
  
  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
