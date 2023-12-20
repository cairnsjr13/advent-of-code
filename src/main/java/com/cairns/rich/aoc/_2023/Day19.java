package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

/**
 * We need to sort machine parts according to a workflow made up of branching rules.  The
 * first applicable rule (according to a '<' or '>' test) will cause the current workflow to
 * jump.  All workflows eventually end in an 'A' (accepted) state or a 'R' (rejected) state.
 */
class Day19 extends Base2023 {
  private static final Pattern partPattern = Pattern.compile("^\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}$");

  /**
   * Calculates the sum of all attribute values of all parts that are accepted by the workflow pipeline and finish in 'A' state.
   * The algorithm for part 2 can be used by converting all attributes to {@link Range#singleton(Comparable)}s.
   */
  @Override
  protected Object part1(Loader loader) {
    List<String> lines = loader.ml();
    int blankI = lines.indexOf("");
    Map<String, Workflow> workflows =
        getLookup(lines.subList(0, blankI).stream().map(Workflow::new).collect(Collectors.toList()));
    return lines.stream().skip(blankI + 1).map((line) -> matcher(partPattern, line)).map((matcher) -> Map.of(
        'x', Range.singleton(num(matcher, 1)),
        'm', Range.singleton(num(matcher, 2)),
        'a', Range.singleton(num(matcher, 3)),
        's', Range.singleton(num(matcher, 4))
    )).mapToLong((part) -> 1 == (countAllAcceptedCombinations(workflows, part))
        ? part.values().stream().mapToLong((r) -> r.lowerEndpoint()).sum()
        : 0)
    .sum();
  }

  /**
   * Returns the total number of attribute combinations that will be accepted by the workflow pipeline and finish in 'A' state.
   * Note that will [1,4000] options for each attribute, there are 256_000_000_000_000 total, so we must use range splitting.
   */
  @Override
  protected Object part2(Loader loader) {
    List<String> lines = loader.ml();
    int blankI = lines.indexOf("");
    Map<String, Workflow> workflows =
        getLookup(lines.subList(0, blankI).stream().map(Workflow::new).collect(Collectors.toList()));
    Range<Integer> full = Range.closed(1, 4000);
    Map<Character, Range<Integer>> initialRange = Map.of('x', full, 'm', full, 'a', full, 's', full);
    return countAllAcceptedCombinations(workflows, initialRange);
  }

  /**
   * Uses a breadth first search to split range combinations into contiguous parts. Each part is guaranteed to either
   * end in 'A' (accepted) or 'R' (rejected).  Instead of short circuiting when we have an accepted rule, we also pass
   * the remainder of the range to the next rule until the left over range is empty.  This allows us to check all ranges.
   * The number of combinations for a given accepted range is the product of all sizes of its component ranges.
   */
  private long countAllAcceptedCombinations(Map<String, Workflow> workflows, Map<Character, Range<Integer>> initialRange) {
    AtomicLong totalAcceptedCombinations = new AtomicLong();
    bfs(
        Pair.of(initialRange, "in"),
        (ss) -> false,
        SearchState::getNumSteps,
        (cur, registrar) -> {
          Map<Character, Range<Integer>> fieldRanges = cur.getLeft();
          for (Rule rule : workflows.get(cur.getRight()).rules) {
            Map<Character, Range<Integer>> ifAccepted = rule.getResultRanges(fieldRanges, rule.acceptRange);
            if (ifAccepted.values().stream().noneMatch(Predicate.isEqual(Rule.empty))) {
              if ("A".equals(rule.next)) {
                totalAcceptedCombinations.addAndGet(
                    ifAccepted.values().stream().mapToLong(this::countDiscrete).reduce(Math::multiplyExact).getAsLong()
                );
              }
              else if (!"R".equals(rule.next)) {
                registrar.accept(Pair.of(ifAccepted, rule.next));
              }
            }
            fieldRanges = rule.getResultRanges(fieldRanges, rule.rejectRange);
            if (fieldRanges.values().stream().anyMatch(Predicate.isEqual(Rule.empty))) {
              break;
            }
          }
        }
    );
    return totalAcceptedCombinations.get();
  }

  /**
   * Returns the number of discrete elements in the given range.  We must be careful to
   * handle open endpoints.  We should never see an (x, x) range with our algorithm.
   */
  private long countDiscrete(Range<Integer> partRange) {
    if ((partRange.lowerEndpoint() == partRange.upperEndpoint()) &&
        (partRange.lowerBoundType() == BoundType.OPEN) &&
        (partRange.upperBoundType() == BoundType.OPEN)
    ) {
      throw fail(partRange);  // the formula below will be -1, which is incorrect
    }
    return (1 + partRange.upperEndpoint() - partRange.lowerEndpoint())
         - ((partRange.upperBoundType() == BoundType.OPEN) ? 1 : 0)
         - ((partRange.lowerBoundType() == BoundType.OPEN) ? 1 : 0);
  }

  /**
   * Container class to hold the ordered rules for a workflow.
   * All workflows's last rule is guaranteed to accept everything.
   */
  private static class Workflow implements HasId<String> {
    private static final Pattern pattern = Pattern.compile("^([^{]+)\\{(.+)\\}$");

    private final String name;
    private final List<Rule> rules = new ArrayList<>();

    private Workflow(String line) {
      Matcher matcher = matcher(pattern, line);
      this.name = matcher.group(1);
      String[] ruleSpecs = matcher.group(2).split(",");
      for (int i = 0; i < ruleSpecs.length - 1; ++i) {
        String[] ruleParts = ruleSpecs[i].split(":");
        rules.add(new Rule(ruleParts[1], ruleParts[0]));
      }
      rules.add(new Rule(ruleSpecs[ruleSpecs.length - 1]));
    }

    @Override
    public String getId() {
      return name;
    }
  }

  /**
   * Descriptor class for a workflow routing rule.  The internal {@link Rule#acceptRange} can
   * be used to check if a range pertaining to the {@link Rule#fieldToTest} matches this rule.
   */
  private static class Rule {
    private static final Range<Integer> empty = Range.singleton(0);

    private final String next;
    private final char fieldToTest;
    private final Range<Integer> acceptRange;
    private final Range<Integer> rejectRange;

    /**
     * Constructor used to create a rule that matches a '<' or '>' test.
     */
    private Rule(String next, String spec) {
      this.next = next;
      this.fieldToTest = spec.charAt(0);
      char cmp = spec.charAt(1);
      int boundary = Integer.parseInt(spec.substring(2));
      if (cmp == '<') {
        this.acceptRange = Range.lessThan(boundary);
        this.rejectRange = Range.atLeast(boundary);
      }
      else if (cmp == '>') {
        this.acceptRange = Range.greaterThan(boundary);
        this.rejectRange = Range.atMost(boundary);
      }
      else {
        throw fail(cmp);
      }
    }

    /**
     * Constructor used to create a rule that accepts all input ranges as is needed for the final rule in a workflow.
     */
    private Rule(String next) {
      this.next = next;
      this.fieldToTest = 'x';
      this.acceptRange = Range.all();
      this.rejectRange = empty;
    }

    /**
     * Computes a copy of the given input ranges with this {@link Rule#fieldToTest}'s range restricted to
     * the intersection with the given range.  By using {@link Rule#acceptRange} and {@link Rule#rejectRange}
     * we can split an input range into it's disjoint, composite parts that either pass or fail this rule.
     * Will replace the field's range explicitly with {@link Rule#empty} if there is no intersection.
     */
    private Map<Character, Range<Integer>> getResultRanges(Map<Character, Range<Integer>> input, Range<Integer> intersectRange)
    {
      Map<Character, Range<Integer>> resultRanges = new HashMap<>(input);
      Range<Integer> impactedRange = input.get(fieldToTest);
      resultRanges.put(
          fieldToTest,
          (impactedRange.isConnected(intersectRange)) ? impactedRange.intersection(intersectRange) : empty
      );
      return resultRanges;
    }
  }
}
