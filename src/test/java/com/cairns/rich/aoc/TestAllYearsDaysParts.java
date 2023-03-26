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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  /**
   * {@link TestFactory} method that generates a container for each year under the base package.
   * Will exclude a year if it has no tests.
   */
  @TestFactory
  public Stream<DynamicContainer> testYears() {
    Speed slowestAcceptable = Speed.Ouch;
    return getGroupContainer(getResourceRoot(getClass()), (yearPath) -> {
      String year = yearPath.getFileName().toString();
      return getGroupContainer(yearPath, (dayPath) -> testsForADay(slowestAcceptable, year, dayPath));
    }).stream();
  }

  /**
   * Generates a (possibly) empty list of test containers for the given day (specified by the given path).
   * There will be a container for each part that has tests under it.
   * Tests can be disqualified based on their {@link Speed} suffix or {@link Broken} status.
   */
  private List<DynamicContainer> testsForADay(Speed slowestAcceptable, String year, Path dayPath) {
    List<DynamicContainer> partTests = new ArrayList<>();
    Base dayImpl = quietly(() -> createDayImpl(year, dayPath.getFileName().toString()));
    BiConsumer<String, LoudPart> addPartIfApplicable = (part, loudPart) -> {
      Optional<Method> partMethodOpt = Arrays.stream(dayImpl.getClass().getDeclaredMethods()).filter(
          (method) -> part.equals(method.getName())
                   && Arrays.equals(partParams, method.getParameterTypes())
      ).findFirst();
      if (partMethodOpt.isPresent() && (!partMethodOpt.get().isAnnotationPresent(Broken.class))) {
        addContainerIfHasAny(partTests, part, testsForAPart(slowestAcceptable, dayImpl, dayPath, part, loudPart));
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
  private List<DynamicTest> testsForAPart(Speed slowestAcceptable, Base dayImpl, Path dayPath, String part, LoudPart partImpl) {
    return quietly(() -> Files.list(dayPath))
        .filter((testPath) -> testPath.getFileName().toString().contains(part))
        .filter((testPath) -> getTestSpeed(testPath).ordinal() <= slowestAcceptable.ordinal())
        .map((testPath) -> DynamicTest.dynamicTest(
            testPath.getFileName().toString(),
            () -> partImpl.run(dayImpl, new Loader(testPath))
        )).collect(Collectors.toList());
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
  private List<DynamicContainer> getGroupContainer(Path path, Function<Path, List<DynamicContainer>> subGroupTests) {
    List<DynamicContainer> groupTests = new ArrayList<>();
    quietly(() -> Files.list(path))
        .filter(Files::isDirectory)
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
