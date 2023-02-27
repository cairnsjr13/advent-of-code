package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day10 extends Base2018 {
  @Override
  protected void run() {
    List<Light> lights = fullLoader.ml(Light::new);
    int numTicksToMessage = numTicksToClosest(lights);
    lights.forEach(Light::unTick);
    print(lights);
    System.out.println(numTicksToMessage);
  }

  private int numTicksToClosest(List<Light> lights) {
    int previousDistance = getVertDiff(lights);
    for (int numTicks = 0; true; ++numTicks) {
      lights.forEach(Light::tick);
      int currentDistance = getVertDiff(lights);
      if (currentDistance > previousDistance) {
        return numTicks;
      }
      previousDistance = currentDistance;
    }
  }

  private int getVertDiff(List<Light> lights) {
    return getMax(lights, (light) -> light.position.y()).position.y()
         - getMin(lights, (light) -> light.position.y()).position.y();
  }

  // TODO: do better print (use utf8 black)
  private void print(List<Light> lights) {
    Set<MutablePoint> points = lights.stream().map((light) -> light.position).collect(Collectors.toSet());
    int minX = getMin(points, (p) -> p.x()).x();
    int maxX = getMax(points, (p) -> p.x()).x();
    int minY = getMin(points, (p) -> p.y()).y();
    int maxY = getMax(points, (p) -> p.y()).y();
    for (int y = minY; y <= maxY; ++y) {
      for (MutablePoint point = new MutablePoint(minX, y); point.x() <= maxX; point.move(ReadDir.Right)) {
        System.out.print((points.contains(point)) ? '#' : ' ');
      }
      System.out.println();
    }
  }

  private static class Light {
    private static final String subPattern = "< *(-?\\d+), *(-?\\d+)>";
    private static final Pattern pattern = Pattern.compile("^position=" + subPattern + " velocity=" + subPattern + "$");

    private final MutablePoint position;
    private final ReadDir horizDir;
    private final int horizVelocity;
    private final ReadDir vertDir;
    private final int vertVelocity;

    private Light(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.position = new MutablePoint(num(matcher, 1), num(matcher, 2));
      int dh = num(matcher, 3);
      int dv = num(matcher, 4);
      this.horizDir = (dh < 0) ? ReadDir.Left : ReadDir.Right;
      this.horizVelocity = Math.abs(dh);
      this.vertDir = (dv < 0) ? ReadDir.Up : ReadDir.Down;
      this.vertVelocity = Math.abs(dv);
    }

    private void tick() {
      position.move(horizDir, horizVelocity);
      position.move(vertDir, vertVelocity);
    }

    private void unTick() {
      position.move(horizDir.turnAround(), horizVelocity);
      position.move(vertDir.turnAround(), vertVelocity);
    }
  }
}
