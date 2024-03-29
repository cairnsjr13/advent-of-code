package com.cairns.rich.aoc.grid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link MutablePoint}.
 */
public class TestMutablePoint extends TestPoint<MutablePoint> {
  public TestMutablePoint() {
    super(MutablePoint::origin, MutablePoint::new);
  }

  /**
   * This test ensures that the origins provided are not the same point since they are mutable.
   */
  @Test
  public void testOriginNotSameness() {
    Assertions.assertNotSame(MutablePoint.origin(), MutablePoint.origin());
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

      Assertions.assertSame(point, move);
      Assertions.assertEquals(point, move);

      Assertions.assertEquals(spec.getLeft() + dir.dx(), move.x());
      Assertions.assertEquals(spec.getRight() + dir.dy(), move.y());
    });
  }
}
