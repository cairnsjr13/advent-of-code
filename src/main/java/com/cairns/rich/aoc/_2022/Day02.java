package com.cairns.rich.aoc._2022;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day02 extends Base2022 {
  private static final Weapon[] weapons = Weapon.values();
  private static final int[] outcomeValues = { 3, 0, 6 };

  @Override
  protected void run() {
    List<Game> games = fullLoader.ml(Game::new);
    System.out.println(getTotalScore(games, (g) -> g.myselfAsWeapon));
    System.out.println(getTotalScore(games, (g) -> g.myselfAsOutcome));
  }

  private int getTotalScore(List<Game> games, Function<Game, Weapon> toMyself) {
    return games.stream().mapToInt((g) -> {
      Weapon myself = toMyself.apply(g);
      return myself.score + myself.outcome(g.opponent);
    }).sum();
  }

  private static class Game {
    private static final Pattern pattern = Pattern.compile("^([ABC]) ([XYZ])$");
    private static final Map<Character, Weapon> lookup = new HashMap<>();
    private static final Map<Character, Integer> myselfAsOutcomeShift = Map.of(
        'X', 2,
        'Y', 0,
        'Z', 1
    );
    private static final BiFunction<Weapon, Character, Weapon> myselfAsOutcomeLookup =
        (opponent, symbol) -> weapons[(opponent.ordinal() + myselfAsOutcomeShift.get(symbol)) % weapons.length];
    static {
      lookup.put('A', Weapon.Rock);
      lookup.put('B', Weapon.Paper);
      lookup.put('C', Weapon.Scissors);
      lookup.put('X', Weapon.Rock);
      lookup.put('Y', Weapon.Paper);
      lookup.put('Z', Weapon.Scissors);
    }

    private final Weapon opponent;
    private final Weapon myselfAsWeapon;
    private final Weapon myselfAsOutcome;

    private Game(String line) {
      Matcher matcher = matcher(pattern, line);
      this.opponent = lookup.get(matcher.group(1).charAt(0));
      Character myselfSymbol = matcher.group(2).charAt(0);
      this.myselfAsWeapon = lookup.get(myselfSymbol);
      this.myselfAsOutcome = myselfAsOutcomeLookup.apply(opponent, myselfSymbol);
    }
  }

  private enum Weapon {
    Rock(1),
    Paper(2),
    Scissors(3);

    private final int score;

    private Weapon(int score) {
      this.score = score;
    }

    private int outcome(Weapon against) {
      return safeGet(outcomeValues, (against.ordinal() - ordinal()) + weapons.length);
    }
  }
}
