package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

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
    return lights.stream().mapToInt((light) -> light.position.y()).max().getAsInt()
         - lights.stream().mapToInt((light) -> light.position.y()).min().getAsInt();
  }
  
  // TODO: do better print (use utf8 black)
  private void print(List<Light> lights) {
    Set<MutablePoint> points = lights.stream().map((light) -> light.position).collect(Collectors.toSet());
    int minX = lights.stream().mapToInt((light) -> light.position.x()).min().getAsInt();
    int maxX = lights.stream().mapToInt((light) -> light.position.x()).max().getAsInt();
    int minY = lights.stream().mapToInt((light) -> light.position.y()).min().getAsInt();
    int maxY = lights.stream().mapToInt((light) -> light.position.y()).max().getAsInt();
    for (int y = maxY; y >= minY; --y) {
      for (int x = minX; x <= maxX; ++x) {
        MutablePoint point = new MutablePoint(x, y);
        System.out.print((points.contains(point)) ? '#' : ' ');
      }
      System.out.println();
    }
  }
  
  private static class Light {
    private static final String subPattern = "< *(-?\\d+), *(-?\\d+)>";
    private static final Pattern pattern = Pattern.compile("^position=" + subPattern + " velocity=" + subPattern + "$");
    
    private final MutablePoint position;
    private final Pair<RelDir, Integer> horizontalVelocity;
    private final Pair<RelDir, Integer> verticalVelocity;
    
    private Light(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.position = new MutablePoint(num(matcher, 1), -num(matcher, 2));
      int dh = num(matcher, 3);
      int dv = num(matcher, 4);
      this.horizontalVelocity = Pair.of((dh < 0) ? RelDir.Left : RelDir.Right, Math.abs(dh));
      this.verticalVelocity = Pair.of((dv < 0) ? RelDir.Up : RelDir.Down, Math.abs(dv));
    }
    
    private void tick() {
      position.move(horizontalVelocity.getLeft(), horizontalVelocity.getRight());
      position.move(verticalVelocity.getLeft(), verticalVelocity.getRight());
    }
    
    private void unTick() {
      position.move(horizontalVelocity.getLeft().turnAround(), horizontalVelocity.getRight());
      position.move(verticalVelocity.getLeft().turnAround(), verticalVelocity.getRight());
    }
  }
}
