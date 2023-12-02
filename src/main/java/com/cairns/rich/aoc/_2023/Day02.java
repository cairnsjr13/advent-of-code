package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We are playing a game of hidden cubes with an elf.  We are given information
 * about what is in the bag and need to find out various information about the game.
 */
class Day02 extends Base2023 {
  private static final Map<String, Integer> colorCounts = Map.of("red", 12, "green", 13, "blue", 14);

  /**
   * Returns the sum of game ids who could have been played with {@link #colorCounts} boxes.
   * We can do this by simply ensuring every show in the game sees no more than the limits.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Game::new).stream()
        .filter((g) -> g.shows.stream().allMatch(
            (s) -> colorCounts.entrySet().stream().allMatch((cc) -> s.count(cc.getKey()) <= cc.getValue())
        )).mapToLong((g) -> g.id)
        .sum();
  }

  /**
   * Returns the sum of box powers from each game.  The box power of a game is the product
   * of each color's minimum need for the game.  We can compute this by finding the maximum
   * of  each color within the game, this is the minimum required for the whole game.
   */
  @Override
  protected Object part2(Loader loader) {
    return loader.ml(Game::new).stream()
        .mapToLong((g) -> colorCounts.keySet().stream()
            .mapToLong((color) -> g.shows.stream().mapToInt((s) -> s.count(color)).max().getAsInt())
            .reduce(Math::multiplyExact).getAsLong()
        ).sum();
  }

  /**
   * Description container of an input game.  Each game consists of an id followed
   * by a number of showings.  Each showing is how many of each color was shown.
   */
  private static class Game {
    private static final Pattern linePattern = Pattern.compile("^Game (\\d+): (.*)$");
    private static final Pattern colorPattern = Pattern.compile("^(\\d+) ([^ ]+)$");

    private final long id;
    private List<Multiset<String>> shows = new ArrayList<>();

    private Game(String input) {
      Matcher lineMatcher = matcher(linePattern, input);
      this.id = num(lineMatcher, 1);
      for (String showSpec : lineMatcher.group(2).split("; ")) {
        Multiset<String> show = HashMultiset.create();
        for (String colorSpec : showSpec.split(", ")) {
          Matcher colorMatcher = matcher(colorPattern, colorSpec);
          show.setCount(colorMatcher.group(2), num(colorMatcher, 1));
        }
        shows.add(show);
      }
    }
  }
}
