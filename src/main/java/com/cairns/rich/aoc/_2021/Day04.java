package com.cairns.rich.aoc._2021;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day04 extends Base2021 {
  @Override
  protected void run() throws Throwable {
    List<Integer> calledNumbers;
    Set<int[][]> boards = new HashSet<>();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fullLoader.file)))) {
      String firstLine = in.readLine();
      calledNumbers = Arrays.stream(firstLine.split(",")).map(Integer::parseInt).collect(Collectors.toList());
      
      while (true) {
        String line = in.readLine();
        if (line == null) {
          break;
        }
        int[][] board = new int[5][];
        for (int i = 0; i < 5; ++i) {
          board[i] = Arrays.stream(in.readLine().trim().split(" +")).mapToInt(Integer::parseInt).toArray();
        }
        boards.add(board);
      }
    }
    System.out.println(firstBoardToWinScore(calledNumbers, boards));
    System.out.println(lastBoardToWinScore(calledNumbers, boards));
  }
  
  private int firstBoardToWinScore(List<Integer> calledNumbers, Set<int[][]> boards) {
    for (int calledNumber : calledNumbers) {
      for (int[][] board : boards) {
        if (markBoard(board, calledNumber)) {
          if (doesBoardWin(board)) {
            return computeScore(board, calledNumber);
          }
        }
      }
    }
    throw fail();
  }
  
  private int lastBoardToWinScore(List<Integer> calledNumbers, Set<int[][]> boards) {
    for (int calledNumber : calledNumbers) {
      Iterator<int[][]> itr = boards.iterator();
      while (itr.hasNext()) {
        int[][] board = itr.next();
        if (markBoard(board, calledNumber)) {
          if (doesBoardWin(board)) {
            itr.remove();
            if (boards.isEmpty()) {
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
    for (int i = 0; i < 5; ++i) {
      int index = i;
      if (Arrays.stream(board[index]).allMatch((square) -> square == -1)) {
        return true;
      }
      if (Arrays.stream(board).mapToInt((row) -> row[index]).allMatch((square) -> square == -1)) {
        return true;
      }
    }
    return false;
  }
  
  private int computeScore(int[][] board, int lastCalledNumber) {
    int sumUnmarked = 0;
    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        if (board[i][j] != -1) {
          sumUnmarked += board[i][j];
        }
      }
    }
    return sumUnmarked * lastCalledNumber;
  }
}
