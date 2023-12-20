package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We need to dig a lagoon for lava by following instructions to dig a trench.  The pit will
 * consist of all of the exterior trench, as well as spots enclosed by the trench.
 *
 * NOTE: We are using pick's theorem (https://en.m.wikipedia.org/wiki/Pick%27s_theorem)
 * and the shoelace formula (https://en.m.wikipedia.org/wiki/Shoelace_formula)
 */
class Day18 extends Base2023 {
  /**
   * Computes the filled capacity of the trench described by the normal reading of the instructions.
   */
  @Override
  protected Object part1(Loader loader) {
    Pattern pattern = Pattern.compile("^([UDLR]) (\\d+).*$");
    Map<Character, ReadDir> dirs = EnumUtils.getLookup(ReadDir.class);
    return computeFilledCapacity(loader, (line) -> {
      Matcher matcher = matcher(pattern, line);
      return new Inst(dirs.get(matcher.group(1).charAt(0)), num(matcher, 2));
    });
  }

  /**
   * Computes the filled capacity of the trench described by the hex based reading of the instructions
   */
  @Override
  protected Object part2(Loader loader) {
    Pattern pattern = Pattern.compile("^.*\\(#(.....)([0123])\\)$");
    Map<Character, ReadDir> dirs = Map.of('0', ReadDir.Right, '1', ReadDir.Down, '2', ReadDir.Left, '3', ReadDir.Up);
    return computeFilledCapacity(loader, (line) -> {
      Matcher matcher = matcher(pattern, line);
      return new Inst(dirs.get(matcher.group(2).charAt(0)), Integer.parseInt(matcher.group(1), 16));
    });
  }

  /**
   * Uses pick's theorem to compute the total filled capacity of the shape described by the given input instructions.
   * The filled capacity is the sum of the number of boundary points and interior points.  We can use the shoelace
   * formula to compute the area of the shape, which can in turn be used to compute the number of interior points.
   *
   * Pick's theorem states that the area of the shape (A) is equal to the number of interior points plus half the boundary
   * points minus 1: A = i + b/2 - 1.  We can use the shoelace formula to compute the area from the vertexes.
   */
  private long computeFilledCapacity(Loader loader, Function<String, Inst> parser) {
    List<Inst> insts = loader.ml(parser);
    ArrayDeque<ImmutablePoint> vertexes = new ArrayDeque<>();
    long numBoundaryPoints = computeVertexesAndGetBoundaryPoints(insts, vertexes);
    return getArea(vertexes) + 1 + numBoundaryPoints / 2;
  }

  /**
   * Adds all of the vertexes of the trench shape to the given deque.  Returns the number of
   * boundary points along the edge.  This is just the span of the range of the edge.
   */
  private long computeVertexesAndGetBoundaryPoints(List<Inst> insts, ArrayDeque<ImmutablePoint> vertexes) {
    long numBoundaryPoints = 0;
    ImmutablePoint cur = ImmutablePoint.origin;
    vertexes.offer(cur);
    for (Inst inst : insts) {
      ImmutablePoint next = cur.move(inst.dir, inst.mag);
      numBoundaryPoints += Math.abs(next.x() - cur.x()) + Math.abs(next.y() - cur.y());
      vertexes.offer(next);
      cur = next;
    }
    return numBoundaryPoints;
  }

  /**
   * Computes the area inscribed by the shape described by the given in order vertexes.  Uses the shoelace
   * formula to compute the crossproduct of each sliding pair of vertexes, which will yield twice the area.
   */
  private long getArea(ArrayDeque<ImmutablePoint> vertexes) {
    long total = 0;
    while (vertexes.size() >= 2) {
      ImmutablePoint first = vertexes.poll();
      ImmutablePoint second = vertexes.peek();
      total += (((long) first.x()) * ((long) second.y()) - ((long) first.y()) * ((long) second.x()));
    }
    return total / 2;
  }

  /**
   * Descriptor class for a trench digging instruction.
   */
  private static class Inst {
    private final ReadDir dir;
    private final int mag;

    private Inst(ReadDir dir, int mag) {
      this.dir = dir;
      this.mag = mag;
    }
  }
}
