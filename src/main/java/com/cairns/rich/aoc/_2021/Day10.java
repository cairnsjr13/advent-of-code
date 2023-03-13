package com.cairns.rich.aoc._2021;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

class Day10 extends Base2021 {
  private static final Map<Character, Character> openToClose = Map.of(
      '(', ')',
      '[', ']',
      '{', '}',
      '<', '>'
  );
  private static final Map<Character, Integer> points = Map.of(
      ')', 3,
      ']', 57,
      '}', 1197,
      '>', 25137,
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
    return lines.stream()
        .map(this::getCompletionStack)
        .filter((completionStack) -> completionStack.pop() == 'c')
        .mapToInt((completionStack) -> points.get(completionStack.pop()))
        .sum();
  }

  private long partTwo(List<String> lines) {
    List<Long> scores = lines.stream()
        .map(this::getCompletionStack)
        .filter((completionStack) -> completionStack.peek() != 'c')
        .mapToLong((completionStack) -> {
          long score = 0;
          while (!completionStack.isEmpty()) {
            score = (score * 5) + points.get(completionStack.pop());
          }
          return score;
        })
        .sorted().boxed()
        .collect(Collectors.toList());
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
