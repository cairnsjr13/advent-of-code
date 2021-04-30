package com.cairns.rich.aoc._2020;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

class Day22 extends Base2020 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    int blankIndex = lines.indexOf("");
    
    LinkedList<Long> player1 = parseCards(lines, 1, blankIndex);
    LinkedList<Long> player2 = parseCards(lines, blankIndex + 2, lines.size());
    
    System.out.println(getScoreOfWinner(player1, player2, this::getWinnerOfSimpleGame));
    System.out.println(getScoreOfWinner(player1, player2, this::getWinnerOfComplexGame));
  }
  
  private long getScoreOfWinner(LinkedList<Long> player1, LinkedList<Long> player2, Function<State, Integer> game) {
    State state = new State(new LinkedList<>(player1), new LinkedList<>(player2));
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
  
  private LinkedList<Long> parseCards(List<String> lines, int from, int to) {
    LinkedList<Long> cards = new LinkedList<>();
    for (int i = from; i < to; ++i) {
      cards.add(Long.parseLong(lines.get(i)));
    }
    return cards;
  }
  
  private static class State {
    private final LinkedList<Long> player1;
    private final LinkedList<Long> player2;
    
    private State(LinkedList<Long> player1, LinkedList<Long> player2) {
      this.player1 = player1;
      this.player2 = player2;
    }
    
    private State(State copy) {
      this.player1 = new LinkedList<>(copy.player1);
      this.player2 = new LinkedList<>(copy.player2);
    }
    
    private State subState(long player1Card, long player2Card) {
      LinkedList<Long> newPlayer1 = new LinkedList<>(player1.subList(0, (int) player1Card));
      LinkedList<Long> newPlayer2 = new LinkedList<>(player2.subList(0, (int) player2Card));
      return new State(newPlayer1, newPlayer2);
    }
    
    @Override
    public boolean equals(Object obj) {
      return player1.equals(((State) obj).player1)
          && player2.equals(((State) obj).player2);
    }
    
    @Override
    public int hashCode() {
      return player1.hashCode() + player2.hashCode();
    }
  }
}
