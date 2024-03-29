package com.cairns.rich.aoc.grid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ImmutablePoint}.
 */
public class TestImmutablePoint extends TestPoint<ImmutablePoint> {
  public TestImmutablePoint() {
    super(() -> ImmutablePoint.origin, ImmutablePoint::new);
  }

  /**
   * This test ensures that the origin is always the same point since it is immutable.
   *
   * While this may seem silly at first, if it is ever changed from a constant to a method,
   * this will ensure that at the very least the new method is still returning a singleton.
   */
  @Test
  public void testOriginSameness() {
    Assertions.assertSame(ImmutablePoint.origin, ImmutablePoint.origin);
  }

  /**
   * This test ensures that each call to {@link ImmutablePoint#move(Dir)} returns a new object.
   * Also checks that two opposing move calls results in a point object that is logically equivalent but not the same.
   */
  @Override
  public void testMove() {
    testPoints.forEach((spec) -> {
      ImmutablePoint point = new ImmutablePoint(spec.getLeft(), spec.getRight());
      ImmutablePoint move = point.move(RelDir.Down);
      ImmutablePoint back = move.move(RelDir.Up);

      Assertions.assertNotSame(point, move);
      Assertions.assertNotEquals(point, move);

      Assertions.assertNotSame(point, back);
      Assertions.assertEquals(point, back);
    });
  }
}
