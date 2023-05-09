package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We need to simulate a display screen with a few commands.
 */
class Day08 extends Base2016 {
  private static final ConfigToken<Integer> numRows = ConfigToken.of("numRows", Integer::parseInt);
  private static final ConfigToken<Integer> numCols = ConfigToken.of("numCols", Integer::parseInt);

  /**
   * Counts the number of lit pixels on the screen after simulating all instructions.
   */
  @Override
  protected Object part1(Loader loader) {
    return screen(loader, false);
  }

  /**
   * Prints the screen after simulating all instructions.  Should display capital letters.
   */
  @Override
  protected Object part2(Loader loader) {
    return screen(loader, true);
  }

  /**
   * Simulates the screen with the input instructions.  If the returnScreen param is set
   * the final screen will be printed, otherwise a count of lit pixels will be returned.
   */
  private Object screen(Loader loader, boolean returnScreen) {
    List<Consumer<boolean[][]>> insts = loader.ml(InstType::generateInstruction);
    boolean[][] grid = new boolean[loader.getConfig(numRows)][loader.getConfig(numCols)];
    insts.stream().forEach((inst) -> inst.accept(grid));
    int sum = 0;
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        if (grid[row][col]) {
          ++sum;
        }
      }
    }
    return (returnScreen) ? print(grid) : sum;
  }

  /**
   * Helper function to print the screen to a {@link StringBuilder}.
   */
  private StringBuilder print(boolean[][] grid) {
    StringBuilder out = new StringBuilder();
    out.append("\n");
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        out.append((grid[row][col]) ? DARK_PIXEL : ' ');
      }
      out.append("\n");
    }
    return out;
  }

  /**
   * An enumeration of all of the different instructions we can use to mutate the screen.
   */
  private enum InstType {
    /**
     * Turns all of the pixels which are in the numCols by numRows rectangle at the top left (inclusive).
     */
    Rect("rect", "^rect (\\d+)x(\\d+)$") {
      @Override
      protected void apply(boolean[][] grid, int numCols, int numRows) {
        for (int row = 0; row < numRows; ++row) {
          for (int col = 0; col < numCols; ++col) {
            grid[row][col] = true;
          }
        }
      }
    },
    /**
     * Rotates all pixels in the given row (0 based) to the right by the given number of columns, in a circular fashion.
     */
    RotRow("rotate row", "^rotate row y=(\\d+) by (\\d+)$") {
      @Override
      protected void apply(boolean[][] grid, int rowNum, int numCols) {
        boolean[] newLights = new boolean[grid[rowNum].length];
        for (int i = 0; i < newLights.length; ++i) {
          newLights[i] = safeGet(grid[rowNum], i - numCols);
        }
        for (int i = 0; i < newLights.length; ++i) {
          grid[rowNum][i] = newLights[i];
        }
      }
    },
    /**
     * Rotates all pixels in the given col (0 based) downward by the given number of rows, in a circular fashion.
     */
    RotCol("rotate col", "^rotate column x=(\\d+) by (\\d+)$") {
      @Override
      protected void apply(boolean[][] grid, int colNum, int numRows) {
        boolean[] newLights = new boolean[grid.length];
        for (int i = 0; i < newLights.length; ++i) {
          newLights[i] = safeGet(grid, i - numRows)[colNum];
        }
        for (int i = 0; i < newLights.length; ++i) {
          grid[i][colNum] = newLights[i];
        }
      }
    };

    private final String name;
    private final Pattern pattern;

    private InstType(String name, String regex) {
      this.name = name;
      this.pattern = Pattern.compile(regex);
    }

    /**
     * An instruction should mutate the given grid based on the two parameters from its line.
     */
    protected abstract void apply(boolean[][] grid, int arg0, int arg1);

    /**
     * Parser method that converts a line into its corresponding instruction action.
     */
    private static Consumer<boolean[][]> generateInstruction(String line) {
      for (InstType instType : EnumUtils.enumValues(InstType.class)) {
        if (line.startsWith(instType.name)) {
          Matcher matcher = matcher(instType.pattern, line);
          int arg0 = Integer.parseInt(matcher.group(1));
          int arg1 = Integer.parseInt(matcher.group(2));
          return (grid) -> instType.apply(grid, arg0, arg1);
        }
      }
      throw fail(line);
    }
  }
}
