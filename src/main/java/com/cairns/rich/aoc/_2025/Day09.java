package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Red tiles are laid out on the floor to create potential rectangles.  We need to maximize
 * the area contained in valid rectangles where valid is defined differently in each part.
 */
class Day09 extends Base2025 {
  /**
   * The largest rectangle formable is the first in the area sorted rectangles returned by {@link #allRectangles(List)}.
   */
  @Override
  protected Object part1(Loader loader) {
    List<ImmutablePoint> reds = loader.ml(this::parse);
    return allRectangles(reds).poll().area();
  }

  /**
   * The red tiles have an order which form horizontal or vertical lines of green tiles between them.  This creates a
   * shape whose interior tiles are also green.  We need to find the maximum area rectangle that has opposite corners
   * red, and ALL other tiles red or green.  This can be computed by ordering all rectangles by their size (to make
   * the algorithm short circuit sooner) and then checking if it is valid.  Validity can be determined by checking if
   * ANY of the red-red lines intersect the interior of the rectangle.
   *
   * NOTE: I believe there are valid inputs that could result in this algorithm failing, but the actual structure
   *       of all inputs do not form these edge condition cases.
   */
  @Override
  protected Object part2(Loader loader) {
    List<ImmutablePoint> reds = loader.ml(this::parse);
    List<Line> lines = allLines(reds);
    PriorityQueue<Rectangle> rectangles = allRectangles(reds);
    while (!rectangles.isEmpty()) {
      Rectangle rectangle = rectangles.poll();
      if (lines.stream().noneMatch(rectangle::interiorIntersectedBy)) {
        return rectangle.area();
      }
    }
    throw fail();
  }

  /**
   * Helper function to compute all of the rectangles that can be formed by selecting a red tile on opposite corners.
   * The returned {@link PriorityQueue} is sorted by the largest area to speed up following searches.
   */
  private PriorityQueue<Rectangle> allRectangles(List<ImmutablePoint> reds) {
    PriorityQueue<Rectangle> rectangles = new PriorityQueue<>(Comparator.comparingLong(Rectangle::area).reversed());
    for (int i = 0; i < reds.size(); ++i) {
      ImmutablePoint first = reds.get(i);
      for (int j = i + 1; j < reds.size(); ++j) {
        ImmutablePoint second = reds.get(j);
        rectangles.add(new Rectangle(first, second));
      }
    }
    return rectangles;
  }

  /**
   * Helper function to compute all red-red tile connecting lines in the order they appear in the given reds list.
   * Will wrap the last tile back to the first tile.
   */
  private List<Line> allLines(List<ImmutablePoint> reds) {
    List<Line> lines = new ArrayList<>();
    for (int i = 0; i < reds.size(); ++i) {
      lines.add(new Line(reds.get(i), safeGet(reds, i + 1)));
    }
    return lines;
  }

  /**
   * Container describing a discrete, endpoint inclusive line segment.
   *
   * NOTE: in this problem, lines are required to be either horizontal or vertical.
   *       because of this, the concept of low vs high is sound since at least one of the coords match.
   */
  private static class Line {
    private final ImmutablePoint low;
    private final ImmutablePoint high;

    private Line(ImmutablePoint first, ImmutablePoint second) {
      this.low = new ImmutablePoint(Math.min(first.x(), second.x()), Math.min(first.y(), second.y()));
      this.high = new ImmutablePoint(Math.max(first.x(), second.x()), Math.max(first.y(), second.y()));
    }
  }

  /**
   * Container describing a discrete, boundary inclusive rectangle specified by its upperLeft and lowerRight corners.
   */
  private static class Rectangle {
    private final ImmutablePoint upperLeft;
    private final ImmutablePoint bottomRight;

    private Rectangle(ImmutablePoint first, ImmutablePoint second) {
      this.upperLeft = new ImmutablePoint(Math.min(first.x(), second.x()), Math.min(first.y(), second.y()));
      this.bottomRight = new ImmutablePoint(Math.max(first.x(), second.x()), Math.max(first.y(), second.y()));
    }

    /**
     * Returns if any discrete point on the given line intersects with any non-boundary position inside the rectangle.
     * Vertical lines need to have their x's strictly within the bounds of the rectangle and y endpoints intersecting.
     * Horizontal lines need to have their y's strictly within the bounds of the rectangle and x endpoints intersecting.
     */
    private boolean interiorIntersectedBy(Line line) {
      if (line.low.x() == line.high.x()) {  // vert
        return (upperLeft.x() < line.low.x()) && (line.low.x() < bottomRight.x())
            && (line.low.y() < bottomRight.y()) && (upperLeft.y() < line.high.y());
      }
      else if (line.low.y() == line.high.y()) { //horiz
        return (upperLeft.y() < line.low.y()) && (line.low.y() < bottomRight.y())
            && (line.low.x() < bottomRight.x()) && (upperLeft.x() < line.high.x());
      }
      throw fail("Line must be either horiz or vert: [" + line.low + "-" + line.high + "]");
    }

    /**
     * Computes the discrete, boundary inclusive area of this rectangle.
     */
    private long area() {
      return (1L + bottomRight.x() - upperLeft.x()) * (1L + bottomRight.y() - upperLeft.y());
    }
  }

  /**
   * Parses an {@link ImmutablePoint} from the given input line, splitting on a single comma (,).
   */
  private ImmutablePoint parse(String line) {
    String[] pieces = line.split(",");
    return new ImmutablePoint(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]));
  }
}
