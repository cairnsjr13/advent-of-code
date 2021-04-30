package com.cairns.rich.aoc;

import java.util.function.Supplier;

/**
 * Abstraction layer that provides the ability to simply perform actions
 * that would otherwise require try-catch blocks.  Checked exceptions are
 * the bane of quick-java's existence and this strives to fix that.
 */
public abstract class QuietCapable {
  /**
   * Performs the given action while quietly re-throwing any exceptions that result.
   */
  protected static final void quietly(LoudRunnable action) {
    quietly(() -> {
      action.run();
      return null;
    });
  }

  /**
   * Performs the given action to compute a value while quietly re-throwing any exceptions that result.
   * {@link RuntimeException}s will be re-thrown as is.
   * All other {@link Throwable}s will be wrapped in a {@link RuntimeException}.
   */
  protected static final <T> T quietly(LoudSupplier<T> action) {
    try {
      return action.get();
    }
    catch (Error e) {
      throw e;
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  
  /**
   * Functional interface that allows a {@link Runnable} to throw any {@link Throwable}s.
   */
  protected interface LoudRunnable {
    /**
     * Executes an action without being required to try-catch any {@link Throwable}s.
     */
    void run() throws Throwable;
  }

  /**
   * Functional interface that allows a {@link Supplier} to throw any {@link Throwable}s.
   * 
   * Note: This will require primitives to be auto boxed/unboxed.
   *       It might be worth creating specialized versions, but doing that preemptively is a ton of code.
   */
  protected interface LoudSupplier<T> {
    /**
     * Executes an action and returns a value without being required to try-catch any {@link Throwable}s.
     */
    T get() throws Throwable;
  }
}
