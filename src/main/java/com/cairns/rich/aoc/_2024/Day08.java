package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Range;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Antennas are around the city creating antinodes with their frequencies.
 * We need to count the number of locations that have antinodes.
 */
class Day08 extends Base2024 {
  /**
   * Simple frequencies cause antinodes exactly one magnitude away from their antennas.
   */
  @Override
  protected Object part1(Loader loader) {
    return countAllAntinodes(loader, Range.singleton(1));
  }

  /**
   * Resonant frequencies cause antinodes at all magnitudes away from their antennas.
   */
  @Override
  protected Object part2(Loader loader) {
    return countAllAntinodes(loader, Range.atLeast(0));
  }

  /**
   * Helper function to count the number of grid locations that have antinodes from the given loader.
   * Only magnitudes in the given range will be considered.
   */
  private int countAllAntinodes(Loader loader, Range<Integer> magRange) {
    City city = new City(loader);
    Set<ImmutablePoint> antinodes = new HashSet<>();
    for (char signal : city.antennasBySignal.keySet()) {
      List<ImmutablePoint> antennas = city.antennasBySignal.get(signal);
      for (int i = 0; i < antennas.size(); ++i) {
        ImmutablePoint antennaI = antennas.get(i);
        for (int j = i + 1; j < antennas.size(); ++j) {
          ImmutablePoint antennaJ = antennas.get(j);
          findAntinodesFromPair(city, antinodes, antennaI, antennaJ, magRange);
          findAntinodesFromPair(city, antinodes, antennaJ, antennaI, magRange);
        }
      }
    }
    return antinodes.size();
  }

  /**
   * Adds all of the antinodes from the given antenna pair in reference to the first antenna.
   * An antinode occurs at every multiple of the antenna's (dx, dy) delta from the antennas.
   * Adds all antinodes from the first antenna that are on the grid and in the given magRange.
   */
  private void findAntinodesFromPair(
      City city,
      Set<ImmutablePoint> antinodes,
      ImmutablePoint antennaI,
      ImmutablePoint antennaJ,
      Range<Integer> magRange
  ) {
    int dx = antennaI.x() - antennaJ.x();
    int dy = antennaI.y() - antennaJ.y();
    for (int mag = magRange.lowerEndpoint(); magRange.contains(mag); ++mag) {
      ImmutablePoint antinode = new ImmutablePoint(
          antennaI.x() + mag * dx,
          antennaI.y() + mag * dy
      );
      if (!Grid.isValid(city.grid, antinode)) {
        break;
      }
      antinodes.add(antinode);
    }
  }

  /**
   * Container class describing the city as well as the antenna locations grouped by signal type.
   */
  private static final class City {
    private final char[][] grid;
    private final ArrayListMultimap<Character, ImmutablePoint> antennasBySignal = ArrayListMultimap.create();

    private City(Loader loader) {
      this.grid = loader.ml(String::toCharArray).stream().toArray(char[][]::new);
      for (int row = 0; row < grid.length; ++row) {
        for (int col = 0; col < grid[0].length; ++col) {
          char ch = grid[row][col];
          if (ch != '.') {
            antennasBySignal.put(ch, new ImmutablePoint(col, row));
          }
        }
      }
    }
  }
}
