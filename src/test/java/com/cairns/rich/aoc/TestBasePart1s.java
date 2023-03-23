package com.cairns.rich.aoc;

import java.io.IOException;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test class for all {@link Base#part1(Loader)}s.
 * Uses {@link Parameterized} in base class to find all test cases.
 *
 * TODO: Probably should have a way to target certain tests (all, onlyFast, reasonable)
 */
public class TestBasePart1s extends DayTestBase {
  /**
   * Returns a {@link PartTestSpec} for each part1* file in the year/day folder structure.
   */
  @Parameters(name = "{0}")
  public static Iterable<PartTestSpec> getTestData() throws IOException {
    return getPartTestSpecs(
        acceptAll,
        acceptAll,
        (p) -> p.getFileName().toString().startsWith("part1")
    );
  }

  public TestBasePart1s(PartTestSpec spec) {
    super(Base::part1, spec);
  }
}
