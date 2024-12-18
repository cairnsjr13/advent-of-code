package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.ToLongBiFunction;
import java.util.function.UnaryOperator;

/**
 * The reindeer Olympics is happening and computing best paths through the maze will help decide where to sit.
 */
class Day16 extends Base2024 {
  /**
   * The reindeer can take any of the "best score" paths through the maze.  We need to find the score of any.
   */
  @Override
  protected Object part1(Loader loader) {
    return findBestPathsAndComputeResult(loader, (distance, cur) -> cur.score);
  }

  /**
   * To decide where to sit, we must count all spots along any of the "best score" paths.
   * While executing the path search, we need to keep track of all paths to a state in
   * order to reconstruct all possible paths.  This is done by walking backwards from
   * the goal and adding all {@link ImmutablePoint} locations that we encounter.
   */
  @Override
  protected Object part2(Loader loader) {
    return findBestPathsAndComputeResult(loader, this::countSpotsAlongPath);
  }

  /**
   * Performs Dijkstra's algorithm to find the "best score" path from the start to the goal.
   * Moving forward costs 1 point and turning costs 1000 points.  The slightly modified algorithm
   * keeps track of all predecessor states to a state along with the score for path construction.
   */
  private long findBestPathsAndComputeResult(
      Loader loader,
      ToLongBiFunction<Map<MazeState, ToHere>, MazeState> resultsComputer
  ) {
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    ImmutablePoint goal = find(grid, 'E');
    ImmutablePoint start = find(grid, 'S');

    PriorityQueue<MazeState> pq = new PriorityQueue<>(Comparator.comparingLong((mz) -> mz.score));
    Map<MazeState, ToHere> distances = new HashMap<>();

    MazeState startState = new MazeState(start, ReadDir.Right, 0);
    pq.offer(startState);
    distances.put(startState, new ToHere(0));

    while (!pq.isEmpty()) {
      MazeState cur = pq.poll();
      if (goal.equals(cur.at)) {
        return resultsComputer.applyAsLong(distances, cur);
      }
      tryMove(grid, pq, distances, cur, MazeState::straight);
      tryMove(grid, pq, distances, cur, (mz) -> mz.turn(ReadDir::turnLeft));
      tryMove(grid, pq, distances, cur, (mz) -> mz.turn(ReadDir::turnRight));
    }
    throw fail("No path found");
  }

  /**
   * Attempts to add a state to the search queue from the given current state and given move.
   * It is important to add the cur state to the next state's predecessor list if the score
   * is equal to the best so far.  This allows a reverse path construction of all best paths.
   */
  private void tryMove(
      char[][] grid,
      PriorityQueue<MazeState> pq,
      Map<MazeState, ToHere> distances,
      MazeState cur,
      UnaryOperator<MazeState> move
  ) {
    MazeState next = move.apply(cur);
    if (grid[next.at.y()][next.at.x()] == '.') {
      if (distances.containsKey(next)) {
        ToHere existing = distances.get(next);
        if (next.score >= existing.score) {
          if (next.score == existing.score) {
            existing.predecessors.add(cur);
          }
          return;
        }
      }
      ToHere pathStats = new ToHere(next.score);
      pathStats.predecessors.add(cur);
      distances.put(next, pathStats);
      pq.offer(next);
    }
  }

  /**
   * Returns the number of unique spots along any of the best score paths that terminate
   * at the given finalState.  This is done by following the path backwards using the
   * given lookup map, which contains all predecessor states from a given state.
   */
  private int countSpotsAlongPath(Map<MazeState, ToHere> distances, MazeState finalState) {
    Set<ImmutablePoint> alongPath = new HashSet<>();
    Set<MazeState> toExplore = new HashSet<>();
    toExplore.add(finalState);
    while (!toExplore.isEmpty()) {
      Set<MazeState> nextToExplore = new HashSet<>();
      for (MazeState cur : toExplore) {
        alongPath.add(cur.at);
        nextToExplore.addAll(distances.get(cur).predecessors);
      }
      toExplore = nextToExplore;
    }
    return alongPath.size();
  }

  /**
   * Helper method to locate the given character in the given grid.
   */
  private ImmutablePoint find(char[][] grid, char search) {
    for (int row = 0; row < grid.length; ++row) {
      for (int col = 0; col < grid[0].length; ++col) {
        if (grid[row][col] == search) {
          grid[row][col] = '.';
          return new ImmutablePoint(col, row);
        }
      }
    }
    throw fail("Could not find '" + search + "'");
  }

  /**
   * Container class describing the score required to get to a state and all the possible states that can proceed it.
   * The predecessors set can be used to reconstruct ALL possible paths.
   */
  private static final class ToHere {
    private final long score;
    private final Set<MazeState> predecessors = new HashSet<>();

    private ToHere(long score) {
      this.score = score;
    }
  }

  /**
   * Container class describing a reindeer's location and facing along with the score required to get there.
   */
  private static final class MazeState {
    private final ImmutablePoint at;
    private final ReadDir facing;
    private final long score;

    private MazeState(
        ImmutablePoint at,
        ReadDir facing,
        long score
    ) {
      this.at = at;
      this.facing = facing;
      this.score = score;
    }

    @Override
    public boolean equals(Object other) {
      return at.equals(((MazeState) other).at)
          && facing.equals(((MazeState) other).facing);
    }

    @Override
    public int hashCode() {
      return at.hashCode() << 2 + facing.ordinal();
    }

    /**
     * Returns a new {@link MazeState} that moves one space forward from this state.
     */
    private MazeState straight() {
      return new MazeState(at.move(facing), facing, 1 + score);
    }

    /**
     * Returns a new {@link MazeState} that turns the reindeer by the given rotation from this state.
     */
    private MazeState turn(UnaryOperator<ReadDir> rotation) {
      return new MazeState(at, rotation.apply(facing), 1000 + score);
    }
  }
}
