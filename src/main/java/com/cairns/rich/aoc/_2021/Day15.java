package com.cairns.rich.aoc._2021;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;

public class Day15 extends Base2021 {
  @Override
  protected void run() {
    int[][] risks = fullLoader.ml((line) -> line.chars().map((c) -> c - '0').toArray()).toArray(int[][]::new);
    System.out.println(getLowestRisk(1, risks));
    System.out.println(getLowestRisk(5, risks));
  }
  
  private int getLowestRisk(int repeat, int[][] risks) {
    Cave cave = new Cave(repeat, risks);
    Map<ImmutablePoint, Integer> shortestPaths = new HashMap<>();
    ImmutablePoint start = new ImmutablePoint(0, 0);
    shortestPaths.put(start, 0);

    PriorityQueue<ImmutablePoint> pq = new PriorityQueue<>(Comparator.comparing(shortestPaths::get));
    pq.offer(start);
    while (!pq.isEmpty()) {
      ImmutablePoint current = pq.poll();
      if (current.equals(cave.destination)) {
        return shortestPaths.get(current);
      }
      int shortestPathToCurrent = shortestPaths.get(current);
      for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
        ImmutablePoint next = current.move(dir);
        if (cave.isValid(next)) {
          int costThroughCurrent = shortestPathToCurrent + cave.get(next);
          if (costThroughCurrent < shortestPaths.getOrDefault(next, Integer.MAX_VALUE)) {
            pq.remove(next);
            shortestPaths.put(next, costThroughCurrent);
            pq.offer(next);
          }
        }
      }
    }
    throw fail("never found");
  }
  
  private static class Cave {
    private final int repeat;
    private final int[][] risks;
    private final ImmutablePoint destination;
    
    private Cave(int repeat, int[][] risks) {
      this.repeat = repeat;
      this.risks = risks;
      this.destination = new ImmutablePoint(risks.length * repeat - 1, risks[0].length * repeat - 1);
    }
    
    private int get(ImmutablePoint p) {
      int shiftedRisk = (risks[p.x() % risks.length][p.y() % risks[0].length])
                      + (p.x() / risks.length)
                      + (p.y() / risks[0].length);
      return (shiftedRisk - 1) % 9 + 1;
    }
    
    private boolean isValid(ImmutablePoint p) {
      return (0 <= p.x()) && (p.x() < risks.length * repeat)
          && (0 <= p.y()) && (p.y() < risks[0].length * repeat);
    }
  }
}
