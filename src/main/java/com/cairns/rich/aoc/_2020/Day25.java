package com.cairns.rich.aoc._2020;

class Day25 extends Base2020 {
  private static final int SUBJECT = 7;

  @Override
  protected void run() {
    print(5764801, 17807724);
    print(1965712, 19072108);
  }

  private void print(long cardPublic, long doorPublic) {
    long cardLoopSize = computeLoopSize(cardPublic);
    long encryption = 1;
    for (long i = 0; i < cardLoopSize; ++i) {
      encryption *= doorPublic;
      encryption %= 20201227;
    }
    System.out.println(encryption);
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
