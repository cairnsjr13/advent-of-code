package com.cairns.rich.aoc;

import com.cairns.rich.aoc.testing.Broken;
import com.cairns.rich.aoc.testing.Speed;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * This is a test factory that generates a test for every test file for every part of every day for every year.
 */
//@Execution(ExecutionMode.CONCURRENT)  // TODO: enable multi threading.  Seems like there are races
public class TestAllYearsDaysParts extends AocTestBase {
  private static final Class<?>[] partParams = { Loader.class };

  private static final Predicate<Path> yearFilter = (yearPath) -> yearPath.getFileName().toString().contains("2024");
  private static final Predicate<Path> dayFilter = (dayPath) -> dayPath.getFileName().toString().contains("23");
  private static final Predicate<String> partFilter = (part) -> true;
  private static final Predicate<Path> fileFilter = (file) -> true;
  private static final Predicate<Speed> speedFilter = (speed) -> speed.ordinal() <= Speed.Ouch.ordinal();

  /**
   * {@link TestFactory} method that generates a container for each year under the base package.
   * Will exclude a year if it has no tests.
   */
  @TestFactory
  public Stream<DynamicContainer> testYears() {
    return getGroupContainer(getResourceRoot(getClass()), yearFilter, (yearPath) -> {
      String year = yearPath.getFileName().toString();
      return getGroupContainer(yearPath, dayFilter, (dayPath) -> testsForADay(year, dayPath));
    }).stream();
  }

  /**
   * Generates a (possibly) empty list of test containers for the given day (specified by the given path).
   * There will be a container for each part that has tests under it.
   * Tests can be disqualified based on their {@link Speed} suffix or {@link Broken} status.
   */
  private List<DynamicContainer> testsForADay(String year, Path dayPath) {
    List<DynamicContainer> partTests = new ArrayList<>();
    Base dayImpl = quietly(() -> createDayImpl(year, dayPath.getFileName().toString()));
    BiConsumer<String, LoudPart> addPartIfApplicable = (part, loudPart) -> {
      Optional<Method> partMethodOpt = Arrays.stream(dayImpl.getClass().getDeclaredMethods()).filter(
          (method) -> part.equals(method.getName())
                   && Arrays.equals(partParams, method.getParameterTypes())
      ).findFirst();
      if (partFilter.test(part) && partMethodOpt.isPresent() && (!partMethodOpt.get().isAnnotationPresent(Broken.class))) {
        addContainerIfHasAny(partTests, part, testsForAPart(dayImpl, dayPath, part, loudPart));
      }
    };
    addPartIfApplicable.accept("part1", Base::part1);
    addPartIfApplicable.accept("part2", Base::part2);
    return partTests;
  }

  /**
   * Generates a (possibly) empty list of test containers for the given part of a day (specified by a string name and an impl).
   * There will be a test for each file under the given day's path that contains given part name.
   * Tests can be disqualified based on their {@link Speed} suffix.
   */
  private List<DynamicTest> testsForAPart(Base dayImpl, Path dayPath, String part, LoudPart partImpl) {
    return quietly(() -> Files.list(dayPath))
        .filter((testPath) -> testPath.getFileName().toString().contains(part))
        .filter((testPath) -> fileFilter.test(testPath))
        .filter((testPath) -> speedFilter.test(getTestSpeed(testPath)))
        .map((testPath) -> buildTest(testPath, partImpl, dayImpl))
        .collect(Collectors.toList());
  }

  /**
   * Generates a test for the given path for the given part of the given day.
   * Will pull the expected result from the loader on the test path.
   */
  private DynamicTest buildTest(Path testPath, LoudPart partImpl, Base dayImpl) {
    return DynamicTest.dynamicTest(
        testPath.getFileName().toString(),
        () -> {
          Loader loader = new Loader(testPath);
          String actual = Objects.toString(partImpl.run(dayImpl, loader));
          Assertions.assertEquals(loader.expected, actual);
        }
    );
  }

  /**
   * Creates an instance of the {@link Base} impl for the given year and day using reflection.
   */
  private Base createDayImpl(String year, String day) throws ReflectiveOperationException {
    String dayClassName = TestAllYearsDaysParts.class.getPackageName() + "." + year + ".Day" + day.substring("day".length());
    Constructor<? extends Base> ctor = Class.forName(dayClassName).asSubclass(Base.class).getDeclaredConstructor();
    ctor.setAccessible(true);
    return ctor.newInstance();
  }

  /**
   * Returns the {@link Speed} category for the given testFile which is specified by including the name in the filename.
   */
  private Speed getTestSpeed(Path testPath) {
    String fileName = testPath.getFileName().toString();
    return Arrays.stream(EnumUtils.enumValues(Speed.class))
        .filter((speed) -> fileName.contains(speed.name()))
        .findFirst().orElse(Speed.Fast);
  }

  /**
   * Returns a list of non empty {@link DynamicContainer}s that are created with the
   * given subGroupTests function from the directories under the given root group path.
   */
  private List<DynamicContainer> getGroupContainer(
      Path path,
      Predicate<Path> subGroupPathFilter,
      Function<Path, List<DynamicContainer>> subGroupTests
  ) {
    List<DynamicContainer> groupTests = new ArrayList<>();
    quietly(() -> Files.list(path))
        .filter(Files::isDirectory)
        .filter(subGroupPathFilter)
        .forEach((subPath) -> addContainerIfHasAny(groupTests, subPath.getFileName().toString(), subGroupTests.apply(subPath)));
    return groupTests;
  }

  /**
   * Adds a newly created {@link DynamicContainer} with the given name to the given group list if the tests are not empty.
   */
  private void addContainerIfHasAny(List<DynamicContainer> group, String name, List<? extends DynamicNode> subGroupTests) {
    if (!subGroupTests.isEmpty()) {
      group.add(DynamicContainer.dynamicContainer(name, subGroupTests));
    }
  }

  /**
   * Functional interface specifying the part we are testing.
   */
  protected interface LoudPart {
    /**
     * Runs the tested part on the given day with the given loader.  Is allowed to propagate any exceptions.
     */
    Object run(Base base, Loader loader) throws Throwable;
  }
}
