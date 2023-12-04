package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.base.Function;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * We are playing scratch tickets and need to figure out how many we win based on different rules.
 */
class Day04 extends Base2023 {
  /**
   * We can compute the total score by finding all tickets that win (have at least 1 match) and then computing
   * each tickets' score by counting one point for the initial match and doubling for each additional match.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Card::new).stream().filter((c) -> c.numMatches > 0).mapToLong((c) -> 1 << (c.numMatches - 1)).sum();
  }

  /**
   * We simply need to count the total number of tickets with the modified rules, instead of scores.  Since matching logic is
   * static, we can process the list front to back and keep a running total of each tickets' copies.  Each time a ticket wins,
   * it adds a single copy to each of the following tickets based on the number of matches.  We need to add the number of
   * tickets equal to the current card's count for each following card (up to the number of matches).  This avoids recursion.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Card> cards = loader.ml(Card::new);
    for (int cardIndex = 0; cardIndex < cards.size(); ++cardIndex) {
      Card card = cards.get(cardIndex);
      long eachCardNumAdditionalCopies = card.numCards;
      for (int cardCopyOffset = 1; cardCopyOffset <= card.numMatches; ++cardCopyOffset) {
        cards.get(cardIndex + cardCopyOffset).numCards += eachCardNumAdditionalCopies;
      }
    }
    return cards.stream().mapToLong((c) -> c.numCards).sum();
  }

  /**
   * Descriptor class for a single scratch ticket.  The number of matches is static and can be computed at pars
   * time with set intersection logic. The number of total copies of this ticket won is also kept track here.
   */
  private static class Card {
    private static final Pattern pattern = Pattern.compile("^Card +\\d+: +([^|]+) \\| +([^|]+)$");
    private static final Function<String, Set<Integer>> toNums =
        (g) -> Arrays.stream(g.split(" +")).map(Integer::parseInt).collect(Collectors.toSet());

    private final long numMatches;
    private long numCards;

    private Card(String line) {
      Matcher matcher = matcher(pattern, line);
      Set<Integer> winners = toNums.apply(matcher.group(1));
      Set<Integer> numbers = toNums.apply(matcher.group(2));
      this.numMatches = numbers.stream().filter(winners::contains).count();
      this.numCards = 1;
    }
  }
}
