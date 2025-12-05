package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.AocTestBase;
import com.cairns.rich.aoc.Loader;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Grid}.
 */
public class TestGrid extends AocTestBase {
  /**
   * This test ensures {@link Grid#parseChars(Loader)} returns the characters unchanged.
   */
  @Test
  public void testParseChars() {
    Loader loader = new Loader(getResourcePath("test_grid_testParseChars.txt"));
    char[][] expected = {
        { 'a', 'b', 'c' },
        { 'd', 'e', 'f' },
        { 'g', 'h', 'i' }
    };
    char[][] actual = Grid.parseChars(loader);
    Assertions.assertArrayEquals(expected, actual);
  }

  /**
   * This test ensures {@link Grid#parseInts(Loader)} converts from chars to ints correctly.
   */
  @Test
  public void testParseInts() {
    Loader loader = new Loader(getResourcePath("test_grid_testParseInts.txt"));
    int[][] expected = {
        { 0, 1, 2, 3, 4},
        { 5, 6, 7, 8, 9}
    };
    int[][] actual = Grid.parseInts(loader);
    Assertions.assertArrayEquals(expected, actual);
  }

  /**
   * This test ensures {@link Grid#parseBools(Loader, char)} converts from chars to bools using the trueValue correctly.
   */
  @Test
  public void testParseBools() {
    Loader loader = new Loader(getResourcePath("test_grid_testParseBools.txt"));
    BiConsumer<Character, boolean[][]> testWithTrueValue = (trueValue, expected) -> {
      Assertions.assertArrayEquals(expected, Grid.parseBools(loader, trueValue));
    };

    boolean[][] truesByIndex = {
        { true, false, false, false },
        { false, true, false, false },
        { false, false, true, false },
        { false, false, false, true }
    };

    testWithTrueValue.accept('0', new boolean[][] {
      truesByIndex[0],
      truesByIndex[3],
      truesByIndex[2],
      truesByIndex[1]
    });
    testWithTrueValue.accept('a', new boolean[][] {
      truesByIndex[1],
      truesByIndex[0],
      truesByIndex[3],
      truesByIndex[2]
    });
    testWithTrueValue.accept(' ', new boolean[][] {
      truesByIndex[2],
      truesByIndex[1],
      truesByIndex[0],
      truesByIndex[3]
    });
    testWithTrueValue.accept('.', new boolean[][] {
      truesByIndex[3],
      truesByIndex[2],
      truesByIndex[1],
      truesByIndex[0],
    });
  }

  /**
   * This test ensures {@link Grid#manhattanDist(Point, Point)} works for the different {@link Point} type.
   * Points in each quadrant of the x-y space are tested against each other to ensure proper negative handling.
   */
  @Test
  public void testManhattanDist() {
    runManhattanDistTest(0, Pair.of(0, 0), Pair.of(0, 0));
    runManhattanDistTest(1, Pair.of(0, 0), Pair.of(0, 1));
    runManhattanDistTest(1, Pair.of(0, 0), Pair.of(1, 0));
    runManhattanDistTest(2, Pair.of(0, 0), Pair.of(1, 1));

    runManhattanDistTest(9, Pair.of(1, 2), Pair.of(7, 5));      // q1 -> q1
    runManhattanDistTest(4, Pair.of(1, 2), Pair.of(-2, 1));     // q1 -> q2
    runManhattanDistTest(6, Pair.of(1, 2), Pair.of(-2, -1));    // q1 -> q3
    runManhattanDistTest(5, Pair.of(1, 2), Pair.of(2, -2));     // q1 -> q4

    runManhattanDistTest(7, Pair.of(-2, 1), Pair.of(-3, -5));   // q2 -> q2
    runManhattanDistTest(3, Pair.of(-2, 1), Pair.of(-3, -1));   // q2 -> q3
    runManhattanDistTest(8, Pair.of(-2, 1), Pair.of(3, -2));    // q2 -> q4

    runManhattanDistTest(3, Pair.of(-2, -1), Pair.of(-3, -3));  // q3 -> q3
    runManhattanDistTest(5, Pair.of(-2, -1), Pair.of(2, -2));   // q3 -> q4

    runManhattanDistTest(3, Pair.of(2, -1), Pair.of(1, -3));    // q4 -> q4
  }

  /**
   * Runs {@link Grid#manhattanDist(Point, Point)} test to verify given {@link Point} xy's result in the expected dist.
   */
  private void runManhattanDistTest(int expected, Pair<Integer, Integer> p0, Pair<Integer, Integer> p1) {
    Consumer<BiFunction<Integer, Integer, Point<?>>> runTest = (ctor) -> {
      Point<?> first = ctor.apply(p0.getLeft(), p0.getRight());
      Point<?> second = ctor.apply(p1.getLeft(), p1.getRight());
      Assertions.assertEquals(expected, Grid.manhattanDist(first, second));
      Assertions.assertEquals(expected, Grid.manhattanDist(second, first));
    };
    runTest.accept(ImmutablePoint::new);
    runTest.accept(MutablePoint::new);
  }

  /**
   * This test ensures {@link Grid#find(char[][], char)} can find targets without changing them.
   * Also verifies an exception is thrown if the target cannot be found.
   */
  @Test
  public void testFind() {
    char[][] grid = {
        { '#', '#', '#', '#' },
        { '#', '.', 'S', '#' },
        { '#', 'S', '.', '#' },
        { '#', '#', '#', '#' },
    };
    BiConsumer<Character, ImmutablePoint> runTest = (target, expected) -> {
      ImmutablePoint actual = Grid.find(grid, target);
      Assertions.assertEquals(expected, actual);
      Assertions.assertEquals(target, grid[actual.y][actual.x]);
    };
    runTest.accept('#', new ImmutablePoint(0, 0));
    runTest.accept('.', new ImmutablePoint(1, 1));
    runTest.accept('S', new ImmutablePoint(2, 1));
    Assertions.assertThrows(RuntimeException.class, () -> Grid.find(grid, 'x'));
  }

  /**
   * This test ensures {@link Grid#findAndReplace(char[][], char, char)} can find targets and replace them in place.
   * Also verifies an exception is thrown if the target cannot be found.  This test works by running a search on the
   * same target multiple times with a different expectation since previous runs will replace the found target.
   */
  @Test
  public void testFindAndReplace() {
    char[][] grid = {
        { '.', '.', '.', '.' },
        { '.', '.', 'S', '.' },
        { '.', 'S', '.', '.' },
        { '.', '.', '.', '.' },
    };
    char replace = '.';
    BiConsumer<Character, ImmutablePoint> runTest = (target, expected) -> {
      ImmutablePoint actual = Grid.findAndReplace(grid, target, replace);
      Assertions.assertEquals(expected, actual);
      Assertions.assertEquals(replace, grid[actual.y][actual.x]);
    };
    runTest.accept('.', new ImmutablePoint(0, 0));
    runTest.accept('S', new ImmutablePoint(2, 1));
    runTest.accept('S', new ImmutablePoint(1, 2));
    Assertions.assertThrows(RuntimeException.class, () -> Grid.find(grid, 'S'));
    Assertions.assertThrows(RuntimeException.class, () -> Grid.find(grid, 'X'));
  }

  /**
   * This test ensures the various isValid method implementations in {@link Grid} work correctly.
   * Because of the way java's generic system interacts with primitives, there needs to be separate
   * isValid functions for grids of the different primitive types.  We also want to be able to handle
   * testing {@link Point}s or xy coordinates.
   */
  @Test
  public void testIsValid() {
    List<List<Integer>> lstGrid = List.of(List.of(0, 1, 2), List.of(3, 4, 5));
    runIsValidTest(lstGrid.size(), lstGrid.get(0).size(), (p) -> Grid.isValid(lstGrid, p));
    runIsValidTest(lstGrid.size(), lstGrid.get(0).size(), (p) -> Grid.isValid(lstGrid, p.y, p.x));

    int[][] iGrid = new int[3][2];
    runIsValidTest(iGrid.length, iGrid[0].length, (p) -> Grid.isValid(iGrid, p));
    runIsValidTest(iGrid.length, iGrid[0].length, (p) -> Grid.isValid(iGrid, p.y, p.x));

    long[][] lGrid = new long[2][3];
    runIsValidTest(lGrid.length, lGrid[0].length, (p) -> Grid.isValid(lGrid, p));
    runIsValidTest(lGrid.length, lGrid[0].length, (p) -> Grid.isValid(lGrid, p.y, p.x));

    boolean[][] bGrid = new boolean[4][6];
    runIsValidTest(bGrid.length, bGrid[0].length, (p) -> Grid.isValid(bGrid, p));
    runIsValidTest(bGrid.length, bGrid[0].length, (p) -> Grid.isValid(bGrid, p.y, p.x));

    char[][] cGrid = new char[6][4];
    runIsValidTest(cGrid.length, cGrid[0].length, (p) -> Grid.isValid(cGrid, p));
    runIsValidTest(cGrid.length, cGrid[0].length, (p) -> Grid.isValid(cGrid, p.y, p.x));

  }

  /**
   * Runs a suite of isValid tests checking various boundary conditions along the edge of a grid.
   */
  private void runIsValidTest(int rows, int cols, Predicate<Point<?>> isValid) {
    runAllPointTypesIsValidTest(isValid, true, 0, 0);
    runAllPointTypesIsValidTest(isValid, false, -1, 0);
    runAllPointTypesIsValidTest(isValid, false, 0, -1);
    runAllPointTypesIsValidTest(isValid, true, 0, rows - 1);
    runAllPointTypesIsValidTest(isValid, true, cols - 1, 0);
    runAllPointTypesIsValidTest(isValid, false, 0, rows);
    runAllPointTypesIsValidTest(isValid, false, cols, 0);
    runAllPointTypesIsValidTest(isValid, false, cols, rows);
    runAllPointTypesIsValidTest(isValid, true, cols / 2, rows / 2);
  }

  /**
   * Runs an isValid test for each {@link Point} type with the given expectation and xy spec.
   */
  private void runAllPointTypesIsValidTest(Predicate<Point<?>> isValid, boolean expected, int x, int y) {
    Assertions.assertEquals(expected, isValid.test(new MutablePoint(x, y)));
    Assertions.assertEquals(expected, isValid.test(new ImmutablePoint(x, y)));
  }
}
