package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.MutablePoint;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Robot security is patrolling a grid.  Their movements have constant
 * velocity and will allow us to simulate their movements over time.
 */
class Day14 extends Base2024 {
  private static final ConfigToken<Integer> widthToken = ConfigToken.of("width", Integer::parseInt);
  private static final ConfigToken<Integer> heightToken = ConfigToken.of("height", Integer::parseInt);

  /**
   * Dividing the grid into 4 quadrants, we need to calculate the product of the quadrant counts of number
   * of robots after 100 steps.  Note that robots exactly on the midline do not count.
   */
  @Override
  protected Object part1(Loader loader) {
    int width = loader.getConfig(widthToken);
    int height = loader.getConfig(heightToken);

    int midX = width / 2;
    int midY = height / 2;

    int[][] robotsPerQuadrant = new int[2][2];
    for (Robot robot : loader.ml(Robot::new)) {
      robot.move(width, height, 100);
      if ((robot.location.x() == midX) || (robot.location.y() == midY)) {
        continue;
      }
      int qCol = (robot.location.x() < midX) ? 0 : 1;
      int qRow = (robot.location.y() < midY) ? 0 : 1;
      ++robotsPerQuadrant[qRow][qCol];
    }
    return Arrays.stream(robotsPerQuadrant).flatMapToInt(Arrays::stream).reduce(1, Math::multiplyExact);
  }

  /**
   * An unspecified picture of a Christmas tree appears after on the robot grid after some number of steps.
   * The puzzle question does not tell us what the Christmas tree looks like, however, the description of
   * robot movements specifically calls out that it is possible for the robots to occupy the same space.
   * Taking a guess that "all robots on unique locations" represents a special scenario, printing the grid
   * when that happens reveals this picture (along with a few randomly placed but still unique robots):
   * ███████████████████████████████
   * █                             █
   * █                             █
   * █                             █
   * █                             █
   * █              █              █
   * █             ███             █
   * █            █████            █
   * █           ███████           █
   * █          █████████          █
   * █            █████            █
   * █           ███████           █
   * █          █████████          █
   * █         ███████████         █
   * █        █████████████        █
   * █          █████████          █
   * █         ███████████         █
   * █        █████████████        █
   * █       ███████████████       █
   * █      █████████████████      █
   * █        █████████████        █
   * █       ███████████████       █
   * █      █████████████████      █
   * █     ███████████████████     █
   * █    █████████████████████    █
   * █             ███             █
   * █             ███             █
   * █             ███             █
   * █                             █
   * █                             █
   * █                             █
   * █                             █
   * ███████████████████████████████
   */
  @Override
  protected Object part2(Loader loader) {
    boolean shouldPrint = false;
    int width = loader.getConfig(widthToken);
    int height = loader.getConfig(heightToken);

    List<Robot> robots = loader.ml(Robot::new);
    for (int step = 1; true; ++step) {
      for (Robot robot : robots) {
        robot.move(width, height, 1);
      }
      if (areAllUnique(robots)) {
        if (shouldPrint) {
          print(width, height, robots);
        }
        return step;
      }
    }
  }

  /**
   * Returns true if all robots are on unique locations.
   * This occurs when the number of unique locations equals the number of robots.
   */
  private boolean areAllUnique(List<Robot> robots) {
    return robots.size() == robots.stream().map((robot) -> robot.location).collect(Collectors.toSet()).size();
  }

  /**
   * Helper method to print the grid of given dimensions.  The robots will be represented with {@link Base#DARK_PIXEL}.
   */
  private void print(int width, int height, List<Robot> robots) {
    Set<MutablePoint> locations = robots.stream().map((robot) -> robot.location).collect(Collectors.toSet());
    MutablePoint location = MutablePoint.origin();
    for (int row = 0; row < height; ++row) {
      location.y(row);
      for (int col = 0; col < width; ++col) {
        location.x(col);
        System.out.print((locations.contains(location)) ? DARK_PIXEL : ' ');
      }
      System.out.println();
    }
  }

  /**
   * Container class describing the current location and movement velocity of a robot.
   */
  private static final class Robot {
    private static final Pattern pattern = Pattern.compile("^p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)$");

    private MutablePoint location;
    private final int dx;
    private final int dy;

    private Robot(String line) {
      Matcher matcher = matcher(pattern, line);
      this.location = new MutablePoint(num(matcher, 1), num(matcher, 2));
      this.dx = num(matcher, 3);
      this.dy = num(matcher, 4);
    }

    /**
     * Moves this robot the given number of steps, ensuring it stays on the grid with given dimensions.
     * Because of the way negatives work with the mod (%) operator, we need to ensure the value is positive.
     * This can be done by adding increments of the dimension to make sure the value is positive.
     */
    private void move(int width, int height, int numSteps) {
      int totalX = (location.x() + numSteps * dx) + (numSteps * width);
      int totalY = (location.y() + numSteps * dy) + (numSteps * height);

      this.location.x(totalX % width);
      this.location.y(totalY % height);
    }
  }
}
