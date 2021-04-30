package com.cairns.rich.aoc.grid;

/**
 * A mutable implementation of a point that will change its state as the {@link #move(Dir)} methods are used.
 * Because of this, these are unsafe for use in hash based data-structures.   
 */
public class MutablePoint extends Point<MutablePoint> {
  public MutablePoint(Point<?> copy) {
    this(copy.x, copy.y);
  }
  
  public MutablePoint(int x, int y) {
    super(x, y);
  }
  
  /**
   * Direct setter method for the x-coordinate.
   */
  public final void x(int x) {
    this.x = x;
  }
  
  /**
   * Delta based mutation method for the x-coordinate.
   */
  public final void mutateX(int delta) {
    this.x += delta;
  }
  
  /**
   * Direct setter method for the y-coordinate.
   */
  public final void y(int y) {
    this.y = y;
  }
  
  /**
   * Delta based mutation method for the y-coordinate.
   */
  public final void mutateY(int delta) {
    this.y += delta;
  }
  
  @Override
  public MutablePoint move(Dir<?, ?> dir, int magnitude) {
    x += magnitude * dir.dx();
    y += magnitude * dir.dy();
    return this;
  }
}
