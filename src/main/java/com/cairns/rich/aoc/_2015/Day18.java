package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

class Day18 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    boolean[][] lights = parseLights(loader::ml);
    result.part1(countOn(runSteps(copy(lights), 100, false)));
    result.part2(countOn(runSteps(copy(lights), 100, true)));
  }

  private int countOn(boolean[][] lights) {
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

  private boolean[][] copy(boolean[][] lights) {
    boolean[][] copy = new boolean[lights.length][];
    for (int r = 0; r < lights.length; ++r) {
      copy[r] = Arrays.copyOf(lights[r], lights[r].length);
    }
    return copy;
  }

  private boolean[][] runSteps(boolean[][] lights, int numSteps, boolean broken) {
    if (broken) {
      setBrokenLightsOn(lights);
    }
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
      if (broken) {
        setBrokenLightsOn(lights);
      }
    }
    return lights;
  }

  private void setBrokenLightsOn(boolean[][] lights) {
    lights[0][0] = true;
    lights[0][lights[0].length - 1] = true;
    lights[lights.length - 1][0] = true;
    lights[lights.length - 1][lights[0].length - 1] = true;
  }

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

  private boolean isSet(boolean[][] lights, int r, int c) {
    return (0 <= r) && (r < lights.length)
        && (0 <= c) && (c < lights[0].length)
        && lights[r][c];
  }

  private boolean[][] parseLights(Supplier<List<String>> load) {
    List<String> lines = load.get();
    boolean[][] lights = new boolean[lines.size()][lines.get(0).length()];
    for (int r = 0; r < lights.length; ++r) {
      for (int c = 0; c < lights[0].length; ++c) {
        lights[r][c] = (lines.get(r).charAt(c) == '#');
      }
    }
    return lights;
  }
}
