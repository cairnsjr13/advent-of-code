package com.cairns.rich.aoc;

import com.cairns.rich.aoc.QuietCapable.LoudRunnable;
import com.cairns.rich.aoc.QuietCapable.LoudSupplier;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link QuietCapable}.
 */
public class TestQuietCapable {
  private static final String expectedExMessage = "This is an expected message to check against.";

  /**
   * This test ensures that both quietly methods work correctly under success conditions.
   */
  @Test
  public void testQuietlySuccess() {
    QuietCapable.quietly(() -> { });                                  // LoudRunnable test

    String expectedValue = "This is an expected value.";
    String actualValue = QuietCapable.quietly(() -> expectedValue);   // LoudSupplier test
    Assert.assertEquals(expectedValue, actualValue);
  }

  /**
   * This test ensures that both quietly methods allow exceptions to be thrown quietly.
   * Also tests that unchecked throwables are not wrapped.
   */
  @Test
  public void testQuietlyFailure() {
    Function<Throwable, LoudRunnable> toRunnableAction = (t) -> () -> { throw t; };
    runFailureTest(QuietCapable::quietly, toRunnableAction, Error.class, Function.identity());
    runFailureTest(QuietCapable::quietly, toRunnableAction, IOException.class, Throwable::getCause);
    runFailureTest(QuietCapable::quietly, toRunnableAction, IllegalStateException.class, Function.identity());

    Function<Throwable, LoudSupplier<String>> toSupplierAction = (t) -> () -> { throw t; };
    runFailureTest(QuietCapable::quietly, toSupplierAction, Error.class, Function.identity());
    runFailureTest(QuietCapable::quietly, toSupplierAction, IOException.class, Throwable::getCause);
    runFailureTest(QuietCapable::quietly, toSupplierAction, IllegalStateException.class, Function.identity());
  }

  /**
   * Runs a test verifying that the given quietly method behaves properly when throwing the given throwable type.
   * The given toInspect method is used to determine where the expected thrown type should sit in the cause stack.
   */
  private <A> void runFailureTest(
      Consumer<A> quietly,
      Function<Throwable, A> toAction,
      Class<? extends Throwable> throwableType,
      Function<Throwable, Throwable> toInspect
  ) {
    try {
      quietly.accept(toAction.apply(throwableType.getConstructor(String.class).newInstance(expectedExMessage)));
      Assert.fail("The above should have thrown an exception");
    }
    catch (Throwable t) {
      Throwable inspect = toInspect.apply(t);
      Assert.assertEquals(throwableType, inspect.getClass());
      Assert.assertEquals(expectedExMessage, inspect.getMessage());
    }
  }
}
