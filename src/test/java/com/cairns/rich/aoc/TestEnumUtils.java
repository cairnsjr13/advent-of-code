package com.cairns.rich.aoc;

import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ReadDir;
import org.junit.Assert;
import org.junit.Test;

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

    Assert.assertSame(cardDirs0, cardDirs1);
    Assert.assertSame(cardDirs0, cardDirs2);
    Assert.assertSame(readDirs0, readDirs1);
  }
}
