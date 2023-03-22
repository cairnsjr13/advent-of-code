package com.cairns.rich.aoc;

import java.io.IOException;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test class for all {@link Base#part2(Loader)}s.
 * Uses {@link Parameterized} in base class to find all test cases.
 *
 * TODO: Probably should have a way to target certain tests (all, onlyFast, reasonable)
 */
public class TestBasePart2s extends DayTestBase {
  /**
   * Returns a {@link PartTestSpec} for each part2* file in the year/day folder structure.
   */
  @Parameters(name = "{0}")
  public static Iterable<PartTestSpec> getTestData() throws IOException {
    return getPartTestSpecs(acceptAll, acceptAll, (p) -> p.getFileName().toString().startsWith("part2"));
  }

  public TestBasePart2s(PartTestSpec spec) {
    super(Base::part2, spec);
  }
}
