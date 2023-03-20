package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
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
  protected Object part1(Loader2 loader) {
    return loader.ml(this::getCompletionStack).stream()
        .filter((completionStack) -> completionStack.pop() == 'c')
        .mapToInt((completionStack) -> points.get(completionStack.pop()))
        .sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Long> scores = loader.ml(this::getCompletionStack).stream()
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
