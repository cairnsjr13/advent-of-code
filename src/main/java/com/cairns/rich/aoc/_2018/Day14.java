package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Day14 extends Base2018 {
  @Override
  protected Object part1(Loader loader) {
    int recipeCount = Integer.parseInt(loader.sl());
    List<Integer> scores = runWhile((s) -> s.size() < recipeCount + 10);
    return scores.subList(scores.size() - 10, scores.size()).stream()
        .map(Object::toString).collect(Collectors.joining());
  }

  @Override
  protected Object part2(Loader loader) {
    String suffix = loader.sl();
    List<Integer> scores = runWhile((s) -> {
      if (s.size() < suffix.length()) {
        return true;
      }
      for (int i = 0; i < suffix.length(); ++i) {
        if ((suffix.charAt(i) - '0') != s.get(s.size() - (suffix.length() - i))) {
          return true;
        }
      }
      return false;
    });
    return scores.size() - suffix.length();
  }

  private List<Integer> runWhile(Predicate<List<Integer>> condition) {
    List<Integer> scores = new ArrayList<>();
    scores.add(3);
    scores.add(7);
    int[] elfPositions = { 0, 1 };
    while (condition.test(scores)) {
      int elf0 = scores.get(elfPositions[0]);
      int elf1 = scores.get(elfPositions[1]);
      int sum = elf0 + elf1;
      if (sum >= 10) {
        scores.add(1);
      }
      if (!condition.test(scores)) {
        break;
      }
      scores.add(sum % 10);
      elfPositions[0] = (elfPositions[0] + 1 + elf0) % scores.size();
      elfPositions[1] = (elfPositions[1] + 1 + elf1) % scores.size();
    }
    return scores;
  }
}
