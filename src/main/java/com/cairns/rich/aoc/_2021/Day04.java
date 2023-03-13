package com.cairns.rich.aoc._2021;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day04 extends Base2021 {
  @Override
  protected void run() throws Throwable { // TODO: multi group candidate
    State state = new State(fullLoader.ml()); // TODO: kinda weird to have mutated the state for part 2
    System.out.println(firstBoardToWinScore(state));
    System.out.println(lastBoardToWinScore(state));
  }

  private int firstBoardToWinScore(State state) {
    for (int calledNumber : state.calledNumbers) {
      for (int[][] board : state.boards) {
        if (markBoard(board, calledNumber)) {
          if (doesBoardWin(board)) {
            state.boards.remove(board);
            return computeScore(board, calledNumber);
          }
        }
      }
    }
    throw fail();
  }

  private int lastBoardToWinScore(State state) {
    for (int calledNumber : state.calledNumbers) {
      Iterator<int[][]> itr = state.boards.iterator();
      while (itr.hasNext()) {
        int[][] board = itr.next();
        if (markBoard(board, calledNumber)) {
          if (doesBoardWin(board)) {
            itr.remove();
            if (state.boards.isEmpty()) {
              return computeScore(board, calledNumber);
            }
          }
        }
      }
    }
    throw fail();
  }

  private boolean markBoard(int[][] board, int calledNumber) {
    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        if (board[i][j] == calledNumber) {
          board[i][j] = -1;
          return true;
        }
      }
    }
    return false;
  }

  private boolean doesBoardWin(int[][] board) {
    return IntStream.range(0, 5).anyMatch((i) -> {
      return Arrays.stream(board[i]).allMatch((square) -> square == -1)
          || Arrays.stream(board).mapToInt((row) -> row[i]).allMatch((square) -> square == -1);
    });
  }

  private int computeScore(int[][] board, int lastCalledNumber) {
    return lastCalledNumber * Arrays.stream(board).flatMapToInt(Arrays::stream).filter((s) -> s != -1).sum();
  }

  private static class State {
    private final List<Integer> calledNumbers;
    private final Set<int[][]> boards = new HashSet<>();

    private State(List<String> lines) {
      this.calledNumbers = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());
      for (int i = 2; i < lines.size(); i += 6) {
        int[][] board = new int[5][];
        for (int r = 0; r < 5; ++r) {
          board[r] = Arrays.stream(lines.get(i + r).trim().split(" +")).mapToInt(Integer::parseInt).toArray();
        }
        boards.add(board);
      }
    }
  }
}
