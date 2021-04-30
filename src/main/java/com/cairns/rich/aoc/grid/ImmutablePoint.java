package com.cairns.rich.aoc.grid;

/**
 * An immutable implementation of a point that will never change once constructed.  It is thus safe for use in hash
 * based data-structures.  The {@link #move(Dir)} methods will return newly constructed point objects and leave the
 * original unchanged.  Because of this, it is important to be careful with memory usage.
 */
public class ImmutablePoint extends Point<ImmutablePoint> {
  public ImmutablePoint(Point<?> copy) {
    this(copy.x, copy.y);
  }
  
  public ImmutablePoint(int x, int y) {
    super(x, y);
  }
  
  @Override
  public final ImmutablePoint move(Dir<?, ?> dir, int magnitude) {
    return new ImmutablePoint(x + magnitude * dir.dx(), y + magnitude * dir.dy());
  }
}
