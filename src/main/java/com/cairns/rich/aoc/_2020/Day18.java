package com.cairns.rich.aoc._2020;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;

class Day18 extends Base2020 {
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    System.out.println(getEvalSum(lines, this::collapseWithoutPrecedence));
    System.out.println(getEvalSum(lines, this::collapseWithPrecedence));
  }

  private long getEvalSum(List<String> lines, ToLongFunction<List<Long>> collapse) {
    return lines.stream().mapToLong((line) -> eval(line, collapse)).sum();
  }

  private long eval(String line, ToLongFunction<List<Long>> collapse) {
    line = line.replaceAll(" +", "");
    List<Long> tokens = new ArrayList<>();
    for (int i = 0; i < line.length(); ++i) {
      char ch = line.charAt(i);
      if (Character.isDigit(ch)) {
        tokens.add((long) (ch - '0'));
      }
      else if ((ch == '+') || (ch == '*')) {
        tokens.add(-((long) ch));
      }
      else if (ch == '(') {
        int indexOfClosing = findClosing(line, i);
        tokens.add(eval(line.substring(i + 1, indexOfClosing), collapse));
        i = indexOfClosing;
      }
    }
    return collapse.applyAsLong(tokens);
  }

  private long collapseWithoutPrecedence(List<Long> tokens) {
    long total = tokens.get(0);
    for (int i = 1; i < tokens.size(); i += 2) {
      long op = -tokens.get(i);
      long operand = tokens.get(i + 1);
      total = (op == '+')
          ? total + operand
          : total * operand;

    }
    return total;
  }

  private long collapseWithPrecedence(List<Long> tokens) {
    while (tokens.size() > 1) {
      int indexOfPlus = tokens.indexOf(-((long) '+'));
      if (indexOfPlus == -1) {
        return collapseWithoutPrecedence(tokens);
      }
      List<Long> newTokens = new ArrayList<>();
      if (indexOfPlus != 1) {
        newTokens.add(collapseWithoutPrecedence(tokens.subList(0, indexOfPlus - 2)));
        newTokens.add(tokens.get(indexOfPlus - 2));
      }
      newTokens.add(tokens.get(indexOfPlus - 1) + tokens.get(indexOfPlus + 1));
      newTokens.addAll(tokens.subList(indexOfPlus + 2, tokens.size()));
      tokens = newTokens;
    }
    return tokens.get(0);
  }

  private int findClosing(String line, int openIndex) {
    int nest = 1;
    for (int i = openIndex + 1; true; ++i) {
      char ch = line.charAt(i);
      if (ch == '(') {
        ++nest;
      }
      else if (ch == ')') {
        --nest;
        if (nest == 0) {
          return i;
        }
      }
    }
  }
}
