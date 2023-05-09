package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We have accessed a storage cluster arranged in a grid.  This particular cluster is setup in such a way that
 * each node can only interact with nodes adjacent to it.  We have an initial state of each node (used/avail)
 * and we can move (not copy) data around how we need to.  This will allow us to access what we need.
 */
class Day22 extends Base2016 {
  private static final Pattern pattern = Pattern.compile("^/dev/grid/node-x(\\d+)-y(\\d+) +\\d+T +(\\d+)T +(\\d+)T +\\d+%$");

  /**
   * Computes the number of pairs (across the whole cluster) such that they
   * are different nodes and the first can move its data to the second.
   */
  @Override
  protected Object part1(Loader loader) {
    short[][] grid = getGrid(loader);
    int numPairs = 0;
    for (int x1 = 0; x1 < grid.length; ++x1) {
      for (int y1 = 0; y1 < grid[0].length; ++y1) {
        int used = getUsed(grid[x1][y1]);
        for (int x2 = 0; x2 < grid.length; ++x2) {
          for (int y2 = 0; y2 < grid[0].length; ++y2) {
            if ((x1 != x2) || (y1 != y2)) {
              int avail = getAvail(grid[x2][y2]);
              if ((0 < used) && (used <= avail)) {
                ++numPairs;
              }
            }
          }
        }
      }
    }
    return numPairs;
  }

  /**
   * Computes the minimum number of steps required to move all of the data from the target node
   * (the top right node) back to the origin.  There seems to always be an empty node which we
   * can use to our advantage.  The movement is as followed:
   *   - position empty next to the top right node
   *   - 5 moves for each loop to move the data. (one for the data, four to reposition empty)
   *   - 1 more to move data a final time
   *
   * TODO: This formula isn't quite right.  It doesnt work for the 3x3 example
   */
  @Override
  protected Object part2(Loader loader) {
    short[][] grid = getGrid(loader);
    short encodedLocationOfEmptyNode = getEncodedLocationOfEmptyNode(grid);
    int xOfEmpty = getUsed(encodedLocationOfEmptyNode);
    int yOfEmpty = getAvail(encodedLocationOfEmptyNode);
    int distanceEmptyMustMove = grid.length - 2;
    return xOfEmpty + yOfEmpty + distanceEmptyMustMove + 5 * distanceEmptyMustMove + 1;
  }

  /**
   * Returns the encoded (using {@link #encode(int, int)}) location of the node
   * with no used space.  X will be encoded as used and y will be encoded as avail.
   */
  private short getEncodedLocationOfEmptyNode(short[][] grid) {
    for (int x = 0; x < grid.length; ++x) {
      for (int y = 0; y < grid[0].length; ++y) {
        if (getUsed(grid[x][y]) == 0) {
          return encode(x, y);
        }
      }
    }
    throw fail();
  }

  /**
   * Parses the encoded data grid from the input.  Each location represents
   * a node where the data is encoded with {@link #encode(int, int)}.  The
   * input is assumed to be in sorted order where the largest col/row node
   * is found in the last line of the input.
   */
  private short[][] getGrid(Loader loader) {
    List<String> nodeSpecs = loader.ml();
    Matcher lastMatcher = matcher(pattern, safeGet(nodeSpecs, -1));
    int cols = num(lastMatcher, 1) + 1;
    int rows = num(lastMatcher, 2) + 1;
    short[][] grid = new short[cols][rows];
    for (String nodeSpec : nodeSpecs) {
      Matcher matcher = matcher(pattern, nodeSpec);
      int x = num(matcher, 1);
      int y = num(matcher, 2);
      int used = num(matcher, 3);
      int avail = num(matcher, 4);
      grid[x][y] = encode(used, avail);
    }
    return grid;
  }

  /**
   * Returns the 8 lowest significant bits from the given encoded data to fetch the used space.
   */
  private int getUsed(short encoded) {
    return (encoded >> 0) & 0xff;
  }

  /**
   * Returns the 8 highest significant bits from the given encoded data to fetch the available space.
   */
  private int getAvail(short encoded) {
    return (encoded >> 8) & 0xff;
  }

  /**
   * Encodes the data such that used is at bits 0-7 and avail is at bits 8-15.
   * Decode using {@link #getUsed(short)} and {@link #getAvail(short)}.
   */
  private short encode(int used, int avail) {
    return (short) ((used << 0) + (avail << 8));
  }
}
