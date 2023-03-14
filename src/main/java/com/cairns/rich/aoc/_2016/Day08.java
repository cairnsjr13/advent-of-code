package com.cairns.rich.aoc._2016;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day08 extends Base2016 {
  private static final InstType[] instTypes = InstType.values();
  private static final int NUM_ROWS = 6;
  private static final int NUM_COLS = 50;

  @Override
  protected void run() {
    List<Consumer<boolean[][]>> insts = fullLoader.ml(InstType::generateInstruction);
    boolean[][] grid = new boolean[NUM_ROWS][NUM_COLS];
    insts.stream().forEach((inst) -> inst.accept(grid));
    int sum = 0;
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        if (grid[row][col]) {
          ++sum;
        }
      }
    }
    System.out.println(sum);
    print(grid);
  }

  private void print(boolean[][] grid) {
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        System.out.print((grid[row][col]) ? '\u25A0' : ' ');  // TODO: use central color
      }
      System.out.println();
    }
  }

  private enum InstType {
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

    protected abstract void apply(boolean[][] grid, int arg0, int arg1);

    private static Consumer<boolean[][]> generateInstruction(String line) {
      for (InstType instType : instTypes) {
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
