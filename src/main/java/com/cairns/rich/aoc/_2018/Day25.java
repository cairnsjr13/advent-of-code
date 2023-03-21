package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

class Day25 extends Base2018 {
  @Override
  protected Object part1(Loader loader) {
    List<Star> stars = loader.ml(Star::new);
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
    return constellations.size();
  }

  private static class Star {
    private final int[] coords;

    private Star(String spec) {
      this.coords = Arrays.stream(spec.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private int manhattan(Star other) {
      return IntStream.range(0, coords.length).map((i) -> Math.abs(coords[i] - other.coords[i])).sum();
    }
  }
}
