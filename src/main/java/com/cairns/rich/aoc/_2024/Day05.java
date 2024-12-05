package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Update pages must be in an order that follows a list of rules specifying relative ordering between pages.
 */
class Day05 extends Base2024 {
  /**
   * For part 1 we need to return the checksum of the already ordered updates.
   */
  @Override
  protected Object part1(Loader loader) {
    return sumMiddleElementsAfterCorrectlyOrdered(loader, true, (pageToNotBefores, update) -> update);
  }

  /**
   * For part 2 we need to return the checksum of the unordered updates after ordering them.
   */
  @Override
  protected Object part2(Loader loader) {
    return sumMiddleElementsAfterCorrectlyOrdered(loader, false, this::correctOrder);
  }

  /**
   * Performs these actions to compute the checksum answer:
   *   1) parses loader input
   *   2) finds updates that have the given followRules value
   *   3) orders the updates with the given orderProperly function
   *   4) computes the checksum by returning the sum of the middle elements
   */
  private int sumMiddleElementsAfterCorrectlyOrdered(
      Loader loader,
      boolean expectedFollowRulesValue,
      BiFunction<HashMultimap<Integer, Integer>, List<Integer>, List<Integer>> orderProperly
  ) {
    HashMultimap<Integer, Integer> pageToNotBefores = HashMultimap.create();
    List<List<String>> rulesAndUpdates = loader.gDelim("");
    for (String rule : rulesAndUpdates.get(0)) {
      String[] pieces = rule.split("\\|");
      int before = Integer.parseInt(pieces[0]);
      int after = Integer.parseInt(pieces[1]);
      pageToNotBefores.put(before, after);
    }

    return rulesAndUpdates.get(1).stream()
        .map((line) -> Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList()))
        .filter((update) -> expectedFollowRulesValue == followsRules(pageToNotBefores, update))
        .map((update) -> orderProperly.apply(pageToNotBefores, update))
        .mapToInt((update) -> update.get(update.size() / 2))
        .sum();
  }

  /**
   * Returns true if the order of pages in the given update follow the given rules.
   * As we see a page, we can check its list of pages that are not allowed before it for pages that we have
   * already seen.  If we find no conflict, we add the page to the seen list and continue checking the rest.
   */
  private boolean followsRules(HashMultimap<Integer, Integer> pageToNotBefores, List<Integer> update) {
    Set<Integer> seen = new HashSet<>();
    for (int pageNum : update) {
      if (!Sets.intersection(pageToNotBefores.get(pageNum), seen).isEmpty()) {
        return false;
      }
      seen.add(pageNum);
    }
    return true;
  }

  /**
   * Returns a new list for the given update that has its ordering following the given rules.
   * By using recursion we can add a page only after all of its required predecessors are added.
   */
  private List<Integer> correctOrder(HashMultimap<Integer, Integer> pageToNotBefores, List<Integer> update) {
    Set<Integer> includedPages = new HashSet<>(update);
    Set<Integer> visited = new HashSet<>();
    List<Integer> fixed = new ArrayList<>();
    update.forEach((page) -> handlePage(pageToNotBefores, includedPages, visited, fixed, page));
    return fixed;
  }

  /**
   * Recursive method to handle adding all of a page's required predecessors to the given fixed list and then itself.
   */
  private void handlePage(
      HashMultimap<Integer, Integer> pageToNotBefores,
      Set<Integer> includedPages,
      Set<Integer> visited,
      List<Integer> fixed,
      int at
  ) {
    if (!visited.contains(at) && includedPages.contains(at)) {
      visited.add(at);
      pageToNotBefores.get(at).forEach((before) -> handlePage(pageToNotBefores, includedPages, visited, fixed, before));
      fixed.add(at);
    }
  }
}
