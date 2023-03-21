package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader;
import java.util.function.Function;

class Day08 extends Base2019 {
  @Override
  protected Object part1(Loader loader) {
    return parseAndFind(loader, this::computeChecksum);
  }

  @Override
  protected Object part2(Loader loader) {
    return parseAndFind(loader, this::printImage);
  }

  private <T> T parseAndFind(Loader loader, Function<int[][][], T> toAnswer) {
    int[][][] image = parseLayerRowCol(loader.sl(), 25, 6);
    return toAnswer.apply(image);
  }

  private int computeChecksum(int[][][] image) {
    int[] fewestZeroLayerCount = { Integer.MAX_VALUE, 0, 0 };
    for (int layer = 0; layer < image.length; ++layer) {
      int[] layerCount = new int[3];
      for (int row = 0; row < image[0].length; ++row) {
        for (int col = 0; col < image[0][0].length; ++col) {
          ++layerCount[image[layer][row][col]];
        }
      }
      if (layerCount[0] < fewestZeroLayerCount[0]) {
        fewestZeroLayerCount = layerCount;
      }
    }
    return fewestZeroLayerCount[1] * fewestZeroLayerCount[2];
  }

  private StringBuilder printImage(int[][][] image) {
    StringBuilder out = new StringBuilder("\n");
    for (int row = 0; row < image[0].length; ++row) {
      for (int col = 0; col < image[0][0].length; ++col) {
        out.append((isPixelSet(image, row, col)) ? DARK_PIXEL : ' ');
      }
      out.append("\n");
    }
    return out;
  }

  private boolean isPixelSet(int[][][] image, int row, int col) {
    for (int layer = 0; layer < image.length; ++layer) {
      if (image[layer][row][col] != 2) {
        return image[layer][row][col] == 1;
      }
    }
    throw fail(row + ", " + col);
  }

  private int[][][] parseLayerRowCol(String input, int numCols, int numRows) {
    int numLayers = input.length() / (numCols * numRows);
    int[][][] image = new int[numLayers][numRows][numCols];
    for (int layer = 0; layer < numLayers; ++layer) {
      for (int row = 0; row < numRows; ++row) {
        for (int col = 0; col < numCols; ++col) {
          image[layer][row][col] = input.charAt(layer * (numRows * numCols) + row * numCols + col) - '0';
        }
      }
    }
    return image;
  }
}
