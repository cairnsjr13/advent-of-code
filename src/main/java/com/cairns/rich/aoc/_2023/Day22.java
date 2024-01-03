package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * Bricks of sand are falling and we need to figure out how to disintegrate them optimally.
 */
class Day22 extends Base2023 {
  /**
   * Computes the number of bricks that are safe to disintegrate after dropping all to their final
   * position.  "Safe" is defined as all bricks remaining supported after a given brick is removed.
   */
  @Override
  protected Object part1(Loader loader) {
    Analysis analysis = new Analysis(loader);
    return analysis.bricks.stream()
        .filter((brick) -> analysis.baseToSupporteds.get(brick).stream()
            .noneMatch((supported) -> supported.wouldDrop(analysis, Set.of(brick)))
        ).count();
  }

  /**
   * Computes the number of bricks that would fall if each brick is individually disintegrated.  Returns the sum for all bricks.
   */
  @Override
  protected Object part2(Loader loader) {
    Analysis analysis = new Analysis(loader);
    return analysis.bricks.stream().mapToInt((brick) -> countTotalToDrop(brick, analysis)).sum();
  }

  /**
   * Computes the number of bricks that would drop if the given brick was disintegrated.  Each dropped
   * brick is considered to be gone for the bricks above it, which can lead to more dropped bricks.
   * Again, because the bricks are sorted by their z coordinates, we only need to consider each brick once.
   */
  private int countTotalToDrop(Brick disintegrate, Analysis analysis) {
    Set<Brick> dropping = new HashSet<>();
    dropping.add(disintegrate);
    bfs(
        disintegrate,
        (ss) -> false,
        SearchState::getNumSteps,
        (dropped, registrar) -> analysis.baseToSupporteds.get(dropped).stream()
            .filter((supported) -> supported.wouldDrop(analysis, dropping))
            .forEach((willDrop) -> {
                dropping.add(willDrop);
                registrar.accept(willDrop);
            })
    );
    return dropping.size() - 1;   // original disintegration doesnt count
  }

  /**
   * Container class for the various state objects for dropping bricks.
   */
  private static class Analysis {
    private final List<Brick> bricks;
    private final Table<Integer, Integer, Map<Integer, Brick>> xyzFilleds = HashBasedTable.create();
    private final Multimap<Brick, Brick> baseToSupporteds = HashMultimap.create();
    private final Multimap<Brick, Brick> supportedToBases = HashMultimap.create();

    /**
     * Drops all bricks into their final position and then computes {@link #baseToSupporteds} which represents all of the
     * bricks supported by each base and {@link #supportedToBases} which represents all of the bases supporting each brick.
     */
    private Analysis(Loader loader) {
      this.bricks = loader.ml(Brick::new);
      dropBricks();
      for (Brick base : bricks) {
        base.forEachXyAtZ(base.zs[1], (x, y) -> {
          Brick supported = getBrickAt(x, y, base.zs[1] + 1);
          if (supported != null) {
            baseToSupporteds.put(base, supported);
            supportedToBases.put(supported, base);
          }
        });
      }
    }

    /**
     * Drops all bricks to their final location.  By sorting the bricks in increasing order based on their
     * z coordinate, we can ensure bricks only need to move once.  Decrements the start/stop z position of
     * a brick if the layer under it is completely clear until it hits something or the ground (z = 1).
     */
    private void dropBricks() {
      bricks.sort(Comparator.comparingInt((brick) -> brick.zs[0]));
      for (Brick brick : bricks) {
        for (; brick.zs[0] > 1; --brick.zs[0], --brick.zs[1]) {
          AtomicBoolean allClear = new AtomicBoolean(true);
          brick.forEachXyAtZ(brick.zs[0], (x, y) -> {
            if (getBrickAt(x, y, brick.zs[0] - 1) != null) {
              allClear.set(false);
            }
          });
          if (!allClear.get()) {
            break;
          }
        }
        brick.addToFilleds(xyzFilleds);
      }
    }

    /**
     * Helper function to simplify getting the brick at a given location.  Empty locations will return null.
     */
    private Brick getBrickAt(int x, int y, int z) {
      return ((0 < z) && xyzFilleds.contains(x, y) && xyzFilleds.get(x, y).containsKey(z))
           ? xyzFilleds.get(x, y).get(z)
           : null;
    }
  }

  /**
   * Container class to describe the (x, y, z)s of a linear brick.  The width is always 1 and the brick extends along
   * one axis.  Each of the instance variables corresponds to the start and end of positions along the axis inclusively.
   */
  private static class Brick {
    private final int[] xs;
    private final int[] ys;
    private final int[] zs;

    private Brick(String line) {
      int tilda = line.indexOf('~');
      String[] firstParts = line.substring(0, tilda).split(",");
      String[] secondParts = line.substring(tilda + 1).split(",");
      IntFunction<int[]> parse = (i) -> Stream.of(firstParts[i], secondParts[i]).mapToInt(Integer::parseInt).sorted().toArray();
      this.xs = parse.apply(0);
      this.ys = parse.apply(1);
      this.zs = parse.apply(2);
    }

    /**
     * Returns true if the given brick would drop if the bricks in the given dropped set were not present.
     * Any location supported from below would result in the brick not dropping.
     */
    private boolean wouldDrop(Analysis analysis, Set<Brick> dropped) {
      return analysis.supportedToBases.get(this).stream().allMatch(dropped::contains);
    }

    /**
     * Adds every (x, y, z) coordinate that this brick contains to the given filleds map.
     */
    private void addToFilleds(Table<Integer, Integer, Map<Integer, Brick>> filleds) {
      for (int x = xs[0]; x <= xs[1]; ++x) {
        for (int y = ys[0]; y <= ys[1]; ++y) {
          if (!filleds.contains(x, y)) {
            filleds.put(x, y, new HashMap<>());
          }
          Map<Integer, Brick> xy = filleds.get(x, y);
          for (int z = zs[0]; z <= zs[1]; ++z) {
            xy.put(z, this);
          }
        }
      }
    }

    /**
     * Runs the given action for every (x, y) pair this brick contains at the given z coordinate.
     */
    private void forEachXyAtZ(int z, BiConsumer<Integer, Integer> at) {
      for (int x = xs[0]; x <= xs[1]; ++x) {
        for (int y = ys[0]; y <= ys[1]; ++y) {
          if ((zs[0] <= z) && (z <= zs[1])) {
            at.accept(x, y);
          }
        }
      }
    }
  }
}
