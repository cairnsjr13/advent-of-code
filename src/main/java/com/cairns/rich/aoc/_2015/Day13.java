package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * While waiting for Santa we need to eat.  We know how happy/sad everyone will be stting
 * next to everyone else.  Let's find the optimal seating arrangements to keep it smooth.
 */
class Day13 extends Base2015 {
  /**
   * Finds the total change in happiness of the optimal seating arrangement of the people in the input description.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    Table<String, String, Integer> impacts = HashBasedTable.create();
    loader.ml(Line::new).forEach((line) -> impacts.put(line.impacted, line.neighbor, line.value));
    List<String> people = new ArrayList<>(impacts.rowKeySet());
    return computeBest(impacts, people, new ArrayList<>());
  }

  /**
   * Finds the total change in happiness of the optimal seating arrangement of the
   * people in the input description, including a new, universally ambivalent person.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    Table<String, String, Integer> impacts = HashBasedTable.create();
    loader.ml(Line::new).forEach((line) -> impacts.put(line.impacted, line.neighbor, line.value));
    List<String> people = new ArrayList<>(impacts.rowKeySet());
    people.forEach((person) -> {
      impacts.put("", person, 0);
      impacts.put(person, "", 0);
    });
    people.add("");
    return computeBest(impacts, people, new ArrayList<>());
  }

  /**
   * Recursive ordering algorithm to compute the best possible placement given placements that have already happened.
   *
   * @implNote There is an inherant assumption that all people are mentioned in the input, even if they are ambivalent
   * @implNote There is a bit of inefficiency here given the table is a circle.  Some layouts are equivalent.
   */
  private int computeBest(Table<String, String, Integer> impacts, List<String> peopleLeft, List<String> order) {
    if (peopleLeft.isEmpty()) {
      return computeScore(impacts, order);
    }
    int best = Integer.MIN_VALUE;
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

  /**
   * The score of a seating arrangement is simply the sum of all peoples' change
   * in happiness where each person considers both of their neighbors independently.
   */
  private int computeScore(Table<String, String, Integer> impacts, List<String> order) {
    int score = 0;
    for (int i = 0; i < order.size(); ++i) {
      score += impacts.get(order.get(i), safeGet(order, i + 1));
      score += impacts.get(order.get(i), safeGet(order, i - 1));
    }
    return score;
  }

  /**
   * Input class that describes how a person will react to sitting next to another person.
   */
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
