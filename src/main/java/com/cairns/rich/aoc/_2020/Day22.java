package com.cairns.rich.aoc._2020;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class Day22 extends Base2020 {
  @Override
  protected void run() {
    List<List<Long>> players = fullLoader.gDelim("", this::parseCards);
    System.out.println(getScoreOfWinner(players, this::getWinnerOfSimpleGame));
    System.out.println(getScoreOfWinner(players, this::getWinnerOfComplexGame));
  }

  private long getScoreOfWinner(List<List<Long>> players, Function<State, Integer> game) {
    State state = new State(players);
    int winner = game.apply(state);
    return computeScore((winner == 1) ? state.player1 : state.player2);
  }

  private int getWinnerOfSimpleGame(State state) {
    while (!state.player1.isEmpty() && !state.player2.isEmpty()) {
      long player1Card = state.player1.pollFirst();
      long player2Card = state.player2.pollFirst();
      winRound(state, player1Card > player2Card, player1Card, player2Card);
    }
    return (state.player1.isEmpty()) ? 2 : 1;
  }

  private int getWinnerOfComplexGame(State state) {
    Set<State> seenStates = new HashSet<>();
    while (!state.player1.isEmpty() && !state.player2.isEmpty()) {
      if (seenStates.contains(state)) {
        return 1;
      }
      seenStates.add(new State(state));
      long player1Card = state.player1.pollFirst();
      long player2Card = state.player2.pollFirst();
      if ((player1Card > state.player1.size()) || (player2Card > state.player2.size())) {
        winRound(state, player1Card > player2Card, player1Card, player2Card);
      }
      else {
        winRound(state, getWinnerOfComplexGame(state.subState(player1Card, player2Card)) == 1, player1Card, player2Card);
      }
    }
    return (state.player1.isEmpty()) ? 2 : 1;
  }

  private void winRound(State state, boolean player1Wins, long player1Card, long player2Card) {
    if (player1Wins) {
      state.player1.add(player1Card);
      state.player1.add(player2Card);
    }
    else {
      state.player2.add(player2Card);
      state.player2.add(player1Card);
    }
  }

  private long computeScore(LinkedList<Long> winner) {
    long score = 0;
    Iterator<Long> itr = winner.descendingIterator();
    for (long multiplier = 1; itr.hasNext(); ++multiplier) {
      score += itr.next() * multiplier;
    }
    return score;
  }

  private List<Long> parseCards(List<String> lines) {
    return lines.subList(1, lines.size()).stream().map(Long::parseLong).collect(Collectors.toList());
  }

  private static class State {
    private final LinkedList<Long> player1;
    private final LinkedList<Long> player2;

    private State(List<List<Long>> players) {
      this.player1 = new LinkedList<>(players.get(0));
      this.player2 = new LinkedList<>(players.get(1));
    }

    private State(State copy) {
      this.player1 = new LinkedList<>(copy.player1);
      this.player2 = new LinkedList<>(copy.player2);
    }

    private State subState(long player1Card, long player2Card) {
      return new State(List.of(
          player1.subList(0, (int) player1Card),
          player2.subList(0, (int) player2Card)
      ));
    }

    @Override
    public boolean equals(Object obj) {
      return player1.equals(((State) obj).player1)
          && player2.equals(((State) obj).player2);
    }

    @Override
    public int hashCode() {
      return (player1.hashCode() * 31) + player2.hashCode();
    }
  }
}
