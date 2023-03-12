package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.Base.HasId;
import com.cairns.rich.aoc.TestUniqueness;
import com.cairns.rich.aoc.grid.Dir.EvenDir;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for all {@link Dir} implementations.
 */
public class TestDir {
  private List<Class<? extends Dir<?, ?>>> dirClasses = List.of(
      CardDir.class,
      RelDir.class,
      ReadDir.class,
      SHexDir.class,
      UHexDir.class
  );

  /**
   * This test ensures that each direction class has no conflicts in their Ids.
   */
  @Test
  public void testHasId() {
    dirClasses.forEach((dirClass) -> TestUniqueness.test(dirClass.getEnumConstants(), HasId::getId));
  }

  /**
   * This test ensures that each direction class has no conflicts in their ordinal values.
   */
  @Test
  public void testOrdinal() {
    dirClasses.forEach((dirClass) -> TestUniqueness.test(dirClass.getEnumConstants(), Dir::ordinal));
  }

  /**
   * This test ensures that turning left and turning right work as expected.
   */
  @Test
  public void testTurnLeftTurnRight() {
    testTurns(dirClasses.stream(), Function.identity(), Dir::turnLeft, Dir::turnRight);
  }

  /**
   * This test ensures that turning around for {@link EvenDir}s works as expected.
   */
  @Test
  public void testTurnAround() {
    testTurns(
        dirClasses.stream().filter(EvenDir.class::isAssignableFrom),
        EvenDir.class::cast,
        EvenDir::turnAround,
        EvenDir::turnAround
    );
  }

  /**
   * Helper method to ensure turning works for all of the given dirClasses.  Makes sure
   * that turning results in a different direction, but turning back results in the original direction.
   */
  private <D> void testTurns(
      Stream<Class<? extends Dir<?, ?>>> dirClasses,
      Function<Dir<?, ?>, D> toType,
      UnaryOperator<D> toTurn,
      UnaryOperator<D> toBack
  ) {
    dirClasses.forEach((dirClass) -> Arrays.stream(dirClass.getEnumConstants()).map(toType).forEach((dir) -> {
      D turn = toTurn.apply(dir);
      D back = toBack.apply(turn);
      Assert.assertNotEquals(dir, turn);
      Assert.assertEquals(dir, back);
    }));
  }
}
