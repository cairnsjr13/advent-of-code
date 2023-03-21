package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class Day21 extends Base2021 {
  private static final Map<Integer, BigInteger> diceTotalToUniverseMultiplier = new HashMap<>();

  static {
    diceTotalToUniverseMultiplier.put(3, BigInteger.ONE);         // (111)
    diceTotalToUniverseMultiplier.put(4, BigInteger.valueOf(3));  // (211), (121), (112)
    diceTotalToUniverseMultiplier.put(5, BigInteger.valueOf(6));  // (311), (131), (113), (122), (212), (221)
    diceTotalToUniverseMultiplier.put(6, BigInteger.valueOf(7));  // (222), (123), (132), (213), (231), (312), (321)
    diceTotalToUniverseMultiplier.put(7, BigInteger.valueOf(6));  // (311), (131), (113), (122), (212), (221)
    diceTotalToUniverseMultiplier.put(8, BigInteger.valueOf(3));  // (211), (121), (112)
    diceTotalToUniverseMultiplier.put(9, BigInteger.ONE);         // (111)
  }

  @Override
  protected Object part1(Loader loader) {
    List<Player> players = loader.ml(Player::new);
    DeterministicDice dice = new DeterministicDice();
    for (int playerIndex = 0; true; playerIndex = 1 - playerIndex) {
      Player player = players.get(playerIndex);
      int spaces = dice.roll() + dice.roll() + dice.roll();
      player.move(spaces);
      player.score += player.position0Based + 1;
      if (player.score >= 1000) {
        return players.get(1 - playerIndex).score * dice.numRolls;
      }
    }
  }

  @Override
  protected Object part2(Loader loader) {
    List<Player> players = loader.ml(Player::new);
    UniverseSummary[/* turn */][/* player1Position */][/* player2Position */][/* player1Score */][/* player2Score */] cache =
        new UniverseSummary[2][10][10][21][21];
    UniverseSummary fullGameSummary =
        getUniverseSummaryFrom(cache, 0, players.get(0).position0Based, players.get(1).position0Based, 0, 0);
    return fullGameSummary.player1Wins.max(fullGameSummary.player2Wins);
  }

  private UniverseSummary getUniverseSummaryFrom(
      UniverseSummary[][][][][] cache,
      int playerTurn,
      int player1Position,
      int player2Position,
      int player1Score,
      int player2Score
  ) {
    if (player1Score >= 21) {
      return UniverseSummary.player1Win;
    }
    if (player2Score >= 21) {
      return UniverseSummary.player2Win;
    }
    if (cache[playerTurn][player1Position][player2Position][player1Score][player2Score] == null) {
      BigInteger player1Wins = BigInteger.ZERO;
      BigInteger player2Wins = BigInteger.ZERO;
      for (int diceTotal : diceTotalToUniverseMultiplier.keySet()) {
        BigInteger universeMultiplier = diceTotalToUniverseMultiplier.get(diceTotal);
        int[] playerPositions = { player1Position, player2Position };
        int[] playerScores = { player1Score, player2Score };
        playerPositions[playerTurn] = (playerPositions[playerTurn] + diceTotal) % 10;
        playerScores[playerTurn] += playerPositions[playerTurn] + 1;
        UniverseSummary futureSummary = getUniverseSummaryFrom(
            cache,
            1 - playerTurn,
            playerPositions[0],
            playerPositions[1],
            playerScores[0],
            playerScores[1]
        );
        player1Wins = player1Wins.add(universeMultiplier.multiply(futureSummary.player1Wins));
        player2Wins = player2Wins.add(universeMultiplier.multiply(futureSummary.player2Wins));
      }
      cache[playerTurn][player1Position][player2Position][player1Score][player2Score] =
          new UniverseSummary(player1Wins, player2Wins);
    }
    return cache[playerTurn][player1Position][player2Position][player1Score][player2Score];
  }

  private static class DeterministicDice {
    private long numRolls = 0;
    private int nextValue0Based = 0;

    private int roll() {
      ++numRolls;
      int value = nextValue0Based + 1;
      nextValue0Based = value % 100;
      return value;
    }
  }

  private static class UniverseSummary {
    private static final UniverseSummary player1Win = new UniverseSummary(BigInteger.ONE, BigInteger.ZERO);
    private static final UniverseSummary player2Win = new UniverseSummary(BigInteger.ZERO, BigInteger.ONE);

    private final BigInteger player1Wins;
    private final BigInteger player2Wins;

    private UniverseSummary(BigInteger player1Wins, BigInteger player2Wins) {
      this.player1Wins = player1Wins;
      this.player2Wins = player2Wins;
    }

    @Override
    public String toString() {
      return "(" + player1Wins + ", " + player2Wins + ")";
    }
  }

  private static class Player {
    private static final Pattern pattern = Pattern.compile("^Player \\d starting position: (\\d)$");

    private int position0Based;
    private long score = 0;

    private Player(String line) {
      this.position0Based = num(matcher(pattern, line), 1) - 1;
    }

    private void move(int spaces) {
      position0Based = (position0Based + spaces) % 10;
    }
  }
}
