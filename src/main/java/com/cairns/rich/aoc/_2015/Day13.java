package com.cairns.rich.aoc._2015;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day13 extends Base2015 {
  @Override
  protected void run() {
    Table<String, String, Integer> impacts = HashBasedTable.create();
    for (Line line : fullLoader.ml(Line::new)) {
      impacts.put(line.impacted, line.neighbor, line.value);
    }
    List<String> people = new ArrayList<>(impacts.rowKeySet());
    Candidate best = computeBest(impacts, people, new ArrayList<>());
    System.out.println(best.order);
    System.out.println(best.score);

    people.forEach((person) -> {
      impacts.put("", person, 0);
      impacts.put(person, "", 0);
    });
    people.add("");
    Candidate newBest = computeBest(impacts, people, new ArrayList<>());
    System.out.println(newBest.order);
    System.out.println(newBest.score);
  }

  private Candidate computeBest(Table<String, String, Integer> impacts, List<String> peopleLeft, List<String> order) {
    if (peopleLeft.isEmpty()) {
      return computeScore(impacts, order);
    }
    Candidate best = new Candidate(0, Arrays.asList());
    for (int i = 0; i < peopleLeft.size(); ++i) {
      order.add(peopleLeft.remove(i));
      Candidate option = computeBest(impacts, peopleLeft, order);
      if (best.score < option.score) {
        best = option;
      }
      peopleLeft.add(i, order.remove(order.size() - 1));
    }
    return best;
  }

  private Candidate computeScore(Table<String, String, Integer> impacts, List<String> order) {
    int score = 0;
    for (int i = 0; i < order.size(); ++i) {
      score += impacts.get(order.get(i), order.get((i + 1) % order.size()));
      score += impacts.get(order.get(i), order.get((i - 1 + order.size()) % order.size()));
    }
    return new Candidate(score, order);
  }

  private static class Candidate {
    private final int score;
    private final List<String> order;

    private Candidate(int score, List<String> order) {
      this.score = score;
      this.order = new ArrayList<>(order);
    }
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
      this.value = (("gain".equals(matcher.group(2))) ? 1 : -1) * Integer.parseInt(matcher.group(3));
    }
  }
}
