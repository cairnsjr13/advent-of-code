package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.grid.Dir.EvenDir;

/**
 * A stable hexagon direction can be thought of as a honeycomb with a flat edge on the bottom (stable and not tipping).
 *
 * 
 * We use cardinal direction names to describe the directions, but we could have easily used relative names as well.
 * 
 *          N
 *     NW  ___  NE
 *        /   \
 *        \___/
 *     SW       SE
 *          S
 * 
 * 
 * In order to make the delta offsets work, we have the North and South directions change with a magnitude of 2, while
 * the other directions all change with magnitude of 1.  Looking at a grid picture, we can see that going NorthEast
 * followed by NorthWest puts us in the same location as a simple North.
 * 
 *     ___     ___            - If we assume that A is located at (0, 0):
 *    /   \___/   \           - B can be reached by traveling NorthEast d(1, 1),
 *    \___/ C \___/             which means B is located at (1, 1)
 *    /   \___/ B \           - C can then be reached by traveling NorthWest d(-1, 1),
 *    \___/ A \___/             which means C is located at (0, 2)
 *    /   \___/   \           - Our grid works if we can travel from A->C by going North d(0, 2),
 *    \___/   \___/             which is exactly where C is
 *    
 * This approach necessarily means that there are certain locations in the grid which are impossible or unreachable from
 * other positions.  For example, the point (0, 1) does not exist.  Put another way: you cannot reach (0, 1) from (0, 0)
 */
public enum SHexDir implements EvenDir<SHexDir, String> {
  North("n", 0, 2),
  NorthEast("ne", 1, 1),
  SouthEast("se", 1, -1),
  South("s", 0, -2),
  SouthWest("sw", -1, -1),
  NorthWest("nw", -1, 1);
  
  private final String id;
  private final int dx;
  private final int dy;
  
  private SHexDir(String id, int dx, int dy) {
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
