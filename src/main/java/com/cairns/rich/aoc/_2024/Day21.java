package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
/**
 * There are a chain of button pads and robots we need to control to push a final code.  Each robot controls the
 * next in the chain.  To find the minimal presses for each code, we must cache the cost of each through the layers.
 */
class Day21 extends Base2024 {
  private static final ConfigToken<Integer> depthToken = ConfigToken.of("depth", Integer::parseInt);

  private static final Table<Character, Character, List<String>> numPadRoutes = initRoutes(
      "789",
      "456",
      "123",
      " 0A"
  );
  private static final Table<Character, Character, List<String>> dirPadRoutes = initRoutes(
      " ^A",
      "<v>"
  );

  /**
   * Computes the minimal moves needed by the human to get a final code 3 layers deep.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeComplexityTotal(loader);
  }

  /**
   * Computes the minimal moves needed by the human to get a final code 26 layers deep.
   */
  @Override
  protected Object part2(Loader loader) {
    return computeComplexityTotal(loader);
  }

  /**
   * Computes the sum of complexities for each code where the complexity is the minimum moves times the numeric code.
   * This is done optimally by caching the sequences between directional moves.
   */
  private long computeComplexityTotal(Loader loader) {
    int depth = loader.getConfig(depthToken);

    Map<Integer, Table<Character, Character, Long>> depthCache = IntStream.rangeClosed(0, depth).boxed()
        .collect(Collectors.toMap(Function.identity(), (l) -> HashBasedTable.create()));
    return loader.ml().stream()
        .mapToLong(
            (code) -> cost(numPadRoutes, dirPadRoutes, depthCache, depth, code)
                    * Integer.parseInt(code.substring(0, 3))
        ).sum();
  }

  /**
   * Recursive solve method that computes the minimal cost of the given sequence at the given depth.
   * The route is assumed to begin at "A".  The optimal path between each button is considered and
   * summed up to get the total sequence cost.  The base case is simply the final pad and then length.
   */
  private long cost(
    Table<Character, Character, List<String>> initRoutes,
    Table<Character, Character, List<String>> dirPadRoutes,
    Map<Integer, Table<Character, Character, Long>> depthCache,
    int depth,
    String sequence
  ) {
    if (depth == 0) {
      return sequence.length();
    }
    String route = "A" + sequence;
    long cost = 0;
    for (int i = 0; i < route.length() - 1; ++i) {
      char from = route.charAt(i);
      char to = route.charAt(i + 1);
      if (!depthCache.get(depth).contains(from, to)) {
        depthCache.get(depth).put(from, to, initRoutes.get(from, to).stream()
            .mapToLong((next) -> cost(dirPadRoutes, dirPadRoutes, depthCache, depth - 1, next))
            .min().getAsLong()
        );
      }
      cost += depthCache.get(depth).get(from, to);
    }
    return cost;
  }

  /**
   * Helper method to convert a string grid into a table of buttons to optimal routes.
   */
  private static Table<Character, Character, List<String>> initRoutes(String... grid) {
    ImmutablePoint gap = null;
    Map<Character, ImmutablePoint> pad = new HashMap<>();
    for (int y = 0; y < grid.length; ++y) {
      String row = grid[grid.length - y - 1];
      for (int x = 0; x < row.length(); ++x) {
        ImmutablePoint at = new ImmutablePoint(x, y);
        char button = row.charAt(x);
        if (button == ' ') {
          gap = at;
        }
        else {
          pad.put(button, at);
        }
      }
    }
    return initRoutes(gap, pad);
  }

  /**
   * Computes a list of optimal routes between every pair of buttons using directional moves (^>v<).
   * An optimal path MUST be all directions grouped together (zig-zags require more moves up the layers).
   * Because of this there only two possibilities horizontal first, then vertical and visa-versa.
   * The gap given will be off limits and either path crossing it will be excluded.
   * All sequences will have an "A" appended to the end (including no movement).
   */
  private static Table<Character, Character, List<String>> initRoutes(ImmutablePoint gap, Map<Character, ImmutablePoint> pad) {
    Table<Character, Character, List<String>> routes = HashBasedTable.create();
    ImmutablePoint finalGap = gap;
    pad.forEach((from, fromPos) -> pad.forEach((to, toPos) -> {
      routes.put(from, to, new ArrayList<>());
      if (from == to) {
        routes.get(from, to).add("A");
      }
      else {
        int horizDist = toPos.x() - fromPos.x();
        Character horizDir = (horizDist > 0) ? '>' : '<';
        String horizMoves = StringUtils.repeat(horizDir, Math.abs(horizDist));

        int vertDist = toPos.y() - fromPos.y();
        Character vertDir = (vertDist > 0) ? '^' : 'v';
        String vertMoves = StringUtils.repeat(vertDir, Math.abs(vertDist));

        ImmutablePoint horizFirstCorner = new ImmutablePoint(toPos.x(), fromPos.y());
        if ((horizDist != 0) && !finalGap.equals(horizFirstCorner)) {
          routes.get(from, to).add(horizMoves + vertMoves + "A");
        }

        ImmutablePoint vertFirstCorner = new ImmutablePoint(fromPos.x(), toPos.y());
        if ((vertDist != 0) && !finalGap.equals(vertFirstCorner)) {
          routes.get(from, to).add(vertMoves + horizMoves + "A");
        }
      }
    }));
    return routes;
  }
}
