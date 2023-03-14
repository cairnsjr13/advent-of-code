package com.cairns.rich.aoc._2022;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TODO: multi part group?
class Day05 extends Base2022 {
  private static final Pattern movePattern = Pattern.compile("^move (\\d+) from (\\d+) to (\\d+)$");

  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    int emptyLine = lines.indexOf("");
    List<String> init = lines.subList(0, emptyLine);
    List<String> moves = lines.subList(emptyLine + 1, lines.size());
    System.out.println(calculateHeadOfStacksAfterMoves(ArrayDeque::pollFirst, init, moves));
    System.out.println(calculateHeadOfStacksAfterMoves(ArrayDeque::pollLast, init, moves));
  }

  private String calculateHeadOfStacksAfterMoves(
      Function<ArrayDeque<Character>, Character> fromTemp,
      List<String> init,
      List<String> moves
  ) {
    List<Stack<Character>> stacks = initStacks(init);
    ArrayDeque<Character> temp = new ArrayDeque<>();
    for (String move : moves) {
      Matcher matcher = matcher(movePattern, move);
      int count = num(matcher, 1);
      Stack<Character> from = stacks.get(num(matcher, 2) - 1);
      Stack<Character> to = stacks.get(num(matcher, 3) - 1);

      while (temp.size() < count) {
        temp.offerLast(from.pop());
      }
      while (!temp.isEmpty()) {
        to.push(fromTemp.apply(temp));
      }
    }
    return stacks.stream().map(Stack::peek).map(Object::toString).collect(Collectors.joining());
  }

  private List<Stack<Character>> initStacks(List<String> init) {
    int numStacks = (init.get(0).length() + 1) / 4;
    List<Stack<Character>> stacks = new ArrayList<>();
    while (stacks.size() < numStacks) {
      Stack<Character> stack = new Stack<>();
      for (int i = init.size() - 2; i >= 0; --i) {
        char item = init.get(i).charAt(1 + stacks.size() * 4);
        if (item != ' ') {
          stack.push(item);
        }
      }
      stacks.add(stack);
    }
    return stacks;
  }
}
