package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.grid.Dir.EvenDir;

/**
 * An unstable hexagon direction can be thought of as a honeycomb with a point on the bottom (at risk of tipping).
 *
 *
 * We use cardinal direction names to describe the directions, but we could have easily used relative names as well.
 *
 *    NW    NE
 *       /\
 *   W  |  |  E
 *       \/
 *    SW    SE
 *
 *
 * In order to make the delta offsets work, we have the East and West directions change with a magnitude of 2, while
 * the other directions all change with magnitude of 1.  Looking at a grid picture, we can see that going NorthEast
 * followed by a SouthEast puts us in the same location as a simple East.
 *
 *                          - If we assume that A is located at (0, 0):
 *      /\ /\ /\            - B can be reached by traveling NorthEast d(1, 1),
 *     |  | B|  |             which means B is located at (1, 1)
 *      \/A\/C\/            - C can then be reached by traveling SouthEast d(1, -1),
 *      /\ /\ /\              which means C is located at (2, 0)
 *     |  |  |  |           - Our grid works if we can travel from A->C by going East d(2, 0),
 *      \/ \/ \/              which is exactly where C is
 *
 * This approach necessarily means that there are certain locations in the grid which are impossible or unreachable from
 * other positions.  For example, the point (1, 0) does not exist.  Put another way: you cannot reach (1, 0) from (0, 0)
 */
public enum UHexDir implements EvenDir<UHexDir, String> {
  NorthEast("ne", 1, 1),
  East("e", 2, 0),
  SouthEast("se", 1, -1),
  SouthWest("sw", -1, -1),
  West("w", -2, 0),
  NorthWest("nw", -1, 1);

  private final String id;
  private final int dx;
  private final int dy;

  private UHexDir(String id, int dx, int dy) {
    this.id = id;
    this.dx = dx;
    this.dy = dy;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public int dx() {
    return dx;
  }

  @Override
  public int dy() {
    return dy;
  }
}
