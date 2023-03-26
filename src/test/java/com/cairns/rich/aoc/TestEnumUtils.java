package com.cairns.rich.aoc;

import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ReadDir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link EnumUtils}.
 *
 * @implNote There is no test for {@link EnumUtils#getLookup(Class)} because it
 *           is a pass-through to {@link Base#getLookup(java.util.Collection)}.
 */
public class TestEnumUtils {
  /**
   * This test ensures that calls to {@link EnumUtils#enumValues(Class)} cache arrays across calls.
   */
  @Test
  public void testEnumValues() {
    CardDir[] cardDirs0 = EnumUtils.enumValues(CardDir.class);
    CardDir[] cardDirs1 = EnumUtils.enumValues(CardDir.class);
    ReadDir[] readDirs0 = EnumUtils.enumValues(ReadDir.class);
    CardDir[] cardDirs2 = EnumUtils.enumValues(CardDir.class);
    ReadDir[] readDirs1 = EnumUtils.enumValues(ReadDir.class);

    Assertions.assertSame(cardDirs0, cardDirs1);
    Assertions.assertSame(cardDirs0, cardDirs2);
    Assertions.assertSame(readDirs0, readDirs1);
  }
}
