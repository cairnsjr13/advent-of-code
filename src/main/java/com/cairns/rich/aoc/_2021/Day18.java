package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

class Day18 extends Base2021 {
  @Override
  protected Object part1(Loader2 loader) {
    return getSumAllMagnitude(loader.ml());
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<String> inputs = loader.ml();
    long maxSumMagnitude = -1;
    for (int i = 0; i < inputs.size(); ++i) {
      for (int j = 0; j < inputs.size(); ++j) {
        if (i != j) {
          maxSumMagnitude = Math.max(
              maxSumMagnitude,
              getSumAllMagnitude(List.of(inputs.get(i), inputs.get(j)))
          );
        }
      }
    }
    return maxSumMagnitude;
  }

  private long getSumAllMagnitude(List<String> inputs) {
    SnailfishNode accumulator = parse(inputs.get(0));
    for (int i = 1; i < inputs.size(); ++i) {
      accumulator = new PairNode(accumulator, parse(inputs.get(i)));
      while (accumulator.explode(0) || accumulator.split()) ;
    }
    return accumulator.magnitude();
  }

  private SnailfishNode parse(String input) {
    return parse(null, input.chars().boxed().collect(Collectors.toList()).iterator());
  }

  private SnailfishNode parse(PairNode parent, Iterator<Integer> chars) {
    char ch = (char) chars.next().intValue();
    if (ch == '[') {
      PairNode pairNode = new PairNode(parent);
      pairNode.children[0] = parse(pairNode, chars);
      if (chars.next().intValue() != ',') {
        throw fail();
      }
      pairNode.children[1] = parse(pairNode, chars);
      if (chars.next().intValue() != ']') {
        throw fail();
      }
      return pairNode;
    }
    return new NumNode(parent, ch - '0');
  }

  private static abstract class SnailfishNode {
    protected PairNode parent;

    protected SnailfishNode(PairNode parent) {
      this.parent = parent;
    }

    protected abstract boolean explode(int depth);

    protected abstract boolean split();

    protected abstract long magnitude();

    protected void replaceInParent(SnailfishNode with) {
      for (int i = 0; i < parent.children.length; ++i) {
        if (parent.children[i] == this) {
          parent.children[i] = with;
          return;
        }
      }
      throw fail("Wrong nodes: " + Arrays.toString(parent.children) + " - (" + this + ", " + with + ")");
    }
  }

  private static class PairNode extends SnailfishNode {
    protected SnailfishNode[] children = new SnailfishNode[2];

    private PairNode(PairNode parent) {
      super(parent);
    }

    private PairNode(SnailfishNode left, SnailfishNode right) {
      super(null);
      children[0] = left;
      children[1] = right;
      left.parent = this;
      right.parent = this;
    }

    @Override
    protected boolean explode(int depth) {
      return children[0].explode(depth + 1) || explodePair(depth) || children[1].explode(depth + 1);
    }

    private boolean explodePair(int depth) {
      if (depth == 4) {
        addToNumNodeInDirection(0);
        addToNumNodeInDirection(1);
        replaceInParent(new NumNode(parent, 0));
        return true;
      }
      return false;
    }

    @Override
    protected boolean split() {
      return children[0].split() || children[1].split();
    }

    @Override
    protected long magnitude() {
      return 3 * children[0].magnitude() + 2 * children[1].magnitude();
    }

    private void addToNumNodeInDirection(int directionIndex) {
      PairNode from = this;
      while (from.parent.children[directionIndex] == from) {
        from = from.parent;
        if (from.parent == null) {
          return;
        }
      }
      SnailfishNode node = from.parent.children[directionIndex];
      while (node instanceof PairNode) {
        node = ((PairNode) node).children[1 - directionIndex];
      }
      ((NumNode) node).number += ((NumNode) children[directionIndex]).number;
    }
  }

  private static class NumNode extends SnailfishNode {
    private int number;

    private NumNode(PairNode parent, int number) {
      super(parent);
      this.number = number;
    }

    @Override
    protected boolean explode(int depth) {
      return false;
    }

    @Override
    protected boolean split() {
      if (number >= 10) {
        PairNode splitNode = new PairNode(parent);
        splitNode.children[0] = new NumNode(splitNode, number / 2);
        splitNode.children[1] = new NumNode(splitNode, number / 2 + number % 2);
        replaceInParent(splitNode);
        return true;
      }
      return false;
    }

    @Override
    protected long magnitude() {
      return number;
    }
  }
}
