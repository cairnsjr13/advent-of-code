package com.cairns.rich.aoc.grid;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Base class for tests of {@link Point} implementations.
 *
 * @implNote The {@link #testPoints} variable provided at this level includes specs for points with various values
 *           (positive and negative) for the different coordinates.
 */
abstract class TestPoint<P extends Point<P>> {
  protected static final List<Pair<Integer, Integer>> testPoints =
      List.of(Pair.of(0, 0), Pair.of(1, 2), Pair.of(-3, 4), Pair.of(-5, -6), Pair.of(7, -8));

  private final Supplier<P> originSup;
  private final BiFunction<Integer, Integer, P> ctor;
  private final Function<Pair<Integer, Integer>, P> fromSpec;

  protected TestPoint(Supplier<P> originSup, BiFunction<Integer, Integer, P> ctor) {
    this.originSup = originSup;
    this.ctor = ctor;
    this.fromSpec = (spec) -> ctor.apply(spec.getLeft(), spec.getRight());
  }

  /**
   * This test ensures that the origin point is indeed at the origin (0, 0).
   */
  @Test
  public final void testOriginLocation() {
    P origin = originSup.get();
    Assertions.assertEquals(0, origin.x());
    Assertions.assertEquals(0, origin.y());
  }

  /**
   * This test ensures that the constructor of the points get the coordinates correct.
   */
  @Test
  public final void testCtor() {
    testPoints.forEach((spec) -> {
      P point = fromSpec.apply(spec);
      Assertions.assertEquals(spec.getLeft().intValue(), point.x());
      Assertions.assertEquals(spec.getRight().intValue(), point.y());
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
      Assertions.assertEquals(point, same);
      Assertions.assertEquals(point.hashCode(), same.hashCode());

      P diff = ctor.apply(spec.getLeft() - 13, spec.getRight() + 22);
      Assertions.assertNotEquals(point, diff);

      P moveBack = point.move(RelDir.Down).move(RelDir.Up);
      Assertions.assertEquals(point, moveBack);
      Assertions.assertEquals(point.hashCode(), moveBack.hashCode());
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
      Assertions.assertNotEquals(-1, toString.indexOf(Integer.toString(point.x())));
      Assertions.assertNotEquals(-1, toString.indexOf(Integer.toString(point.y())));
    });
  }

  /**
   * This test ensures that the {@link Point#move(Dir, int)} method moves the correct number of units.
   */
  @Test
  public final void testMagnitudeMove() {
    int factor = 13;
    P move = ctor.apply(0, 0).move(RelDir.Up, factor).move(RelDir.Right, factor);

    Assertions.assertEquals(factor, move.x());
    Assertions.assertEquals(factor, move.y());
  }

  /**
   * Implementations of this test base should use this method to test the {@link Point#move(Dir)} method.
   */
  @Test
  public abstract void testMove();
}
