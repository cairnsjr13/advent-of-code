package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day13 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    Table<String, String, Integer> impacts = HashBasedTable.create();
    loader.ml(Line::new).forEach((line) -> impacts.put(line.impacted, line.neighbor, line.value));
    List<String> people = new ArrayList<>(impacts.rowKeySet());

    result.part1(computeBest(impacts, people, new ArrayList<>()));
    people.forEach((person) -> {
      impacts.put("", person, 0);
      impacts.put(person, "", 0);
    });
    people.add("");
    result.part2(computeBest(impacts, people, new ArrayList<>()));
  }

  private int computeBest(Table<String, String, Integer> impacts, List<String> peopleLeft, List<String> order) {
    if (peopleLeft.isEmpty()) {
      return computeScore(impacts, order);
    }
    int best = 0;
    for (int i = 0; i < peopleLeft.size(); ++i) {
      order.add(peopleLeft.remove(i));
      int option = computeBest(impacts, peopleLeft, order);
      if (best < option) {
        best = option;
      }
      peopleLeft.add(i, order.remove(order.size() - 1));
    }
    return best;
  }

  private int computeScore(Table<String, String, Integer> impacts, List<String> order) {
    int score = 0;
    for (int i = 0; i < order.size(); ++i) {
      score += impacts.get(order.get(i), safeGet(order, i + 1));
      score += impacts.get(order.get(i), safeGet(order, i - 1));
    }
    return score;
  }

  private static class Line {
    private static final Pattern pattern =
        Pattern.compile("^(.+) would (gain|lose) (\\d+) happiness units by sitting next to (.+)\\.$");

    private final String impacted;
    private final String neighbor;
    private final int value;

    private Line(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.impacted = matcher.group(1);
      this.neighbor = matcher.group(4);
      this.value = (("gain".equals(matcher.group(2))) ? 1 : -1) * num(matcher, 3);
    }
  }
}
