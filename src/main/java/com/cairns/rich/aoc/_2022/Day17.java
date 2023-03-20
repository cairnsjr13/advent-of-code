package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.RelDir;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO: Add a test to verify all of the results in maxHeights above the cycle point
class Day17 extends Base2022 {
  private static final Shape[] shapes = EnumUtils.enumValues(Shape.class);

  @Override
  protected Object part1(Loader2 loader) {
    String jets = loader.sl();
    List<Integer> maxHeights = new ArrayList<>();
    maxHeights.add(0);
    dropPiecesAndFindRestarts(jets, 2022, maxHeights);
    return maxHeights.get(2022);
  }

  @Override
  protected Object part2(Loader2 loader) {
    String jets = loader.sl();
    List<Integer> maxHeights = new ArrayList<>();
    maxHeights.add(0);
    List<Integer> factors = findFactors(dropPiecesAndFindRestarts(jets, 10_000, maxHeights));
    int firstRepeatDrop = factors.get(0);
    int dropCycle = factors.get(1);
    int firstRepeatMaxY = factors.get(2);
    int maxYCycle = factors.get(3);

    long numFalls = 1_000_000_000_000L;
    long jumps = (numFalls - firstRepeatDrop) / dropCycle;
    long mod = (numFalls - firstRepeatDrop) % dropCycle;
    return firstRepeatMaxY
         + (jumps * maxYCycle)
         + 1
         + (maxHeights.get((int) (firstRepeatDrop + mod)) - maxHeights.get(firstRepeatDrop));
  }

  private List<Integer> findFactors(List<RestartDescription> restarts) {
    List<RestartDescription> deltas = IntStream.range(1, restarts.size())
        .mapToObj((i) -> restarts.get(i).delta(restarts.get(i - 1)))
        .collect(Collectors.toList());
    Predicate<ToIntFunction<RestartDescription>> allSame = (fn) -> 1 == deltas.stream().mapToInt(fn).distinct().count();
    if (!allSame.test((r) -> r.drop) || !allSame.test((r) -> r.shapeIndex) ||
        !allSame.test((r) -> r.jetIndex) || !allSame.test((r) -> r.maxY) ||
        (deltas.get(0).shapeIndex != 0) || (deltas.get(0).jetIndex != 0)
    ) {
      throw fail(restarts + " - " + deltas);
    }
    return List.of(restarts.get(0).drop, deltas.get(0).drop, restarts.get(0).maxY, deltas.get(0).maxY);
  }

  private List<RestartDescription> dropPiecesAndFindRestarts(String jets, int numFalls, List<Integer> maxHeights) {
    List<RestartDescription> restarts = new ArrayList<>();
    Set<Point<?>> board = new HashSet<>();
    int maxY = -1;
    int jetIndex = 0;
    for (int drop = 0; drop < numFalls; ++drop) {
      int shapeIndex = drop % shapes.length;
      if (topLineFull(board, maxY)) {
        restarts.add(new RestartDescription(drop, shapeIndex, jetIndex, maxY));
      }
      List<MutablePoint> falling = shapes[shapeIndex].generate(2, maxY + 4);
      while (true) {
        RelDir pushDir = (jets.charAt(jetIndex) == '<') ? RelDir.Left : RelDir.Right;
        jetIndex = (jetIndex + 1) % jets.length();
        falling.forEach((f) -> f.move(pushDir));
        if (!wasOkMove(board, falling)) {
          falling.forEach((f) -> f.move(pushDir.turnAround()));
        }
        falling.forEach((f) -> f.move(RelDir.Down));
        if (!wasOkMove(board, falling)) {
          falling.forEach((f) -> f.move(RelDir.Up));
          break;
        }
      }
      falling.stream().map(ImmutablePoint::new).forEach(board::add);
      maxY = Math.max(maxY, falling.stream().mapToInt(Point::y).max().getAsInt());
      maxHeights.add(maxY + 1);
    }
    return restarts;
  }

  private boolean topLineFull(Set<Point<?>> board, int maxY) {
    for (MutablePoint current = new MutablePoint(0, maxY); current.x() < 7; current.move(RelDir.Right)) {
      if (!board.contains(current)) {
        return false;
      }
    }
    return true;
  }

  private boolean wasOkMove(Set<Point<?>> board, List<MutablePoint> falling) {
    return falling.stream().allMatch((f) -> !board.contains(f) && (0 <= f.x()) && (f.x() < 7) && (0 <= f.y()));
  }

  private static class RestartDescription {
    private final int drop;
    private final int shapeIndex;
    private final int jetIndex;
    private final int maxY;

    private RestartDescription(int drop, int shapeIndex, int jetIndex, int maxY) {
      this.drop = drop;
      this.shapeIndex = shapeIndex;
      this.jetIndex = jetIndex;
      this.maxY = maxY;
    }

    private RestartDescription delta(RestartDescription other) {
      return new RestartDescription(
          drop - other.drop,
          shapeIndex - other.shapeIndex,
          jetIndex - other.jetIndex,
          maxY - other.maxY
      );
    }

    @Override
    public String toString() {
      return List.of(drop, shapeIndex, jetIndex, maxY).toString();
    }
  }

  private enum Shape {
    Horiz(
        "####"
    ),
    Plus(
        ".#.",
        "###",
        ".#."
    ),
    L(
        "..#",
        "..#",
        "###"
    ),
    Vert(
        "#",
        "#",
        "#",
        "#"),
    Square(
        "##",
        "##"
    );

    private final Set<ImmutablePoint> rocks = new HashSet<>();

    private Shape(String... desc) {
      for (int y = 0; y < desc.length; ++y) {
        for (int x = 0; x < desc[0].length(); ++x) {
          if (desc[y].charAt(x) == '#') {
            rocks.add(new ImmutablePoint(x, desc.length - y - 1));
          }
        }
      }
    }

    private List<MutablePoint> generate(int x, int y) {
      return rocks.stream().map((init) -> new MutablePoint(x + init.x(), y + init.y())).collect(Collectors.toList());
    }
  }
}
