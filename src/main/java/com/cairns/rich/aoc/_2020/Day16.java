package com.cairns.rich.aoc._2020;

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
  protected void run() {
    List<String> lines = fullLoader.ml(); // TODO: candidate for multiple group loader
    int indexOfFirstBlank = lines.indexOf("");
    List<Rule> rules = lines.subList(0, indexOfFirstBlank).stream().map(Rule::new).collect(Collectors.toList());

    int indexOfLastBlank = lines.lastIndexOf("");
    int[] myTicket = parseTicketSpec(lines.get(indexOfLastBlank - 1));
    List<int[]> nearbyTickets = lines
        .subList(indexOfLastBlank + 2, lines.size())
        .stream()
        .map(this::parseTicketSpec)
        .collect(Collectors.toList());

    System.out.println(computeTotalErrorRate(rules, nearbyTickets));
    System.out.println(computeDepartureFieldsProduct(rules, myTicket, computeRuleAvailableByIndex(rules, nearbyTickets)));
  }

  private int computeTotalErrorRate(List<Rule> rules, List<int[]> nearbyTickets) {
    int totalErrorRate = 0;
    for (int[] nearbyTicket : nearbyTickets) {
      Integer errorRate = getErrorRate(rules, nearbyTicket);
      if (errorRate != null) {
        totalErrorRate += errorRate;
      }
    }
    return totalErrorRate;
  }

  private long computeDepartureFieldsProduct(List<Rule> rules, int[] myTicket, List<Set<Integer>> rulesAvailableByIndex) {
    Map<Rule, Integer> ruleIndexes = new HashMap<>();
    while (ruleIndexes.size() < rules.size()) {
      for (int i = 0; i < rulesAvailableByIndex.size(); ++i) {
        Set<Integer> rulesAvailable = rulesAvailableByIndex.get(i);
        if (rulesAvailable.size() == 1) {
          int ruleIndex = rulesAvailable.iterator().next();
          ruleIndexes.put(rules.get(ruleIndex), i);
          rulesAvailableByIndex.forEach((rs) -> rs.remove(ruleIndex));
        }
      }
    }
    return IntStream.range(0, 6)
        .mapToLong((i) -> myTicket[ruleIndexes.get(rules.get(i))])
        .reduce(1, Math::multiplyExact);
  }

  private List<Set<Integer>> computeRuleAvailableByIndex(List<Rule> rules, List<int[]> nearbyTickets) {
    List<int[]> validTickets =
        nearbyTickets.stream().filter((fields) -> null == getErrorRate(rules, fields)).collect(Collectors.toList());
    List<Set<Integer>> rulesAvailableByIndex = new ArrayList<>();
    int numFields = validTickets.get(0).length;
    for (int i = 0; i < numFields; ++i) {
      Set<Integer> rulesAvailable = IntStream.range(0, numFields).boxed().collect(Collectors.toSet());
      for (int[] validTicket : validTickets) {
        Iterator<Integer> rulesAvailableItr = rulesAvailable.iterator();
        while (rulesAvailableItr.hasNext()) {
          int ruleAvailable = rulesAvailableItr.next();
          if (!rules.get(ruleAvailable).matches(validTicket[i])) {
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

  private int[] parseTicketSpec(String nearbyTicket) {
    return Arrays.stream(nearbyTicket.split(",")).mapToInt(Integer::parseInt).toArray();
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

    @Override
    public String toString() {
      return name;
    }

    private boolean matches(int value) {
      return ranges.stream().anyMatch((range) -> range.contains(value));
    }
  }
}
