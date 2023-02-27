package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultimap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import org.apache.commons.lang3.tuple.Pair;

class Day18 extends Base2018 {
  @Override
  protected void run() {
    State[][] map = loadInit(fullLoader.ml());
    Pair<Map<Integer, Integer>, TreeMultimap<Integer, Integer>> results = getScoresUntil3Repeat(map);
    Map<Integer, Integer> genToScore = results.getLeft();
    TreeMultimap<Integer, Integer> scoresFromGen = results.getRight();

    int tripleRepeatScore =
        scoresFromGen.keySet().stream().filter((score) -> scoresFromGen.get(score).size() == 3).findFirst().get();
    NavigableSet<Integer> gensWithTripleRepeatScore = scoresFromGen.get(tripleRepeatScore);
    int firstSeenGen = gensWithTripleRepeatScore.first();
    int secondSeenGen = gensWithTripleRepeatScore.last(); // the one in the middle is actually a different tree/lumber
    int targetGen = firstSeenGen + ((1_000_000_000 - firstSeenGen) % (secondSeenGen - firstSeenGen));

    System.out.println(genToScore.get(10));
    System.out.println(genToScore.get(targetGen));
  }

  private Pair<Map<Integer, Integer>, TreeMultimap<Integer, Integer>> getScoresUntil3Repeat(State[][] map) {
    Map<Integer, Integer> genToScore = new HashMap<>();
    TreeMultimap<Integer, Integer> scoresFromGen = TreeMultimap.create();
    State[][] next = new State[map.length][map[0].length];
    for (int gen = 1; true; ++gen) {
      runGen(map, next);
      State[][] swap = map;
      map = next;
      next = swap;

      int score = score(map);
      genToScore.put(gen, score);
      scoresFromGen.put(score, gen);
      if (scoresFromGen.get(score).size() == 3) {
        return Pair.of(genToScore, scoresFromGen);
      }
    }
  }

  private void runGen(State[][] map, State[][] next) {
    for (int y = 0; y < map.length; ++y) {
      for (int x = 0; x < map[0].length; ++x) {
        State state = map[y][x];
        Multiset<State> neighbors = countNeighbors(map, x, y);
        if (state == State.Open) {
          next[y][x] = (neighbors.count(State.Trees) >= 3) ? State.Trees : State.Open;
        }
        else if (state == State.Trees) {
          next[y][x] = (neighbors.count(State.Lumberyard) >= 3) ? State.Lumberyard : State.Trees;
        }
        else if (state == State.Lumberyard) {
          next[y][x] = ((neighbors.count(State.Lumberyard) >= 1) && (neighbors.count(State.Trees) >= 1))
              ? State.Lumberyard
              : State.Open;
        }
        else {
          throw fail(state);
        }
      }
    }
  }

  private int score(State[][] map) {
    Multiset<State> states = HashMultiset.create();
    Arrays.stream(map).flatMap(Arrays::stream).forEach(states::add);
    return states.count(State.Trees) * states.count(State.Lumberyard);
  }

  private Multiset<State> countNeighbors(State[][] map, int x, int y) {
    Multiset<State> neighbors = EnumMultiset.create(State.class);
    for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
      int nx = x + dir.dx();
      int ny = y + dir.dy();
      neighbors.add(getState(map, nx, ny));
      dir = dir.turnRight();
      neighbors.add(getState(map, nx + dir.dx(), ny + dir.dy()));
    }
    return neighbors;
  }

  private State getState(State[][] map, int x, int y) {
    return ((0 <= x) && (x < map[0].length) && (0 <= y) && (y < map.length)) ? map[y][x] : State.Open;
  }

  private State[][] loadInit(List<String> lines) {
    Map<Character, State> stateLookup = EnumUtils.getLookup(State.class);
    State[][] map = new State[lines.size()][lines.get(0).length()];
    for (int y = 0; y < lines.size(); ++y) {
      String line = lines.get(y);
      for (int x = 0; x < line.length(); ++x) {
        map[y][x] = stateLookup.get(line.charAt(x));
      }
    }
    return map;
  }

  private enum State implements HasId<Character> {
    Open('.'),
    Trees('|'),
    Lumberyard('#');

    private final char marker;

    private State(char marker) {
      this.marker = marker;
    }

    @Override
    public Character getId() {
      return marker;
    }
  }
}
