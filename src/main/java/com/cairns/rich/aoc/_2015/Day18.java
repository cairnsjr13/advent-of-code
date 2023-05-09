package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.List;
import java.util.function.Consumer;

/**
 * Santa wants us to implement the game of life with lights!
 */
class Day18 extends Base2015 {
  private static final ConfigToken<Integer> numStepsToken = ConfigToken.of("numSteps", Integer::parseInt);

  /**
   * Counts the number of lights on after the configured number of steps for the initial grid in the input.
   */
  @Override
  protected Object part1(Loader loader) {
    return countNumberOnAfterSimulation(loader, (lights) -> { });
  }

  /**
   * Counts the number of lights on after the configured number of steps for the initial
   * grid in the input when the corner lights are always stuck in the on position.
   */
  @Override
  protected Object part2(Loader loader) {
    return countNumberOnAfterSimulation(loader, this::setBrokenLightsOn);
  }

  /**
   * Counts the number of trues in the grid.
   */
  private int countNumberOnAfterSimulation(Loader loader, Consumer<boolean[][]> cornerHandler) {
    boolean[][] lights = runSteps(loader, cornerHandler);
    int on = 0;
    for (int r = 0; r < lights.length; ++r) {
      for (int c = 0; c < lights[0].length; ++c) {
        if (lights[r][c]) {
          ++on;
        }
      }
    }
    return on;
  }

  /**
   * Simulates the configured number of steps on the initial grid of lights.
   * The given {@link Consumer} will be called at the beginning and after every step.
   * A light will be on in the next generation if:
   *   1) it has 3 neighbors in this generation, or
   *   2) it is on and has two neighbors in this generation
   */
  private boolean[][] runSteps(Loader loader, Consumer<boolean[][]> cornerHandler) {
    boolean[][] lights = parseLights(loader.ml());
    int numSteps = loader.getConfig(numStepsToken);
    cornerHandler.accept(lights);
    boolean[][] temp = new boolean[lights.length][lights[0].length];
    for (int i = 0; i < numSteps; ++i) {
      for (int r = 0; r < lights.length; ++r) {
        for (int c = 0; c < lights[0].length; ++c) {
          int numNeighbors = numNeighbors(lights, r, c);
          temp[r][c] = (numNeighbors == 3)
                    || (lights[r][c] && (numNeighbors == 2));
        }
      }
      boolean[][] swap = lights;
      lights = temp;
      temp = swap;
      cornerHandler.accept(lights);
    }
    return lights;
  }

  /**
   * Helper method to set the four corners to on.
   */
  private void setBrokenLightsOn(boolean[][] lights) {
    lights[0][0] = true;
    safeSet(lights[0], -1, true);
    safeGet(lights, -1)[0] = true;
    safeSet(safeGet(lights, -1), -1, true);
  }

  /**
   * Counts the neighbors in the 8 directions bordering the given cell.
   * Positions off the board are considered to be false.
   */
  private int numNeighbors(boolean[][] lights, int r, int c) {
    int numNeighbors = 0;
    for (int dr = -1; dr <= 1; ++dr) {
      for (int dc = -1; dc <= 1; ++dc) {
        if (((dr != 0) || (dc != 0)) && isSet(lights, r + dr, c + dc)) {
          ++numNeighbors;
        }
      }
    }
    return numNeighbors;
  }

  /**
   * Helper method to determine if a coordinate is on, safely considering spots off the board as off.
   */
  private boolean isSet(boolean[][] lights, int r, int c) {
    return (0 <= r) && (r < lights.length)
        && (0 <= c) && (c < lights[0].length)
        && lights[r][c];
  }

  /**
   * Parser method that detects which lights are initially on in a grid.
   */
  private boolean[][] parseLights(List<String> lines) {
    boolean[][] lights = new boolean[lines.size()][lines.get(0).length()];
    for (int r = 0; r < lights.length; ++r) {
      for (int c = 0; c < lights[0].length; ++c) {
        lights[r][c] = (lines.get(r).charAt(c) == '#');
      }
    }
    return lights;
  }
}
