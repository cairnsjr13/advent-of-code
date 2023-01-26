package com.cairns.rich.aoc._2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.RelDir;

public class Day17 extends Base2022 {
  private static final Shape[] shapes = EnumUtils.enumValues(Shape.class);
  
  @Override
  protected void run() throws Throwable {
    String jets = fullLoader.sl();
    List<Integer> maxHeights = new ArrayList<>();
    maxHeights.add(0);
    getHeightAfter(jets, 5000, maxHeights);
    long numFalls = 1_000_000_000_000L;
    long jumps = (numFalls - 561) / 1725;
    long mod = (numFalls - 561) % 1725;
    long expected = 855 + (jumps * 2694) + 1 + (maxHeights.get((int) (561 + mod)) - maxHeights.get(561));
    System.out.println(expected);
    
    /*
    int maxFalls = 561 + (100 * 1725);
    getHeightAfter(jets, maxFalls, maxHeights);
    for (int jumps = 0; jumps < 100; ++jumps) {
      for (int mod = 0; mod < 100; ++mod) {
        int error = printReport(maxHeights, 561 + (jumps * 1725) + mod);
        if (error != 0) {
          throw fail(jumps + " " + mod);
        }
      }
    }
    */
    //System.out.println(getHeightAfter(jets, 20220));
  }
  
  private int printReport(List<Integer> maxHeights, int numFalls) {
    int jumps = (numFalls - 561) / 1725;
    int mod = (numFalls - 561) % 1725;
    int expected = 855 + (jumps * 2694) + 1 + (maxHeights.get(561 + mod) - maxHeights.get(561));
    int actual = maxHeights.get(numFalls);
    //System.out.println(numFalls + "\t" + expected + "\t" + actual + "\t" + (actual - expected));
    return actual - expected;
  }
  
  private void getHeightAfter(String jets, int numFalls, List<Integer> maxHeights) {
    List<List<Integer>> marks = new ArrayList<>();
    Set<Point<?>> board = new HashSet<>();
    int maxY = -1;
    int gi = 0;
    for (int i = 0; i < numFalls; ++i) {
      int ri = i % shapes.length;
      if (topLineFull(board, maxY)) {
        //marks.add(List.of(i, ri, gi, maxY));
      }
      List<MutablePoint> falling = shapes[ri].generate(2, maxY + 4);
      while (true) {
        RelDir pushDir = (jets.charAt(gi) == '<') ? RelDir.Left : RelDir.Right;
        gi = (gi + 1) % jets.length();
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
    
    for (int i = 0; i < marks.size(); ++i) {
      System.out.print(marks.get(i) + " - ");
      if (i > 0) {
        int ii = i;
        System.out.print(
            IntStream.range(0, marks.get(0).size()).mapToObj((j) -> marks.get(ii).get(j) - marks.get(ii - 1).get(j)).collect(Collectors.toList())
        );
      }
      System.out.println();
    }
  }
  
  private boolean topLineFull(Set<Point<?>> board, int maxY) {
    for (MutablePoint current = new MutablePoint(0, maxY); current.x() < 7; current.move(RelDir.Right)) {
      if (!board.contains(current)) {
        return false;
      }
    }
    return true;
  }
  
  private void print(Set<Point<?>> board, List<MutablePoint> falling) {
    System.out.println();
    MutablePoint p = new MutablePoint(0, 0);
    for (int y = 20; y >= 0; --y) {
      p.y(y);
      for (int x = 0; x < 7; ++x) {
        p.x(x);
        System.out.print((board.contains(p) || falling.contains(p)) ? '#' : '.');
      }
      System.out.println();
    }
  }
  
  private boolean wasOkMove(Set<Point<?>> board, List<MutablePoint> falling) {
    return falling.stream().allMatch((f) -> !board.contains(f) && (0 <= f.x()) && (f.x() < 7) && (0 <= f.y()));
  }
  
  private enum Shape {
    Horiz(
        "####"
    ),
    Plus(
        ".#.\n"
      + "###\n"
      + ".#."
    ),
    L(
        "..#\n" 
      + "..#\n"
      + "###"
    ),
    Vert(
        "#\n"
      + "#\n"
      + "#\n"
      + "#"),
    Square(
        "##\n"
      + "##"
    );
    
    private final Set<ImmutablePoint> rocks = new HashSet<>();
    
    private Shape(String desc) {
      String[] lines = desc.split("\\n");
      for (int y = 0; y < lines.length; ++y) {
        for (int x = 0; x < lines[0].length(); ++x) {
          if (lines[y].charAt(x) == '#') {
            rocks.add(new ImmutablePoint(x, lines.length - y - 1));
          }
        }
      }
    }
    
    private List<MutablePoint> generate(int offsetX, int offsetY) {
      return  rocks.stream().map((init) -> new MutablePoint(offsetX + init.x(), offsetY + init.y())).collect(Collectors.toList());
    }
  }
}
