package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.UHexDir;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

class Day24 extends Base2020 {
  private static final UHexDir[] directions = EnumUtils.enumValues(UHexDir.class);
  private static final Map<Boolean, BiConsumer<Set<ImmutablePoint>, ImmutablePoint>> flipActions = Map.of(
      true, Set::remove,
      false, Set::add
  );

  @Override
  protected void run() {
    List<List<UHexDir>> tileSpecs = fullLoader.ml(this::parse);
    Set<ImmutablePoint> initialBlackTiles = getInitialBlackTiles(tileSpecs);
    System.out.println(initialBlackTiles.size());
    System.out.println(getNumBlackTilesAfter100Days(initialBlackTiles));
  }

  private int getNumBlackTilesAfter100Days(Set<ImmutablePoint> blackTiles) {
    Set<ImmutablePoint> temp = new HashSet<>();
    for (int i = 0; i < 100; ++i) {
      temp.clear();
      for (ImmutablePoint candidatePoint : getAllCandidatePoints(blackTiles)) {
        long numNeighbors = countNeighbors(blackTiles, candidatePoint);
        if ((numNeighbors == 2) || ((numNeighbors == 1) && blackTiles.contains(candidatePoint))) {
          temp.add(candidatePoint);
        }
      }
      Set<ImmutablePoint> cache = blackTiles;
      blackTiles = temp;
      temp = cache;
    }
    return blackTiles.size();
  }

  private Set<ImmutablePoint> getInitialBlackTiles(List<List<UHexDir>> tileSpecs) {
    Set<ImmutablePoint> blackTiles = new HashSet<>();
    for (List<UHexDir> tileSpec : tileSpecs) {
      MutablePoint total = new MutablePoint(0, 0);
      tileSpec.forEach(total::move);
      ImmutablePoint flip = new ImmutablePoint(total);
      flipActions.get(blackTiles.contains(flip)).accept(blackTiles, flip);
    }
    return blackTiles;
  }

  private long countNeighbors(Set<ImmutablePoint> blackTiles, ImmutablePoint inspect) {
    return Arrays.stream(directions).filter((dir) -> blackTiles.contains(inspect.move(dir))).count();
  }

  private Set<ImmutablePoint> getAllCandidatePoints(Set<ImmutablePoint> blackTiles) {
    Set<ImmutablePoint> candidatePoints = new HashSet<>();
    for (ImmutablePoint blackTile : blackTiles) {
      for (UHexDir direction : directions) {
        candidatePoints.add(blackTile.move(direction));
      }
      candidatePoints.add(blackTile);
    }
    return candidatePoints;
  }

  private List<UHexDir> parse(String spec) {
    List<UHexDir> directions = new ArrayList<>();
    for (int i = 0; i < spec.length(); ++i) {
      char ch = spec.charAt(i);
      if (ch == 'e') {
        directions.add(UHexDir.East);
      }
      else if (ch == 'w') {
        directions.add(UHexDir.West);
      }
      else if (ch == 'n') {
        directions.add((spec.charAt(i + 1) == 'e') ? UHexDir.NorthEast : UHexDir.NorthWest);
        ++i;
      }
      else if (ch == 's') {
        directions.add((spec.charAt(i + 1) == 'e') ? UHexDir.SouthEast : UHexDir.SouthWest);
        ++i;
      }
    }
    return directions;
  }
}
