package com.cairns.rich.aoc._2019;

import org.apache.commons.lang3.StringUtils;

class Day08 extends Base2019 {
  @Override
  protected void run() {
    String input = fullLoader.sl();
    int[][][] image = parseLayerRowCol(input, 25, 6);
    System.out.println(computeChecksum(image));
    printImage(image);
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
  
  private void printImage(int[][][] image) {
    char setPixel = 0x2588;
    System.out.println(StringUtils.repeat(setPixel, image[0][0].length + 1));
    for (int row = 0; row < image[0].length; ++row) {
      System.out.print(setPixel);
      for (int col = 0; col < image[0][0].length; ++col) {
        System.out.print((isPixelSet(image, row, col)) ? setPixel : ' ');
      }
      System.out.println();
    }
    System.out.println(StringUtils.repeat(setPixel, image[0][0].length + 1));
  }
  
  private boolean isPixelSet(int[][][] image, int row, int col) {
    for (int layer = 0; layer < image.length; ++layer) {
      if (image[layer][row][col] != 2) {
        return image[layer][row][col] == 0;
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
