package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day15 extends Base2022 {
  private static final long TUNING_FACTOR = 4_000_000L;
  private static final ConfigToken<Integer> inspectY = ConfigToken.of("inspectY", Integer::parseInt);
  private static final ConfigToken<Integer> maxXY = ConfigToken.of("maxXY", Integer::parseInt);

  public Day15() {
    super(inspectY.binding(2_000_000), maxXY.binding(4_000_000));
  }

  @Override
  protected Object part1(Loader loader) {
    List<Sensor> sensors = loader.ml(Sensor::new);
    int y = loader.getConfig(inspectY);
    Set<Point<?>> allSensorsAndBeacons =
        sensors.stream().map((s) -> List.of(s.location, s.closestBeacon)).flatMap(List::stream).collect(Collectors.toSet());
    int maxDist = sensors.stream().mapToInt((s) -> computeManDist(s.location, s.closestBeacon)).max().getAsInt();
    int minX = sensors.stream().mapToInt((s) -> Math.min(s.location.x(), s.closestBeacon.x())).min().getAsInt() - maxDist;
    int maxX = sensors.stream().mapToInt((s) -> Math.max(s.location.x(), s.closestBeacon.x())).max().getAsInt() + maxDist;

    int numExcludedPoints = 0;
    for (MutablePoint cur = new MutablePoint(minX, y); cur.x() <= maxX; cur.move(RelDir.Right)) {
      if (!allSensorsAndBeacons.contains(cur) && inExclusionZone(sensors, cur)) {
        ++numExcludedPoints;
      }
    }
    return numExcludedPoints;
  }

  @Override
  protected Object part2(Loader loader)  {
    List<Sensor> sensors = loader.ml(Sensor::new);
    int maxX = loader.getConfig(maxXY);
    int maxY = loader.getConfig(maxXY);
    Set<ImmutablePoint> allSensorsAndBeacons =
        sensors.stream().map((s) -> List.of(s.location, s.closestBeacon)).flatMap(List::stream).collect(Collectors.toSet());
    for (int y = 0; y <= maxY; ++y) {
      int x = 0;
      while (x <= maxX) {
        ImmutablePoint cur = new ImmutablePoint(x, y);
        if (allSensorsAndBeacons.contains(cur)) {
          ++x;
        }
        else {
          int xJump = getXJump(sensors, cur);
          if (xJump == 0) {
            return (x * TUNING_FACTOR) + y;
          }
          x += xJump;
        }
      }
    }
    throw fail();
  }

  private int getXJump(List<Sensor> sensors, ImmutablePoint current) {
    for (Sensor sensor : sensors) {
      int distFromSensor = computeManDist(current, sensor.location);
      if (distFromSensor <= sensor.exclusionDist) {
        return sensor.exclusionDist - distFromSensor + 1;
      }
    }
    return 0;
  }

  private boolean inExclusionZone(List<Sensor> sensors, MutablePoint point) {
    return sensors.stream().anyMatch((sensor) -> computeManDist(point, sensor.location) <= sensor.exclusionDist);
  }

  private static int computeManDist(Point<?> left, Point<?> right) {
    return Math.abs(left.x() - right.x()) + Math.abs(left.y() - right.y());
  }

  private static class Sensor {
    private static final Pattern pattern =
        Pattern.compile("^Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)$");

    private final ImmutablePoint location;
    private final ImmutablePoint closestBeacon;
    private final int exclusionDist;

    private Sensor(String line) {
      Matcher matcher = matcher(pattern, line);
      this.location = new ImmutablePoint(num(matcher, 1), num(matcher, 2));
      this.closestBeacon = new ImmutablePoint(num(matcher, 3), num(matcher, 4));
      this.exclusionDist = computeManDist(location, closestBeacon);
    }
  }
}
