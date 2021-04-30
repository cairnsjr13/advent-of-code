package com.cairns.rich.aoc._2020;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Day17 extends Base2020 {
  @Override
  protected void run() {
    long start = System.currentTimeMillis();
    List<String> lines = fullLoader.ml();
    System.out.println("3d -> " + runSimulation(lines, 3));
    System.out.println("4d -> " + runSimulation(lines, 4));
    System.out.println("TIME: " + (System.currentTimeMillis() - start));
  }
  
  private int runSimulation(List<String> lines, int dimensions) {
    Set<Point> grid = parseGrid(lines, dimensions);
    System.out.println("\t0\t" + grid.size());
    for (int i = 1; i <= 6; ++i) {
      grid = nextStep(grid, dimensions);
      System.out.println("\t" + i + "\t" + grid.size());
    }
    return grid.size();
  }
  
  private Set<Point> nextStep(Set<Point> current, int dimensions) {
    Set<Point> next = new HashSet<>();
    for (Point candidatePoint : getAllCandidates(current, dimensions)) {
      long numNeighbors = countNumNeighbors(current, candidatePoint);
      if ((numNeighbors == 3) || ((numNeighbors == 2) && current.contains(candidatePoint))) {
        next.add(candidatePoint);
      }
    }
    return next;
  }
  
  private Set<Point> getAllCandidates(Set<Point> grid, int dimensions) {
    Set<Point> candidates = new HashSet<>(grid);
    List<Integer> built = new ArrayList<>();
    for (Point point : grid) {
      buildPoints(candidates, point, 0, built);
    }
    return candidates;
  }
  
  private void buildPoints(Set<Point> addTo, Point from, int coordIndex, List<Integer> built) {
    if (coordIndex < from.coords.size()) {
      for (int d = -1; d <= 1; ++d) {
        built.add(from.coords.get(coordIndex) + d);
        buildPoints(addTo, from, coordIndex + 1, built);
        built.remove(built.size() - 1);
      }
    }
    else if (!from.coords.equals(built)) {
      addTo.add(new Point(built));
    }
  }
  
  private long countNumNeighbors(Set<Point> grid, Point point) {
    Set<Point> neighbors = new HashSet<>();
    buildPoints(neighbors, point, 0, new ArrayList<>());
    return neighbors.stream().filter(grid::contains).count();
  }
  
  private Set<Point> parseGrid(List<String> lines, int dimensions) {
    Set<Point> grid = new HashSet<>();
    for (int y = 0; y < lines.size(); ++y) {
      String line = lines.get(y);
      for (int x = 0; x < line.length(); ++x) {
        if (line.charAt(x) == '#') {
          List<Integer> coords = new ArrayList<>();
          coords.add(x);
          coords.add(y);
          for (int i = 0; i < dimensions - 2; ++i) {
            coords.add(0);
          }
          grid.add(new Point(coords));
        }
      }
    }
    return grid;
  }
  
  private static class Point {
    private final List<Integer> coords;
    
    private Point(List<Integer> coords) {
      this.coords = new ArrayList<>(coords);
    }
    
    @Override
    public boolean equals(Object obj) {
      return coords.equals(((Point) obj).coords);
    }
    
    @Override
    public int hashCode() {
      return coords.hashCode();
    }
  }
}
