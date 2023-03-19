package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day16 extends Base2020 {
  @Override
  protected Object part1(Loader2 loader) {
    State state = new State(loader);
    return state.nearbyTickets.stream()
        .map((nearbyTicket) -> getErrorRate(state.rules, nearbyTicket))
        .filter((er) -> er != null)
        .mapToInt(Integer::intValue)
        .sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    State state = new State(loader);
    List<Set<Integer>> rulesAvailableByIndex = computeRuleAvailableByIndex(state);
    Map<Rule, Integer> ruleIndexes = new HashMap<>();
    while (ruleIndexes.size() < state.rules.size()) {
      for (int i = 0; i < rulesAvailableByIndex.size(); ++i) {
        Set<Integer> rulesAvailable = rulesAvailableByIndex.get(i);
        if (rulesAvailable.size() == 1) {
          int ruleIndex = rulesAvailable.iterator().next();
          ruleIndexes.put(state.rules.get(ruleIndex), i);
          rulesAvailableByIndex.forEach((rs) -> rs.remove(ruleIndex));
        }
      }
    }
    return state.rules.stream()
        .filter((r) -> r.name.startsWith("departure"))
        .mapToLong((r) -> state.myTicket[ruleIndexes.get(r)])
        .reduce(1L, Math::multiplyExact);
  }

  private List<Set<Integer>> computeRuleAvailableByIndex(State state) {
    List<int[]> validTickets = state.nearbyTickets.stream()
        .filter((fields) -> null == getErrorRate(state.rules, fields))
        .collect(Collectors.toList());
    List<Set<Integer>> rulesAvailableByIndex = new ArrayList<>();
    int numFields = validTickets.get(0).length;
    for (int i = 0; i < numFields; ++i) {
      Set<Integer> rulesAvailable = IntStream.range(0, numFields).boxed().collect(Collectors.toSet());
      for (int[] validTicket : validTickets) {
        Iterator<Integer> rulesAvailableItr = rulesAvailable.iterator();
        while (rulesAvailableItr.hasNext()) {
          int ruleAvailable = rulesAvailableItr.next();
          if (!state.rules.get(ruleAvailable).matches(validTicket[i])) {
            rulesAvailableItr.remove();
          }
        }
      }
      rulesAvailableByIndex.add(rulesAvailable);
    }
    return rulesAvailableByIndex;
  }

  private Integer getErrorRate(List<Rule> rules, int[] fields) {
    boolean hasError = false;
    int errorSum = 0;
    for (int field : fields) {
      if (rules.stream().noneMatch((rule) -> rule.matches(field))) {
        hasError = true;
        errorSum += field;
      }
    }
    return (hasError) ? errorSum : null;
  }

  private static class Rule {
    private static final Pattern pattern = Pattern.compile("^([^:]+): (.+)$");

    private final String name;
    private final List<Range<Integer>> ranges = new ArrayList<>();

    private Rule(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.name = matcher.group(1);
      for (String rangeSpec : matcher.group(2).split(" or ")) {
        int dashIndex = rangeSpec.indexOf('-');
        this.ranges.add(Range.closed(
            Integer.parseInt(rangeSpec.substring(0, dashIndex)),
            Integer.parseInt(rangeSpec.substring(dashIndex + 1))
        ));
      }
    }

    private boolean matches(int value) {
      return ranges.stream().anyMatch((range) -> range.contains(value));
    }
  }

  private static class State {
    private final List<Rule> rules;
    private final int[] myTicket;
    private final List<int[]> nearbyTickets;

    private State(Loader2 loader) {
      List<String> lines = loader.ml(); // TODO: candidate for multiple group loader
      int indexOfFirstBlank = lines.indexOf("");
      int indexOfLastBlank = lines.lastIndexOf("");

      this.rules = lines.subList(0, indexOfFirstBlank).stream().map(Rule::new).collect(Collectors.toList());
      this.myTicket = parseTicketSpec(lines.get(indexOfLastBlank - 1));
      this.nearbyTickets = lines
          .subList(indexOfLastBlank + 2, lines.size())
          .stream()
          .map(this::parseTicketSpec)
          .collect(Collectors.toList());
    }

    private int[] parseTicketSpec(String nearbyTicket) {
      return Arrays.stream(nearbyTicket.split(",")).mapToInt(Integer::parseInt).toArray();
    }
  }
}
