package com.cairns.rich.aoc._2022;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.RelDir;

public class Day15 extends Base2022 {
  @Override
  protected void run() throws Throwable {
    ImmutablePoint a = new ImmutablePoint(0, 0);
    int ra = 5;
    ImmutablePoint b = new ImmutablePoint(4, 4);
    int rb = 5;
    Set<ImmutablePoint> answer = intersections(a, ra, b, rb);
    System.out.println(answer);
    if (true) return;
    List<Sensor> testSensors = testLoader.ml(Sensor::new);
    System.out.println(countExcludedPoints(testSensors, 10));
    System.out.println(computeTuning(testSensors, 20, 20));
    System.out.println();
    
    List<Sensor> fullSensors = fullLoader.ml(Sensor::new);
    System.out.println(countExcludedPoints(fullSensors, 2000000));
    System.out.println(computeTuning(fullSensors, 4_000_000, 4_000_000));
  }
  
  private int countExcludedPoints(List<Sensor> sensors, int y) {
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
  
  private static Set<ImmutablePoint> intersections(ImmutablePoint a, int ra, ImmutablePoint b, int rb) {
    Set<ImmutablePoint> intersections = new HashSet<>();
    if ((ra + a.x()) >= b.x()) {
      {
        int ex = ((ra + rb) + (a.x() + b.x()) + (a.y() + b.y())) / 2;
        int ey = ((ra - rb) + (a.x() - b.x()) + (a.y() - b.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
      {
        int ex = ((ra + rb) + (a.x() + b.x()) - (a.y() - b.y())) / 2;
        int ey = ((rb - ra) + (b.x() - a.x()) + (b.y() + a.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
    }
    if (b.x() >= (ra + a.x())) {
      {
        int ex = ((ra - rb) + (a.x() + b.x()) + (a.y() - b.y())) / 2;
        int ey = ((ra + rb) + (a.x() - b.x()) + (a.y() + b.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
      {
        int ex = ((ra - rb) + (a.x() + b.x()) - (a.y() - b.y())) / 2;
        int ey = (-(rb + ra) + (b.x() - a.x()) + (b.y() + a.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
    }
    if (a.x() >= (ra + b.x())) {
      {
        int ex = ((rb - ra) + (b.x() + a.x()) + (b.y() - a.y())) / 2;
        int ey = ((rb + ra) + (b.x() - a.x()) + (b.y() + a.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
      {
        int ex = ((rb - ra) + (b.x() + a.x()) - (b.y() - a.y())) / 2;
        int ey = (-(rb + ra) + (a.x() - b.x()) + (a.y() + b.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
    }
    if ((ra + b.x()) >= a.x()) {
      {
        int ex = (-(rb + ra) + (b.x() + a.x()) + (b.y() - a.y())) / 2;
        int ey = ((ra - rb) + (b.x() - a.x()) + (b.y() + a.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
      {
        int ex = (-(rb + ra) + (a.x() - b.x()) + (a.y() - b.y())) / 2;
        int ey = ((rb - ra) + (a.x() + b.x()) + (a.y() + b.y())) / 2;
        intersections.add(new ImmutablePoint(ex, ey));
      }
    }
    return intersections;
  }
  
  private long computeTuning(List<Sensor> sensors, int maxX, int maxY) {
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
            return (x * 4_000_000L) + y;
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
    for (Sensor sensor : sensors) {
      if (computeManDist(point, sensor.location) <= sensor.exclusionDist) {
        return true;
      }
    }
    return false;
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
    
    private Set<ImmutablePoint> marginalIntersections(Sensor other) {
      int dist = computeManDist(location, other.location);
      if (dist > exclusionDist + other.exclusionDist) {
        return Set.of();
      }
      Set<ImmutablePoint> intersections = new HashSet<>();
      
      return intersections;
    }
  }
}
