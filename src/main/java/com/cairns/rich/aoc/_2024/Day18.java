package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bytes are falling into a memory grid over time.  We need to simulate where they fall and when to map paths.
 */
class Day18 extends Base2024 {
  private static final ConfigToken<Integer> widthToken = ConfigToken.of("width", Integer::parseInt);
  private static final ConfigToken<Integer> heightToken = ConfigToken.of("height", Integer::parseInt);
  private static final ConfigToken<Integer> numFallingToken = ConfigToken.of("numFalling", Integer::parseInt);
  private static final Pattern pattern = Pattern.compile("^(\\d+),(\\d+)$");

  /**
   * Simulates the first {@link #numFallingToken} bytes falling and finds the shortest path through the memory grid.
   */
  @Override
  protected Object part1(Loader loader) {
    int width = loader.getConfig(widthToken);
    int height = loader.getConfig(heightToken);
    int numFalling = loader.getConfig(numFallingToken);

    List<ImmutablePoint> bytes = loader.ml(this::parse);
    Set<ImmutablePoint> corrupted = new HashSet<>(bytes.subList(0, numFalling));
    return findShortestPath(width, height, corrupted).get().getNumSteps();
  }

  /**
   * Uses binary search to find the byte that finally blocks a path through the memory grid.
   * Because part1 is guaranteed to have a path, the lower bound can start with the initial corrupted zone.
   */
  @Override
  protected Object part2(Loader loader) {
    int width = loader.getConfig(widthToken);
    int height = loader.getConfig(heightToken);
    int numFalling = loader.getConfig(numFallingToken);

    List<ImmutablePoint> bytes = loader.ml(this::parse);
    int possibleWithIndex = numFalling;
    int blockedWithIndex = bytes.size() - 1;
    while (blockedWithIndex - possibleWithIndex > 1) {
      int candidateIndex = (blockedWithIndex + possibleWithIndex) / 2;
      Set<ImmutablePoint> corrupted = new HashSet<>(bytes.subList(0, candidateIndex + 1));
      if (findShortestPath(width, height, corrupted).isPresent()) {
        possibleWithIndex = candidateIndex;
      }
      else {
        blockedWithIndex = candidateIndex;
      }
    }
    ImmutablePoint fallingByte = bytes.get(blockedWithIndex);
    return fallingByte.x() + "," + fallingByte.y();
  }

  /**
   * Runs a bfs to find the shortest path through the memory grid with given corrupted bytes.
   * Returns a {@link SearchState} so callers can inspect existence as well as number of steps.
   */
  private Optional<SearchState<ImmutablePoint>> findShortestPath(
      int width,
      int height,
      Set<ImmutablePoint> corrupted
  ) {
    return bfs(
        new ImmutablePoint(0, 0),
        (new ImmutablePoint(width - 1, height - 1))::equals,
        (cur, registrar) -> {
          for (ReadDir move : EnumUtils.enumValues(ReadDir.class)) {
            if (isValid(width, height, cur, move)) {
              ImmutablePoint next = cur.move(move);
              if (!corrupted.contains(next)) {
                registrar.accept(next);
              }
            }
          }
        }
    );
  }

  /**
   * Helper method to determine if the point in the given direction from the given anchor point is on the board.
   */
  private boolean isValid(int width, int height, ImmutablePoint at, ReadDir move) {
    int x = at.x() + move.dx();
    int y = at.y() + move.dy();
    return (0 <= y) && (y < height)
        && (0 <= x) && (x < width);
  }

  /**
   * Input parsing method to get an {@link ImmutablePoint} from a comma separated "x,y" coordinate line.
   */
  private ImmutablePoint parse(String line) {
    Matcher matcher = matcher(pattern, line);
    return new ImmutablePoint(num(matcher, 1), num(matcher, 2));
  }
}
