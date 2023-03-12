package com.cairns.rich.aoc.grid;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Base class for tests of {@link Point} implementations.
 *
 * @implNote The {@link #testPoints} variable provided at this level includes specs for points with various values
 *           (positive and negative) for the different coordinates.
 */
abstract class TestPoint<P extends Point<P>> {
  protected static final List<Pair<Integer, Integer>> testPoints =
      List.of(Pair.of(0, 0), Pair.of(1, 2), Pair.of(-3, 4), Pair.of(-5, -6), Pair.of(7, -8));

  private final BiFunction<Integer, Integer, P> ctor;
  private final Function<Pair<Integer, Integer>, P> fromSpec;

  protected TestPoint(BiFunction<Integer, Integer, P> ctor) {
    this.ctor = ctor;
    this.fromSpec = (spec) -> ctor.apply(spec.getLeft(), spec.getRight());
  }

  /**
   * This test ensures that the constructor of the points get the coordinates correct.
   */
  @Test
  public final void testCtor() {
    testPoints.forEach((spec) -> {
      P point = fromSpec.apply(spec);
      Assert.assertEquals(spec.getLeft().intValue(), point.x());
      Assert.assertEquals(spec.getRight().intValue(), point.y());
    });
  }

  /**
   * This test ensures that the {@link #equals(Object)} and {@link #hashCode()} methods are working appropriately.
   */
  @Test
  public final void testEqualsHashCode() {
    testPoints.forEach((spec) -> {
      P point = fromSpec.apply(spec);
      P same = fromSpec.apply(spec);
      Assert.assertEquals(point, same);
      Assert.assertEquals(point.hashCode(), same.hashCode());

      P diff = ctor.apply(spec.getLeft() - 13, spec.getRight() + 22);
      Assert.assertNotEquals(point, diff);

      P moveBack = point.move(RelDir.Down).move(RelDir.Up);
      Assert.assertEquals(point, moveBack);
      Assert.assertEquals(point.hashCode(), moveBack.hashCode());
    });
  }

  /**
   * This test ensures that the {@link #toString()} method includes both coordinates.
   */
  @Test
  public final void testToString() {
    testPoints.forEach((spec) -> {
      P point = fromSpec.apply(spec);
      String toString = point.toString();
      Assert.assertNotEquals(-1, toString.indexOf(Integer.toString(point.x())));
      Assert.assertNotEquals(-1, toString.indexOf(Integer.toString(point.y())));
    });
  }

  /**
   * This test ensures that the {@link Point#move(Dir, int)} method moves the correct number of units.
   */
  @Test
  public final void testMagnitudeMove() {
    int factor = 13;
    P move = ctor.apply(0, 0).move(RelDir.Up, factor).move(RelDir.Right, factor);

    Assert.assertEquals(factor, move.x());
    Assert.assertEquals(factor, move.y());
  }

  /**
   * Implementations of this test base should use this method to test the {@link Point#move(Dir)} method.
   */
  @Test
  public abstract void testMove();
}
