package com.cairns.rich.aoc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Loader}.
 */
public class TestLoader extends AocTestBase {
  private static final int NUM_ELEMS = 6;
  private static final List<Integer> intExp = IntStream.range(0, NUM_ELEMS).boxed().collect(Collectors.toList());
  private static final List<String> strExp = intExp.stream().map(Object::toString).collect(Collectors.toList());

  /**
   * This test ensures the parsing and transforming for multi-line loading.
   */
  @Test
  public void testMl() {
    Loader loader = new Loader(getResourcePath("loader-ml.txt"));
    Assertions.assertEquals(strExp, loader.ml());
    Assertions.assertEquals(intExp, loader.ml(Integer::parseInt));
  }

  /**
   * This test ensures the parsing and transforming for single line loading.
   */
  @Test
  public void testSl() {
    Loader loader = new Loader(getResourcePath("loader-sl.txt"));
    Assertions.assertEquals(strExp.stream().collect(Collectors.joining(",")), loader.sl());
    Assertions.assertEquals(strExp, loader.sl(","));
    Assertions.assertEquals(intExp, loader.sl(",", Integer::parseInt));
  }
}
