package com.cairns.rich.aoc;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Base class for tests of day parts.  Implementors of this base class should provide a callback
 * to the specific part being tested.  In order for junit to find the tests, implementors should
 * provide a static {@link Parameters} annotated method that calls {@link #getPartTestSpecs(Predicate, Predicate, Predicate)}
 * with appropriate filtering.
 */
@RunWith(Parameterized.class)
abstract class DayTestBase extends AocTestBase {
  private static final Path dayTestsRoot = getResourceRoot(DayTestBase.class);
  static final Predicate<Path> acceptAll = (p) -> true;

  private final LoudPart part;
  private final PartTestSpec testSpec;

  protected DayTestBase(LoudPart part, PartTestSpec testSpec) {
    this.part = part;
    this.testSpec = testSpec;
  }

  /**
   * Finds all of the test loader files that pass the given year/day/file filters.
   * Should be used with a static {@link Parameters} method in implementing classes.
   */
  protected static Iterable<PartTestSpec> getPartTestSpecs(
      Predicate<Path> yearFilter,
      Predicate<Path> dayFilter,
      Predicate<Path> fileFilter
  ) throws IOException {
    return Files.list(dayTestsRoot)
        .filter(Files::isDirectory)
        .filter(yearFilter)
        .map((yearPath) -> {
          String year = yearPath.getFileName().toString();
          return quietly(() -> Files.list(yearPath)
              .filter(Files::isDirectory)
              .filter(dayFilter)
              .map((dayPath) -> quietly(() -> {
                String day = dayPath.getFileName().toString();
                String dayClassName = DayTestBase.class.getPackageName() + "." + year + ".Day" + day.substring("day".length());
                Class<? extends Base> dayClass = Class.forName(dayClassName).asSubclass(Base.class);
                Constructor<? extends Base> ctor = dayClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                Base dayImpl = ctor.newInstance();
                return Files.list(dayPath).filter(fileFilter).map((file) -> new PartTestSpec(dayImpl, file));
              }))
              .flatMap(Function.identity()));
        })
        .flatMap(Function.identity()).collect(Collectors.toList());
  }

  /**
   * This test ensures the {@link Object#toString()} of the part result equals the loader's expected value.
   */
  @Test
  public void runTest() throws Throwable {
    Loader loader = new Loader(testSpec.testFile);
    Assert.assertEquals(loader.expected, part.run(testSpec.day, loader).toString());
  }

  /**
   * Convenience wrapper to make specifying a test's day and loader file explicit.
   */
  static final class PartTestSpec {
    private final Base day;
    private final Path testFile;

    private PartTestSpec(Base day, Path testFile) {
      this.day = day;
      this.testFile = testFile;
    }

    /**
     * Can be used in the {@link Parameters#name()} attribute to make tests readable.
     * Will return a string including the year/day/fileName for the test.
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return dayTestsRoot.relativize(testFile).toString();
    }
  }

  /**
   * Functional interface specifying the part we are testing.
   */
  protected interface LoudPart {
    /**
     * Runs the tested part on the given day with the given loader.  Is allowed to propagate any exceptions.
     */
    Object run(Base day, Loader loader) throws Throwable;
  }
}
