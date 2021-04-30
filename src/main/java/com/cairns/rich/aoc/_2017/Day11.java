package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.SHexDir;

class Day11 extends Base2017 {
  @Override
  protected void run() {
    List<SHexDir> steps = parse(fullLoader.ml());
    MutablePoint location = new MutablePoint(0, 0);
    int maxDistance = 0;
    for (SHexDir step : steps) {
      location.move(step);
      maxDistance = Math.max(maxDistance, stepsBackToMiddle(location));
    }
    System.out.println(location);
    System.out.println("Finished: " + stepsBackToMiddle(location) + " steps away");
    System.out.println("Got Max: " + maxDistance + " steps away");
  }
  
  private int stepsBackToMiddle(MutablePoint location) {
    int x = Math.abs(location.x());
    int y = Math.abs(location.y());
    return Math.min(x, y) // steps together
         + ((x > y) ? x - y : (y - x) / 2); 
  }
  
  private List<SHexDir> parse(List<String> lines) {
    return Arrays.stream(lines.get(0).split(",")).map(EnumUtils.getLookup(SHexDir.class)::get).collect(Collectors.toList());
  }
}
