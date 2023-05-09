package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * A kinetic sculpture is dropping capsules and we want one.  We need to figure out when
 * all of the slots line up so we can drop one all the way through all of the spinning discs.
 */
class Day15 extends Base2016 {
  /**
   * Computes the first time that dropping a capsule would result in a win with the input discs.
   */
  @Override
  protected Object part1(Loader loader) {
    return getTWhenFalls(loader.ml(DiscDesc::new));
  }

  /**
   * Computes the first time that a dropping capsule would result in a win
   * with an extra disc of size 11 at the very bottom of the input discs.
   */
  @Override
  protected Object part2(Loader loader) {
    List<DiscDesc> discDescs = loader.ml(DiscDesc::new);
    discDescs.add(new DiscDesc(11, 0));
    return getTWhenFalls(discDescs);
  }

  /**
   * Finds the first time when a capsule will fall all the way through with the given discs.
   */
  private int getTWhenFalls(List<DiscDesc> discDescs) {
    return IntStream.iterate(0, (t) -> t + 1).filter((t) -> doesFall(discDescs, t)).findFirst().getAsInt();
  }

  /**
   * Returns true if all of the discs would be at position zero when the capsule reached them if we start at the given time.
   */
  private boolean doesFall(List<DiscDesc> discDescs, int t) {
    return IntStream.range(0, discDescs.size()).allMatch((disc) -> {
      DiscDesc discDesc = discDescs.get(disc);
      int discPosition = (t + (disc + 1) + discDesc.startPosition) % discDesc.numPositions;
      return discPosition == 0;
    });
  }

  /**
   * Descriptor class for one of the spinning discs involved in the game.  A disc is
   * a number of positions with a starting positions.  Position zero is the opening.
   */
  private static class DiscDesc {
    private static final Pattern pattern =
        Pattern.compile("^Disc #\\d has (\\d+) positions; at time=0, it is at position (\\d+)\\.$");

    private final int numPositions;
    private final int startPosition;

    private DiscDesc(int numPositions, int startPosition) {
      this.numPositions = numPositions;
      this.startPosition = startPosition;
    }

    private DiscDesc(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.numPositions = num(matcher, 1);
      this.startPosition = num(matcher, 2);
    }
  }
}
