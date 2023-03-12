package com.cairns.rich.aoc.grid;

import org.junit.Assert;

/**
 * Test class for {@link ImmutablePoint}.
 */
public class TestImmutablePoint extends TestPoint<ImmutablePoint> {
  public TestImmutablePoint() {
    super(ImmutablePoint::new);
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

      Assert.assertNotSame(point, move);
      Assert.assertNotEquals(point, move);

      Assert.assertNotSame(point, back);
      Assert.assertEquals(point, back);
    });
  }
}
