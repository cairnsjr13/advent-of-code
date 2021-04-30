package com.cairns.rich.aoc._2018;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class Day25 extends Base2018 {
  @Override
  protected void run() {
    List<Star> stars = fullLoader.ml(Star::new);
    List<Constellation> constellations = new LinkedList<>();
    for (Star star : stars) {
      Iterator<Constellation> itr = constellations.iterator();
      Constellation firstConnected = null;
      while (itr.hasNext()) {
        Constellation candidate = itr.next();
        if (candidate.connects(star)) {
          if (firstConnected == null) {
            firstConnected = candidate;
          }
          else {
            firstConnected.stars.addAll(candidate.stars);
            itr.remove();
          }
        }
      }
      if (firstConnected == null) {
        firstConnected = new Constellation();
        constellations.add(firstConnected);
      }
      firstConnected.stars.add(star);
    }
    System.out.println(constellations.size());
  }
  
  private static class Constellation {
    private final List<Star> stars = new ArrayList<>();
    
    private boolean connects(Star candidate) {
      for (Star star : stars) {
        if (candidate.manhattan(star) <= 3) {
          return true;
        }
      }
      return false;
    }
  }
  
  private static class Star {
    private final int[] coords;
    
    private Star(String spec) {
      this.coords = Arrays.stream(spec.split(",")).mapToInt(Integer::parseInt).toArray();
    }
    
    private int manhattan(Star other) {
      int manhattan = 0;
      for (int i = 0; i < coords.length; ++i) {
        manhattan += Math.abs(coords[i] - other.coords[i]);
      }
      return manhattan;
    }
  }
}
