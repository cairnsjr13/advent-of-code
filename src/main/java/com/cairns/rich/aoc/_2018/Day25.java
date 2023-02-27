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
    List<List<Star>> constellations = new LinkedList<>();
    for (Star star : stars) {
      Iterator<List<Star>> itr = constellations.iterator();
      List<Star> firstConnected = null;
      while (itr.hasNext()) {
        List<Star> candidate = itr.next();
        if (candidate.stream().anyMatch((cStar) -> cStar.manhattan(star) <= 3)) {
          if (firstConnected == null) {
            firstConnected = candidate;
          }
          else {
            firstConnected.addAll(candidate);
            itr.remove();
          }
        }
      }
      if (firstConnected == null) {
        firstConnected = new ArrayList<>();
        constellations.add(firstConnected);
      }
      firstConnected.add(star);
    }
    System.out.println(constellations.size());
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
