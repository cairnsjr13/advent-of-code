package com.cairns.rich.aoc.grid;

import org.junit.Assert;

/**
 * Test class for {@link MutablePoint}.
 */
public class TestMutablePoint extends TestPoint<MutablePoint> {
  public TestMutablePoint() {
    super(MutablePoint::new);
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
