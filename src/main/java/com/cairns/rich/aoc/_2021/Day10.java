package com.cairns.rich.aoc._2021;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Day10 extends Base2021 {
  private static final Map<Character, Character> openToClose = Map.of(
      '(', ')',
      '[', ']',
      '{', '}',
      '<', '>'
  );
  private static final Map<Character, Integer> part1Points = Map.of(
      ')', 3,
      ']', 57,
      '}', 1197,
      '>', 25137
  );
  private static final Map<Character, Integer> part2Points = Map.of(
      '(', 1,
      '[', 2,
      '{', 3,
      '<', 4
  );
  
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    System.out.println(partOne(lines));
    System.out.println(partTwo(lines));
  }
  
  private int partOne(List<String> lines) {
    int totalPoints = 0;
    for (String line : lines) {
      Stack<Character> completionStack = getCompletionStack(line);
      if (completionStack.pop() == 'c') {
        totalPoints += part1Points.get(completionStack.pop());
      }
    }
    return totalPoints;
  }
  
  private long partTwo(List<String> lines) {
    List<Long> scores = new ArrayList<>();
    for (String line : lines) {
      Stack<Character> completionStack = getCompletionStack(line);
      if (completionStack.peek() != 'c') {
        long score = 0;
        while (!completionStack.isEmpty()) {
          score = (score * 5) + part2Points.get(completionStack.pop());
        }
        scores.add(score);
      }
    }
    Collections.sort(scores);
    return scores.get(scores.size() / 2);
  }
  
  private Stack<Character> getCompletionStack(String line) {
    Stack<Character> opens = new Stack<>();
    for (char ch : line.toCharArray()) {
      if (openToClose.containsKey(ch)) {
        opens.push(ch);
      }
      else if (ch != openToClose.get(opens.pop())) {
        opens.push(ch);
        opens.push('c');    // mark as corrupted
        break;
      }
    }
    return opens;
  }
}
