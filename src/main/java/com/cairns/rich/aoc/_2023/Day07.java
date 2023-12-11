package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * We need to simulate some CamelCards hands.  CamelCards is a simplified version of 5 card poker
 * where each hand type has a major rank, and the ordering of card values provides a minor rank.
 * Sorting the hands will allow us to compute the total winnings for the input with certain rules.
 */
class Day07 extends Base2023 {
  private static final Multiset<Character> standardStrengths = strengths("23456789TJQKA");
  private static final Multiset<Character> jokerStrengths = strengths("J23456789TQKA");
  private static final Map<Integer, Map<HandType, HandType>> jokerTypeLookup = Map.of(
      1, Map.of(
          HandType.HighCard, HandType.OnePair,
          HandType.OnePair, HandType.ThreeOfAKind,
          HandType.TwoPair, HandType.FullHouse,
          HandType.ThreeOfAKind, HandType.FourOfAKind,
          HandType.FourOfAKind, HandType.FiveOfAKind
      ),
      2, Map.of(
          HandType.OnePair, HandType.ThreeOfAKind,
          HandType.TwoPair, HandType.FourOfAKind,
          HandType.FullHouse, HandType.FiveOfAKind
      ),
      3, Map.of(
          HandType.ThreeOfAKind, HandType.FourOfAKind,
          HandType.FullHouse, HandType.FiveOfAKind
      ),
      4, Map.of(HandType.FourOfAKind, HandType.FiveOfAKind)
  );

  /**
   * Computes the winnings of the input hands based on standard hand strength.
   */
  @Override
  protected Object part1(Loader loader) {
    return calculateWinnings(loader, standardStrengths, Hand::getType);
  }

  /**
   * Computes the winnings of the input hands treating 'J' as joker.
   * The joker can be any card when computing major rank, but is the weakest when computing minor rank.
   *
   * Impl: The {@link #jokerTypeLookup} contains all of the mappings between standard type and joker type.
   *       These can be deduced logically based on the distinct card counts present and are deterministic.
   *       If the mapping does not exist in the map, it either is impossible, or results in no change.
   */
  @Override
  protected Object part2(Loader loader) {
    return calculateWinnings(loader, jokerStrengths, (hand) -> {
      int numJokers = hand.cards.count('J');
      HandType standardType = hand.getType();
      return (jokerTypeLookup.containsKey(numJokers))
           ? jokerTypeLookup.get(numJokers).getOrDefault(standardType, standardType)
           : standardType;
    });
  }

  /**
   * Computes the sum total of all hands' winnings.  A hand's winning is computed by multiplying
   * its bid by its final rank.  The hand's rank is computed by sorting all the hand's by their
   * strengths.  Ranks should be 1 based to prevent the weakest hand from zero'ing out.
   */
  private long calculateWinnings(Loader loader, Multiset<Character> strengths, Function<Hand, HandType> toType) {
    List<Hand> hands = loader.ml(Hand::new);
    hands.sort(compareWithStrengths(toType, strengths));
    long winnings = 0;
    for (int rank = 1; rank <= hands.size(); ++rank) {
      winnings += rank * hands.get(rank - 1).bid;
    }
    return winnings;
  }

  /**
   * Returns a comparator that first compares the hand type followed by
   * the in order comparison of each card, based on the given strengths.
   */
  private Comparator<Hand> compareWithStrengths(Function<Hand, HandType> toType, Multiset<Character> strengths) {
    Comparator<Hand> cmp = Comparator.comparing(toType);
    for (int i = 0; i < 5; ++i) {
      final int cardIndex = i;
      cmp = cmp.thenComparingInt((h) -> strengths.count(h.rawCards.charAt(cardIndex)));
    }
    return cmp;
  }

  /**
   * Helper method to compute a {@link Multiset} of strengths where the key is
   * the card and the count is the strength indicated by index in given string.
   */
  private static Multiset<Character> strengths(String weakestToAStrongest) {
    Multiset<Character> strengths = HashMultiset.create();
    for (int strength = 0; strength < weakestToAStrongest.length(); ++strength) {
      strengths.setCount(weakestToAStrongest.charAt(strength), strength);
    }
    return strengths;
  }

  /**
   * Descriptor class for holding information about a hand.
   */
  private static class Hand {
    private final String rawCards;
    private final long bid;
    private final Multiset<Character> cards = HashMultiset.create();
    private final Set<Integer> counts;
    private final int numUniqueCards;

    private Hand(String line) {
      String[] parts = line.split(" ");
      this.rawCards = parts[0];
      this.bid = Long.parseLong(parts[1]);
      parts[0].chars().forEach((ch) -> cards.add((char) ch));
      this.counts = cards.entrySet().stream().map(Entry::getCount).collect(Collectors.toSet());
      this.numUniqueCards = cards.elementSet().size();
    }

    /**
     * Computes the standard {@link HandType} of this hand using the card counts and number of unique cards.
     */
    private HandType getType() {
      if (counts.contains(5)) {
        return HandType.FiveOfAKind;
      }
      if (counts.contains(4)) {
        return HandType.FourOfAKind;
      }
      if (counts.contains(3)) {
        return (numUniqueCards == 2) ? HandType.FullHouse : HandType.ThreeOfAKind;
      }
      if (counts.contains(2)) {
        return (numUniqueCards == 3) ? HandType.TwoPair : HandType.OnePair;
      }
      return HandType.HighCard;
    }
  }

  /**
   * Naming enum for the different major rank hand types.  This is mainly to make the code easier to read.
   * Note that the order of the elements matters, as it represents the relative strengths of the types.
   */
  private enum HandType {
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfAKind,
    FullHouse,
    FourOfAKind,
    FiveOfAKind
  }
}
