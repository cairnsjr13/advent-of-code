package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import java.util.List;

class Day25 extends Base2020 {
  private static final int SUBJECT = 7;

  @Override
  protected Object part1(Loader2 loader) {
    List<Long> input = loader.ml(Long::parseLong);
    long cardPublic = input.get(0);
    long doorPublic = input.get(1);

    long cardLoopSize = computeLoopSize(cardPublic);
    long encryption = 1;
    for (long i = 0; i < cardLoopSize; ++i) {
      encryption *= doorPublic;
      encryption %= 20201227;
    }
    return encryption;
  }

  private long computeLoopSize(long publicKey) {
    int loopSize = 0;
    for (long value = 1; value != publicKey; ++loopSize) {
      value *= SUBJECT;
      value %= 20201227;
    }
    return loopSize;
  }
}
