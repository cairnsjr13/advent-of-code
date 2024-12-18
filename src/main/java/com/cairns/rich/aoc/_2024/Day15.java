package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * An out of control robot is moving about the grid pushing boxes if not blocked.  We need
 * to simulate the robot's movements to understand what the grid looks like when its done.
 */
class Day15 extends Base2024 {
  private static final Map<Character, ReadDir> dirLookup = Map.of(
      '^', ReadDir.Up,
      '>', ReadDir.Right,
      'v', ReadDir.Down,
      '<', ReadDir.Left
  );
  private static final Map<Character, int[]> doubleBoxAnchorColumnLookup = Map.of(
      '[', new int[] { 0, 1 },
      ']', new int[] { -1, 0 }
  );
  private static final EnumMap<ReadDir, Comparator<ImmutablePoint>> upDownSortOrders = new EnumMap<>(Map.of(
      ReadDir.Up, Comparator.comparingInt(ImmutablePoint::y),
      ReadDir.Down, Comparator.comparingInt(ImmutablePoint::y).reversed()
  ));

  /**
   * The robot will move boxes of width 1 on the original grid spec as long as they are not blocked.
   */
  @Override
  protected Object part1(Loader loader) {
    return doAllMovesAndComputeGps(loader, this::moveWithThinBoxes, 'O', Map.of(
        '#', new char[] { '#' },
        'O', new char[] { 'O' },
        '.', new char[] { '.' },
        '@', new char[] { '@' }
    ));
  }

  /**
   * Simulates one move in the given direction if the robot is not blocked.  The robot will move an
   * entire line of boxes as long as they are not blocked (or if there is no boxes in that direction).
   *
   * Note: while it would be possible to use this method in the wide box case for Left/Right
   *       movement, the implementation would have to do a full line loop instead of just altering
   *       the last box's position (because "[]" is not consistent when moving one spot).
   */
  private void moveWithThinBoxes(State state, ReadDir move) {
    int numThinBoxes = numBoxSpotsInDir(state, move);
    if (numThinBoxes >= 0) {
      if (numThinBoxes >= 1) {
        int openY = state.robot.y() + (1 + numThinBoxes) * move.dy();
        int openX = state.robot.x() + (1 + numThinBoxes) * move.dx();
        state.grid[openY][openX] = 'O';
      }
      state.moveRobot(move);
    }
  }

  /**
   * The robot will move boxes of width 2 on a scaled grid as long as they are not blocked.
   */
  @Override
  protected Object part2(Loader loader) {
    return doAllMovesAndComputeGps(loader, this::moveWithWideBoxes, '[', Map.of(
        '#', new char[] { '#', '#' },
        'O', new char[] { '[', ']' },
        '.', new char[] { '.', '.' },
        '@', new char[] { '@', '.' }
    ));
  }

  /**
   * Helper method to compute a {@link State} object from the given loader and parseMap,
   * execute all of it's move instructions with the given mover operation, and then return
   * the {@link State#gps(char)} score computed with the given box anchor character.
   */
  private int doAllMovesAndComputeGps(
      Loader loader,
      BiConsumer<State, ReadDir> mover,
      char boxChar,
      Map<Character, char[]> parseMap
  ) {
    State state = new State(loader, parseMap);
    for (ReadDir move : state.moves) {
      mover.accept(state, move);
    }
    return state.gps(boxChar);
  }

  /**
   * Simulates one move in the given direction if the robot is not blocked  The robot will move an
   * entire group of boxes as long as they are not blocked (or if there are no boxes in that direction).
   * In the Left/Right direction, simple line pushing will work.  However, because wide boxes have width
   * 2 and move as a unit, both columns of the box must be clear for movement Up/Down.  This can be done
   * by recursively finding all boxes involved in both columns the box resides in.  If ALL of the involved
   * boxes are clear, we can move the boxes by starting at the farthest box in the move direction.
   */
  private void moveWithWideBoxes(State state, ReadDir move) {
    if ((move == ReadDir.Left) || (move == ReadDir.Right)) {
      int numBoxSpots = numBoxSpotsInDir(state, move);
      if (numBoxSpots >= 0) {
        if (numBoxSpots >= 1) {
          for (int delta = numBoxSpots + 1; delta > 0; --delta) {
            state.grid[state.robot.y()][state.robot.x() + delta * move.dx()] =
                state.grid[state.robot.y()][state.robot.x() + (delta - 1) * move.dx()];
          }
        }
        state.moveRobot(move);
      }
    }
    else {
      Set<ImmutablePoint> doubleBoxesToMove = new HashSet<>();
      if (findDoubleBoxesToMove(doubleBoxesToMove, state.grid, state.robot.y() + move.dy(), state.robot.x(), move)) {
        List<ImmutablePoint> sortedDoubleBoxesToMove = new ArrayList<>(doubleBoxesToMove);
        sortedDoubleBoxesToMove.sort(upDownSortOrders.get(move));
        for (ImmutablePoint doubleBox : sortedDoubleBoxesToMove) {
          state.grid[doubleBox.y()][doubleBox.x() + 0] = '.';
          state.grid[doubleBox.y()][doubleBox.x() + 1] = '.';
          state.grid[doubleBox.y() + move.dy()][doubleBox.x() + 0] = '[';
          state.grid[doubleBox.y() + move.dy()][doubleBox.x() + 1] = ']';
        }
        state.moveRobot(move);
      }
    }
  }

  /**
   * Counts the number of unblocked box spots in the given direction.
   * Will return 0 if the spot next to the robot is open. (does not indicate blocked)
   * Will return -1 if the row is blocked.
   * Note that this can only be called in the Up/Down direction if we are dealing with single width boxes.
   */
  private int numBoxSpotsInDir(State state, ReadDir move) {
    for (int mag = 1; true; ++mag) {
      char spot = state.grid[state.robot.y() + mag * move.dy()][state.robot.x() + mag * move.dx()];
      if (spot == '.') {
        return mag - 1;
      }
      else if (spot == '#') {
        return -1;
      }
    }
  }

  /**
   * Recursive method to find all of the box locations involved in the given move from the given location.
   * Because wide boxes are width 2, both columns must be recursively checked and have their involved
   * locations added for successful block checking and movement.  The involved locations will be added
   * to the given set but movement is only valid if the return of this method is true.
   */
  private boolean findDoubleBoxesToMove(
      Set<ImmutablePoint> doubleBoxesToMove,
      char[][] grid,
      int row,
      int col,
      ReadDir move
  ) {
    char val = grid[row][col];
    if (val == '.') {
      return true;
    }
    else if (val == '#') {
      return false;
    }
    else if (doubleBoxAnchorColumnLookup.containsKey(val)) {
      int[] columnOffsets = doubleBoxAnchorColumnLookup.get(val);
      doubleBoxesToMove.add(new ImmutablePoint(col + columnOffsets[0], row));
      return Arrays.stream(columnOffsets)
          .allMatch((offset) -> findDoubleBoxesToMove(doubleBoxesToMove, grid, row + move.dy(), col + offset, move));
    }
    throw fail("Unknown char: " + val);
  }

  /**
   * Container class holding the state for a grid and robot.
   * Note that the robot position is a {@link MutablePoint} and should NOT be used for any hashing.
   */
  private static final class State {
    private final char[][] grid;
    private final List<ReadDir> moves;
    private final MutablePoint robot;

    private State(Loader loader, Map<Character, char[]> parseMap) {
      int widthFactor = parseMap.get('.').length;
      List<String> lines = loader.ml();
      int blankIndex = lines.indexOf("");

      List<String> gridLines = lines.subList(0, blankIndex);
      this.grid = new char[gridLines.size()][gridLines.get(0).length() * widthFactor];
      fillGrid(parseMap, widthFactor, gridLines);
      this.moves = lines.subList(blankIndex + 1, lines.size()).stream().collect(Collectors.joining()).chars()
          .mapToObj((ch) -> dirLookup.get((char) ch))
          .collect(Collectors.toList());
      this.robot = findRobot();
    }

    /**
     * Computes a scaled grid by applying the given width factor and parse map for each character in the original grid.
     */
    private void fillGrid(Map<Character, char[]> parseMap, int widthFactor, List<String> gridLines) {
      for (int row = 0; row < gridLines.size(); ++row) {
        for (int col = 0; col < gridLines.get(0).length(); ++col) {
          char[] toWrite = parseMap.get(gridLines.get(row).charAt(col));
          for (int offset = 0; offset < toWrite.length; ++offset) {
            grid[row][col * widthFactor + offset] = toWrite[offset];
          }
        }
      }
    }

    /**
     * Search method that returns a {@link MutablePoint} where the '@' character is found for the robot.
     */
    private MutablePoint findRobot() {
      for (int row = 0; row < grid.length; ++row) {
        for (int col = 0; col < grid[0].length; ++col) {
          if (grid[row][col] == '@') {
            return new MutablePoint(col, row);
          }
        }
      }
      throw fail();
    }

    /**
     * Convenience method to move the robot's position while updating the grid with the proper markers.
     */
    private void moveRobot(ReadDir move) {
      grid[robot.y()][robot.x()] = '.';
      robot.move(move);
      grid[robot.y()][robot.x()] = '@';
    }

    /**
     * The "score" for a grid is the sum of scores for all boxes. A box is considered "anchored" by the given
     * character and has a score of 100 times its distance from the top plus the distance from the left.
     */
    private int gps(char boxChar) {
      int gps = 0;
      for (int row = 0; row < grid.length; ++row) {
        for (int col = 0; col < grid[0].length; ++col) {
          if (grid[row][col] == boxChar) {
            gps += 100 * row + col;
          }
        }
      }
      return gps;
    }
  }
}
