package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.List;

/**
 * Instead of snowfall, we produced hail.  We need to project their paths so we can explode them all with collisions.
 */
class Day24 extends Base2023 {
  private static final ConfigToken<Long> minToken = ConfigToken.of("min", Long::parseLong);
  private static final ConfigToken<Long> maxToken = ConfigToken.of("max", Long::parseLong);

  /**
   * Computes the number of collisions that will occur for each pair of hailstones in terms of just XY.
   * The collision must not take place in the past (t >= 0) and must occur in the configured window.
   */
  @Override
  protected Object part1(Loader loader) {
    double min = loader.getConfig(minToken);
    double max = loader.getConfig(maxToken);
    int numCollisions = 0;
    List<Stone> stones = loader.ml(Stone::new);
    for (int i = 0; i < stones.size(); ++i) {
      Stone left = stones.get(i);
      for (int j = i + 1; j < stones.size(); ++j) {
        Stone right = stones.get(j);
        if (doCollideXY(left, right, min, max)) {
          ++numCollisions;
        }
      }
    }
    return numCollisions;
  }

  /**
   *
   * Computes the sum of initial {@link Stone#location} coordinates required to throw a stone and hit every input hailstone.
   * We can do this by doing an O(n^3) loop for the axis velocities and treating them as constants in our algebra for the
   * three locations.  The window that we search can be configured with the min/max tokens.  If this part throws a failure,
   * try widening the search window or shuffling the input stones.  This might work because it is important for the chosen
   * stones to not have equal velocities along certain axes.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Stone> stones = loader.ml(Stone::new);
    double vMin = loader.getConfig(minToken);
    double vMax = loader.getConfig(maxToken);
    for (double vz = vMin; vz <= vMax; ++vz) {
      for (double vy = vMin; vy <= vMax; ++vy) {
        for (double vx = vMin; vx <= vMax; ++vx) {
          Stone option = buildOptionStone(stones, vx, vy, vz);
          if (stones.stream().allMatch((stone) -> doCollideXYZ(stone, option))) {
            Double answer = option.location.x + option.location.y + option.location.z;
            return answer.longValue();  // prevents scientific toString
          }
        }
      }
    }
    throw fail("Not found: increase window search or shuffle inputs");
  }

  /**
   * Returns true if the two stones' xy paths will ever cross at some time in the future in the given inclusive window.
   *
   * There are four relevant equations for this algebra:
   *     {1} X(f) = t(f) * V(xf) + L(xf)        ,        {3} Y(f) = t(f) * V(yf) + L(yf)
   *     {2} X(s) = t(s) * V(xs) + L(xs)        ,        {4} Y(s) = t(s) * V(ys) + L(ys)
   * Since a path crossing occurs when each stone's axis location [X|Y(f|s)] are equal, we can set them
   * ({1}&{2} or {3}&{4}) equal to each other to get equations that relate the times [t(f|s)] to each other:
   *     {1} X(f) = t(f) * V(xf) + L(xf) = t(s) * V(xs) + L(xs) = X(s) {2}
   *
   *                t(s) * V(xs) + L(xs) - L(xf)
   *     {5} t(f) = -----------------------------
   *                           V(xf)
   * and similarly, for the y axis:
   *     {3} Y(f) = t(f) * V(yf) + L(yf) = t(s) * V(ys) + L(ys) = Y(s) {4}
   *
   *                t(s) * V(ys) + L(ys) - L(yf)
   *     {6} t(f) = ----------------------------
   *                           V(yf)
   * We can set {5} and {6} to be equal to eliminate t(f) and solve for t(s) in terms of location and velocity constants:
   *                t(s) * V(xs) + L(xs) - L(xf)       t(s) * V(ys) + L(ys) - L(yf)
   *     {5} t(f) = ----------------------------   =   ---------------------------- = t(f) {6}
   *                           V(xf)                              V(yf)
   *
   *     V(yf) * [t(s) * V(xs) + L(xs) - L(xf)] = V(xf) * [t(s) * V(ys) + L(ys) - L(yf)]
   *     t(s) * V(yf) * V(xs) + V(yf) * [L(xs) - L(xf)] = t(s) * V(xf) * V(ys) + V(xf) * [L(ys) - L(yf)]
   *     t(s) * [V(yf) * V(xs) - V(xf) * V(ys)] = V(xf) * [L(ys) - L(yf)] - V(yf) * [L(xs) - L(xf)]
   *
   *                V(xf) * [L(ys) - L(yf)] - V(yf) * [L(xs) - L(xf)]
   *     {7} t(s) = -------------------------------------------------
   *                          V(yf) * V(xs) - V(xf) * V(ys)
   *
   * Equation 7 will give us the time that the second stone will arrive at the intersection point.  We can use that with
   * equation 5 or 6 to give us the time that the first stone will arrive at the intersection point.  Either equation 1 or
   * 2 will give us the x coordinate of the collision while equation 3 or 4 will give us the y coordinate of the collision.
   */
  private boolean doCollideXY(Stone first, Stone second, double min, double max) {
    double dLYsf = second.location.y - first.location.y;
    double dLXsf = second.location.x - first.location.x;

    double numerator = (first.velocity.x * dLYsf) - (first.velocity.y * dLXsf);
    double denominator = (first.velocity.y * second.velocity.x) - (first.velocity.x * second.velocity.y);

    double rightTime = numerator / denominator;
    double leftTime = (second.velocity.y * rightTime + dLYsf) / first.velocity.y;
    double collisionX = second.velocity.x * rightTime + second.location.x;
    double collisionY = second.velocity.y * rightTime + second.location.y;

    return (0 <= leftTime) && (0 <= rightTime)
        && (min <= collisionX) && (collisionX <= max) && (min <= collisionY) && (collisionY <= max);
  }

  /**
   * Returns true if the two stones collide on all three axes at the same time.
   * This will handle continuous collisions in the case of stones having equivalent
   * locations and velocities in any of the three axes.
   *
   * With respect to an axis w, the time of collision t(w) can be found with this algebra:
   *   location on axis w of f = W(f) = t(w) * V(wf) + L(wf)
   *   location on axis w of s = W(s) = t(w) * V(ws) + L(ws)
   *   a collision means they have the same location at the same time: W(f) = W(s)
   *   t(w) * V(wf) + L(wf) = t(w) * V(ws) + L(ws)
   *   t(w) * V(wf) - t(w) * V(ws) = L(ws) - L(wf)
   *   t(w) * [V(wf) - V(ws)] = L(ws) - L(wf)
   *
   *          L(ws) - L(wf)
   *   t(w) = -------------
   *          V(wf) - V(ws)
   */
  private boolean doCollideXYZ(Stone first, Stone second) {
    double dLXsf = second.location.x - first.location.x;
    double dVXfs = first.velocity.x - second.velocity.x;
    double tX = dLXsf / dVXfs;

    double dLYsf = second.location.y - first.location.y;
    double dVYfs = first.velocity.y - second.velocity.y;
    double tY = dLYsf / dVYfs;

    double dLZsf = second.location.z - first.location.z;
    double dVZfs = first.velocity.z - second.velocity.z;
    double tZ = dLZsf / dVZfs;

    return areValidTimes(tX, tY) && areValidTimes(tY, tZ) && areValidTimes(tX, tZ);
  }

  /**
   * Returns true if both given times are valid for a collision.  A time is considered valid if all of these hold:
   *   - Any non-NaN values are greater than or equal to 0
   *   - Either time is NaN or they are equal to each other
   * This is necessary because collisions in each axis must occur at the same time for full collision to occur.
   * A NaN collision time represents a situation in which there is a continuous collision along that axis
   * (for example, two hailstones that start at the same location on the x-axis and have the same x-velocity)
   */
  private boolean areValidTimes(double first, double second) {
    if ((Double.isNaN(first) || (0 <= first)) &&
        (Double.isNaN(second) || (0 <= second))
    ) {
      return Double.isNaN(first) || Double.isNaN(second) || (first == second);
    }
    return false;
  }

  /**
   * Constructs an option for a stone to start and be thrown with the given velocities.  By using two of the stones in our
   * input set along with these initial velocities, we can use algebra to calculate where the initial position should be.
   *
   * For the following algebra, we will use t(s) to denote the time at which stone s and the option stone 'a' are
   * at the same location.  There are nine relevant equations for this algebra:
   *     {1} X(a) = t(?) * V(xa) + L(xa)    ,    {4} Y(a) = t(?) * V(ya) + L(ya)    ,    {7} Z(a) = t(?) * V(za) + L(za)
   *     {2} X(b) = t(b) * V(xb) + L(xb)    ,    {5} Y(b) = t(b) * V(yb) + L(yb)    ,    {8} Z(b) = t(b) * V(zb) + L(zb)
   *     {3} X(c) = t(c) * V(xc) + L(xc)    ,    {6} Y(c) = t(c) * V(yc) + L(yc)    ,    {9} Z(c) = t(c) * V(zc) + L(zc)
   * Stone A and B will collide at time t(b) if X(a) = X(b), so we can set equations 1 and 2 to be equal:
   *     {1} X(a) = t(b) * V(xa) + L(xa) = t(b) * V(xb) + L(xb) = X(b) {2}
   *     t(b) * [V(xa) - V(xb)] = L(xb) - L(xa)
   *
   *                 L(xb) - L(xa)                                                      L(yb) - L(ya)
   *     {10} t(b) = -------------   and doing the same with {4} and {5} -> {11} t(b) = -------------
   *                 V(xa) - V(xb)                                                      V(ya) - V(yb)
   * Now that we have two equations for t(b), we can set them to be equal to derive a formula for L(xa):
   *
   *                 L(xb) - L(xa)       L(yb) - L(ya)
   *     {10} t(b) = -------------   =   ------------- = t(b) {11}
   *                 V(xa) - V(xb)       V(ya) - V(yb)
   *
   *     [L(xb) - L(xa)] * [V(ya) - V(yb)] = [L(yb) - L(ya)] * [V(xa) - V(xb)]
   *     [L(xa) - L(xb)] * [V(ya) - V(yb)] = [L(ya) - L(yb)] * [V(xa) - V(xb)]
   *
   *                      [L(ya) - L(yb)] * [V(xa) - V(xb)]
   *     {12} L(xa)   =   ---------------------------------  +  L(xb)
   *                               [V(ya) - V(yb)]
   *
   * Following a similar approach, but with a new point c, we can use equations {1}, {3}, {4}, and {6} to derive another L(xa):
   *                      [L(ya) - L(yc)] * [V(xa) - V(xc)]
   *     {13} L(xa)   =   ---------------------------------  +  L(xc)
   *                               [V(ya) - V(yc)]
   *
   * At this point, we will introduce a new notation, involving a delta Δ.  For example ΔL(xab) = L(xa) - L(xb).
   * Combining {12} and {13} and using our new notation, we can express L(ya) in terms of only constants:
   *
   *                  ΔL(yab) * ΔV(xab)          ΔL(yac) * ΔV(xac)
   *     {12} L(xa) = ---------------- + L(xb) = ----------------- + L(xc) = L(xa) {13}
   *                       ΔV(yab)                     ΔV(yac)
   *
   *     ΔL(yab) * ΔV(xab) * ΔV(yac) + L(xb) * ΔV(yab) * ΔV(yac) = ΔL(yac) * ΔV(xac) * ΔV(yab) + L(xc) * ΔV(yab) * ΔV(yac)
   *     ΔL(yab) * ΔV(xab) * ΔV(yac) - ΔL(yac) * ΔV(xac) * ΔV(yab) = L(xc) * ΔV(yab) * ΔV(yac) - L(xb) * ΔV(yab) * ΔV(yac)
   *     [L(ya) - L(yb)] * ΔV(xab) * ΔV(yac) - [L(ya) - L(yc)] * ΔV(xac) * ΔV(yab) = ΔL(xcb) * ΔV(yab) * ΔV(yac)
   *     L(ya) * [ΔV(xab) * ΔV(yac) - ΔV(xac) * ΔV(yab)] + L(yc) * ΔV(xac) * ΔV(yab) - L(yb) * ΔV(xab) * ΔV(yac) = ΔL(xcb) * ΔV(yab) * ΔV(yac)
   *     L(ya) * [ΔV(xab) * ΔV(yac) - ΔV(xac) * ΔV(yab)] = ΔL(xcb) * ΔV(yab) * ΔV(yac) + L(yb) * ΔV(xab) * ΔV(yac) - L(yc) * ΔV(xac) * ΔV(yab)
   *
   *                  ΔL(xcb) * ΔV(yab) * ΔV(yac) + L(yb) * ΔV(xab) * ΔV(yac) - L(yc) * ΔV(xac) * ΔV(yab)
   *     {14} L(ya) = -----------------------------------------------------------------------------------
   *                                         ΔV(xab) * ΔV(yac) - ΔV(xac) * ΔV(yab)
   *
   * Now we have an equation for the location of our option stoneA in terms of constants (since we iterate over the velocities)
   * Once we compute L(ya) using equation {14}, we can plug that value into equation {12} or {13} to get L(xa).
   *
   * We can use equations {7} and {8} to compute an additional equation similar to {10} and {11} allows
   * us to derive another equation for t(b) in terms of z.  We can then set it equal to {10}:
   *                 L(zb) - L(za)       L(xb) - L(xa)
   *     {15} t(b) = -------------   =   ------------- = t(b) {10}
   *                 V(za) - V(zb)       V(xa) - V(xb)
   *
   *     [L(za) - L(zb)] * [V(xa) - V(xb)] = [L(xa) - L(xb)] * [V(za) - V(zb)]
   *
   *                  [L(xa) - L(xb)] * [V(za) - V(zb)]
   *     {16} L(za) = --------------------------------- + L(zb)
   *                            V(xa) - V(xb)
   * Equation {16} allows us to use {14} and {12} to get us L(za), which is all three position coordinates.
   */
  private Stone buildOptionStone(List<Stone> stones, double vXa, double vYa, double vZa) {
    Stone stoneB = stones.get(1);
    Stone stoneC = stones.get(2);
    double dLXcb = stoneC.location.x - stoneB.location.x;
    double dVYab = vYa - stoneB.velocity.y;
    double dVYac = vYa - stoneC.velocity.y;
    double dVXab = vXa - stoneB.velocity.x;
    double dVXac = vXa - stoneC.velocity.x;

    double numeratorLeft = dLXcb * dVYab * dVYac;
    double numeratorMiddle = stoneB.location.y * dVXab * dVYac;
    double numeratorRight = stoneC.location.y * dVXac * dVYab;
    double numerator = numeratorLeft + numeratorMiddle - numeratorRight;

    double denominatorLeft = dVXab * dVYac;
    double denominatorRight = dVXac * dVYab;
    double denominator = denominatorLeft - denominatorRight;

    double locationY = numerator / denominator;

    double dLYab = locationY - stoneB.location.y;
    double locationX = ((dLYab * dVXab) / dVYab) + stoneB.location.x;

    double dLXab = locationX - stoneB.location.x;
    double dVZab = vZa - stoneB.velocity.z;
    double locationZ = ((dLXab * dVZab) / dVXab) + stoneB.location.z;

    return new Stone(new Xyz(locationX, locationY, locationZ), new Xyz(vXa, vYa, vZa));
  }

  /**
   * Descriptor class for a hailstone's location and velocity.
   */
  private static class Stone {
    private final Xyz location;
    private final Xyz velocity;

    private Stone(String line) {
      String[] parts = line.split(" +@ +");
      this.location = new Xyz(parts[0]);
      this.velocity = new Xyz(parts[1]);
    }

    private Stone(Xyz location, Xyz veloctiy) {
      this.location = location;
      this.velocity = veloctiy;
    }
  }

  /**
   * Descriptor class for a floating point triple.
   */
  private static class Xyz {
    private final double x;
    private final double y;
    private final double z;

    private Xyz(String spec) {
      String[] parts = spec.split(", +");
      this.x = Long.parseLong(parts[0]);
      this.y = Long.parseLong(parts[1]);
      this.z = Long.parseLong(parts[2]);
    }

    /**
     * Direct constructor that {@link Math#round(double)}s each argument.
     */
    private Xyz(double x, double y, double z) {
      this.x = Math.round(x);
      this.y = Math.round(y);
      this.z = Math.round(z);
    }
  }
}
