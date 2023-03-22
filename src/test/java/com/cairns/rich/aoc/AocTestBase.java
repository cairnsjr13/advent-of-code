package com.cairns.rich.aoc;

import java.nio.file.Path;

/**
 * Base class for all of the tests in our project.  Should add utility methods here.
 */
public abstract class AocTestBase extends QuietCapable {
  /**
   * Returns the path to a test resource in the same package as THIS test's class.
   */
  protected Path getResourcePath(String resourceName) {
    return getResourceRoot(getClass()).resolve(resourceName);
  }

  /**
   * Returns the root path to test resources in the same package as the given {@link Class}.
   */
  protected static Path getResourceRoot(Class<?> classRoot) {
    return Path.of("src", "test", "resources").resolve(classRoot.getPackageName().replace('.', '/'));
  }
}
