package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Grid;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntBiFunction;

/**
 * Our crops need fencing around connected components.  There are different cost models that need to be computed.
 */
class Day12 extends Base2024 {
  /**
   * Price calculation is done by multiplying each region's perimeter and area.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeFenceCost(loader, this::simpleCost);
  }

  /**
   * Price calculation is done by multiply each region's number of sides and area.
   */
  @Override
  protected Object part2(Loader loader) {
    return computeFenceCost(loader, this::bulkCost);
  }

  /**
   * Helper method to find all regions in the input and use the given price to calculate total fence cost.
   */
  private int computeFenceCost(Loader loader, ToIntBiFunction<char[][], Set<ImmutablePoint>> regionPricer) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    Set<ImmutablePoint> visited = new HashSet<>();
    int cost = 0;
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        ImmutablePoint start = new ImmutablePoint(col, row);
        if (!visited.contains(start)) {
          cost += regionPricer.applyAsInt(grid, findWholeRegion(grid, visited, start));
        }
      }
    }
    return cost;
  }

  /**
   * Uses bfs to find all points that belong to the region containing the given point.
   * A region is defined as points containing the same crop when touching in {@link ReadDir}s.
   */
  private Set<ImmutablePoint> findWholeRegion(char[][] grid, Set<ImmutablePoint> visited, ImmutablePoint start) {
    Set<ImmutablePoint> region = new HashSet<>();
    bfs(
        start,
        (cur) -> false,
        (cur, registrar) -> {
          region.add(cur);
          char crop = grid[cur.y()][cur.x()];
          for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
            ImmutablePoint next = cur.move(dir);
            if (!visited.contains(next) && Grid.isValid(grid, next) && (grid[next.y()][next.x()] == crop)) {
              visited.add(next);
              registrar.accept(next);
            }
          }
        }
    );
    return region;
  }

  /**
   * The simple way of computing cost is multiplying the area by the total perimeter of the region.
   * The perimeter includes both internal and external edges.
   */
  private int simpleCost(char[][] grid, Set<ImmutablePoint> region) {
    int perim = 0;
    for (ImmutablePoint cur : region) {
      for (ReadDir dirOfEdge : EnumUtils.enumValues(ReadDir.class)) {
        if (isEdge(grid, cur, dirOfEdge)) {
          ++perim;
        }
      }
    }
    return perim * region.size();
  }

  /**
   * The bulk way of computing cost is multiplying the area by the number of sides of the region.
   * A side is a continuous line of fence in the same {@link ReadDir}.  We can compute this by
   * examining every non-examined point in the region and seeing if it is an edge in all {@link ReadDir}s.
   * If it a new edge, we increase the number of sides and then mark every neighboring point parallel
   * to that edge as being examined.  A point is considered to be part of the edge if it part of the region
   * and has a non-region point in the same direction as the edge (which handles corner points).
   */
  private int bulkCost(char[][] grid, Set<ImmutablePoint> region) {
    int sides = 0;
    Multimap<ImmutablePoint, ReadDir> edgesComputed = HashMultimap.create();
    for (ImmutablePoint cur : region) {
      for (ReadDir dirOfEdge : EnumUtils.enumValues(ReadDir.class)) {
        if (!edgesComputed.containsEntry(cur, dirOfEdge)) {
          if (isEdge(grid, cur, dirOfEdge)) {
            ++sides;
            addAllNextToAlongEdge(grid, region, edgesComputed, cur, dirOfEdge);
          }
        }
      }
    }
    return sides * region.size();
  }

  /**
   * Traverses both parallel directions of the edge from the given start point to add
   * all edge points to the computed {@link Multimap}.  A point is considered on the
   * edge if it along the unbroken direction parallel to the edge and has a non-region
   * point (or off grid) in the same direction of the edge.
   */
  private void addAllNextToAlongEdge(
      char[][] grid,
      Set<ImmutablePoint> region,
      Multimap<ImmutablePoint, ReadDir> edgesComputed,
      ImmutablePoint start,
      ReadDir dirOfEdge
  ) {
    for (ReadDir moveDir : List.of(dirOfEdge.turnLeft(), dirOfEdge.turnRight())) {
      for (ImmutablePoint cur = start; region.contains(cur) && isEdge(grid, cur, dirOfEdge); cur = cur.move(moveDir)) {
        edgesComputed.put(cur, dirOfEdge);
      }
    }
  }

  /**
   * Returns true if the given position is an edge in the given direction.
   * An edge is defined
   */
  boolean isEdge(char[][] grid, ImmutablePoint cur, ReadDir dirOfEdge) {
    ImmutablePoint neighbor = cur.move(dirOfEdge);
    return !Grid.isValid(grid, neighbor)
        || (grid[cur.y()][cur.x()] != grid[neighbor.y()][neighbor.x()]);
  }
}
