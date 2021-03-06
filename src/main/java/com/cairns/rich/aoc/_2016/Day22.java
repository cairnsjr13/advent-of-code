package com.cairns.rich.aoc._2016;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day22 extends Base2016 {
  private static final Pattern pattern = Pattern.compile("^/dev/grid/node-x(\\d+)-y(\\d+) +\\d+T +(\\d+)T +(\\d+)T +\\d+%$");
  
  @Override
  protected void run() {
    List<String> nodeSpecs = fullLoader.ml();
    short[][] nodes = getGrid(nodeSpecs, 37, 25);
    System.out.println(countViablePairs(nodes));
    System.out.println(getFewestOperations(nodes));
  }
  
  private int countViablePairs(short[][] grid) {
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
  
  private int getEncodedLocationOfEmptyNode(short[][] grid) {
    for (int x = 0; x < grid.length; ++x) {
      for (int y = 0; y < grid[0].length; ++y) {
        if (getUsed(grid[x][y]) == 0) {
          return x + (y << 8);
        }
      }
    }
    throw fail();
  }
  
  private int getFewestOperations(short[][] grid) {
    int encodedLocationOfEmptyNode = getEncodedLocationOfEmptyNode(grid);
    int xOfEmpty = (encodedLocationOfEmptyNode >> 0) & 0xff;
    int yOfEmpty = (encodedLocationOfEmptyNode >> 8) & 0xff;
    int distanceEmptyMustMove = grid.length - 2;
    return xOfEmpty + yOfEmpty + distanceEmptyMustMove + 5 * distanceEmptyMustMove + 1;
  }
  
  private short[][] getGrid(List<String> nodeSpecs, int cols, int rows) {
    short[][] grid = new short[cols][rows];
    for (String nodeSpec : nodeSpecs) {
      Matcher matcher = matcher(pattern, nodeSpec);
      int x = Integer.parseInt(matcher.group(1));
      int y = Integer.parseInt(matcher.group(2));
      int used = Integer.parseInt(matcher.group(3));
      int avail = Integer.parseInt(matcher.group(4));
      grid[x][y] = encode(used, avail);
    }
    return grid;
  }
  
  private int getUsed(short encoded) {
    return (encoded >> 0) & 0xff;
  }
  
  private int getAvail(short encoded) {
    return (encoded >> 8) & 0xff;
  }
  
  private short encode(int used, int avail) {
    return (short) ((used << 0) + (avail << 8));
  }
}
