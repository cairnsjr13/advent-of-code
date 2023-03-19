package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.Lists;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

// This shit was terrible: https://github.com/saidaspen/aoc2019/blob/master/java/src/main/java/se/saidaspen/aoc2019/day22/Day22.java
class Day22 extends Base2019 {
  private static UnaryOperator<BigInteger> cutMag = BigInteger::negate;
  private static UnaryOperator<BigInteger> inverseCutMag = UnaryOperator.identity();
  private static BinaryOperator<BigInteger> dealWithIncrementMag = (mag, numCards) -> mag;
  private static BinaryOperator<BigInteger> inverseDealWithIncrementMag = (mag, numCards) -> mag.modInverse(numCards);

  @Override
  protected Object part1(Loader2 loader) {
    BigInteger numCards = BigInteger.valueOf(10_007);
    long card = 2019;
    return reduce(
        loader.ml((spec) -> new Shuffle(spec, numCards, cutMag, dealWithIncrementMag)).stream(),
        numCards
    ).apply(numCards, card);
  }

  @Override
  protected Object part2(Loader2 loader) {
    BigInteger numCards = BigInteger.valueOf(119_315_717_514_047L);
    Long numFullShuffles = 101_741_582_076_661L;
    long position = 2020;
    return multiple(
        new HashMap<>(),
        reduce(
            Lists.reverse(
                loader.ml((spec) -> new Shuffle(spec, numCards, inverseCutMag, inverseDealWithIncrementMag))
            ).stream(),
            numCards
        ),
        numCards,
        numFullShuffles
    ).apply(numCards, position);
  }

  private Shuffle reduce(Stream<Shuffle> shuffles, BigInteger numCards) {
    return shuffles.sequential().reduce(
        Shuffle.identity,
        (first, second) -> combine(first, second, numCards)
    );
  }

  private Shuffle combine(Shuffle first, Shuffle second, BigInteger numCards) {
    return new Shuffle(
        first.m.multiply(second.m).mod(numCards),
        first.b.multiply(second.m).add(second.b).mod(numCards)
    );
  }

  private Shuffle multiple(Map<Long, Shuffle> cache, Shuffle shuffle, BigInteger numCards, Long numTimes) {
    if (!cache.containsKey(numTimes)) {
      if (numTimes == 0) {
        cache.put(numTimes, Shuffle.identity);
      }
      else if (numTimes % 2 == 0) {
        cache.put(numTimes, multiple(cache, combine(shuffle, shuffle, numCards), numCards, numTimes / 2));
      }
      else {
        cache.put(numTimes, combine(multiple(cache, shuffle, numCards, numTimes - 1), shuffle, numCards));
      }
    }
    return cache.get(numTimes);
  }

  // y = mx + b
  private static class Shuffle {
    private static final Shuffle identity = new Shuffle(BigInteger.ONE, BigInteger.ZERO);

    private final BigInteger m;
    private final BigInteger b;

    private Shuffle(BigInteger m, BigInteger b) {
      this.m = m;
      this.b = b;
    }

    private Shuffle(
        String spec,
        BigInteger numCards,
        UnaryOperator<BigInteger> cutMagFn,
        BinaryOperator<BigInteger> dealWithIncrementMagFn
    ) {
      if (spec.equals("deal into new stack")) {
        this.m = BigInteger.ONE.negate();
        this.b = BigInteger.ONE.negate();
      }
      else {
        BigInteger mag = BigInteger.valueOf(Long.parseLong(spec.substring(spec.lastIndexOf(' ') + 1)));
        if (spec.startsWith("cut")) {
          this.m = BigInteger.ONE;
          this.b = cutMagFn.apply(mag);
        }
        else if (spec.startsWith("deal with increment")) {
          this.m = dealWithIncrementMagFn.apply(mag, numCards);
          this.b = BigInteger.ZERO;
        }
        else {
          throw fail(spec);
        }
      }
    }

    private long apply(BigInteger numCards, long val) {
      return BigInteger.valueOf(val).multiply(m).add(b).mod(numCards).longValue();
    }
  }
}
