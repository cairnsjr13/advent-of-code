package com.cairns.rich.aoc.grid;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link MutablePoint}.
 */
public class TestMutablePoint extends TestPoint<MutablePoint> {
  public TestMutablePoint() {
    super(MutablePoint::new);
  }

  /**
   * This test ensures that {@link MutablePoint#origin()} behaves properly.  Namely:
   *   1) the point is at the origin (0, 0)
   *   2) repeated calls to the method return points that are {@link Point#equals(Object)}
   *   3) repeated calls do NOT return the same object (since they are mutable)
   */
  @Test
  public void testOrigin() {
    MutablePoint first = MutablePoint.origin();
    MutablePoint second = MutablePoint.origin();

    Assert.assertEquals(0, first.x());
    Assert.assertEquals(0, first.y());

    Assert.assertEquals(first, second);
    Assert.assertNotSame(first, second);
  }

  /**
   * This test ensures that each call to {@link MutablePoint#move(Dir)} returns the same object.
   * Also checks that two opposing move calls results in a point object that is equivalent to the original coordinates.
   */
  @Override
  public void testMove() {
    testPoints.forEach((spec) -> {
      RelDir dir = RelDir.Down;
      MutablePoint point = new MutablePoint(spec.getLeft(), spec.getRight());
      MutablePoint move = point.move(dir);

      Assert.assertSame(point, move);
      Assert.assertEquals(point, move);

      Assert.assertEquals(spec.getLeft() + dir.dx(), move.x());
      Assert.assertEquals(spec.getRight() + dir.dy(), move.y());
    });
  }
}
