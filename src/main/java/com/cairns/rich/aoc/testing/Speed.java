package com.cairns.rich.aoc.testing;

/**
 * Enumeration categorizing tests into buckets of speed.  Can be used to filter tests.
 */
public enum Speed {
  /**
   * Tests that run reliably less than one second.
   */
  Fast,
  /**
   * Tests that run between 1 and 10 seconds.
   */
  Acceptable,
  /**
   * Tests that run between 10 and 100 seconds.
   */
  Slow,
  /**
   * Tests that run for over ~100 seconds.
   */
  Ouch;
}
