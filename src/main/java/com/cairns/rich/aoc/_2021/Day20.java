package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.BitSet;
import java.util.List;

class Day20 extends Base2021 {
  @Override
  protected Object part1(Loader loader) {
    return getLitPixelsAfter(loader, 2);
  }

  @Override
  protected Object part2(Loader loader) {
    return getLitPixelsAfter(loader, 50);
  }

  private int getLitPixelsAfter(Loader loader, int numSteps) {
    List<String> input = loader.ml();
    BitSet algorithm = parseAlgorithm(input.get(0));
    Image image = parseInitialImage(input.subList(2, input.size()));
    for (int i = 0; i < numSteps; ++i) {
      image = applyAlgorithm(algorithm, image);
    }
    return image.litPoints.size();
  }

  private Image applyAlgorithm(BitSet algorithm, Image inputImage) {
    Image outputImage = new Image(algorithm.get(0) && !inputImage.infiniteOn);
    for (int x = inputImage.minX - 1; x <= inputImage.maxX + 1; ++x) {
      for (int y = inputImage.minY - 1; y <= inputImage.maxY + 1; ++y) {
        if (algorithm.get(findLookupIndex(inputImage, x, y))) {
          outputImage.registerLitPoint(x, y);
        }
      }
    }
    return outputImage;
  }

  private int findLookupIndex(Image image, int x, int y) {
    int lookupIndex = 0;
    for (int i = 0; i < 9; ++i) {
      int dy = 1 - (i / 3);
      int dx = 1 - (i % 3);
      lookupIndex += (image.getPixelValue(x + dx, y + dy) << i);
    }
    return lookupIndex;
  }

  private BitSet parseAlgorithm(String input) {
    BitSet algorithm = new BitSet(512);
    for (int i = 0; i < input.length(); ++i) {
      if (input.charAt(i) == '#') {
        algorithm.set(i);
      }
    }
    return algorithm;
  }

  private Image parseInitialImage(List<String> input) {
    Image initialImage = new Image(false);
    for (int y = 0; y < input.size(); ++y) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); ++x) {
        if (line.charAt(x) == '#') {
          initialImage.registerLitPoint(x, y);
        }
      }
    }
    return initialImage;
  }

  private static final class Image {
    private final Multimap<Integer, Integer> litPoints = HashMultimap.create();
    private final boolean infiniteOn;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    private Image(boolean infiniteOn) {
      this.infiniteOn = infiniteOn;
    }

    private void registerLitPoint(int x, int y) {
      litPoints.put(x, y);
      minX = Math.min(x, minX);
      maxX = Math.max(x, maxX);
      minY = Math.min(y, minY);
      maxY = Math.max(y, maxY);
    }

    private int getPixelValue(int x, int y) {
      if ((x < minX) || (maxX < x) || (y < minY) || (maxY < y)) {
        return (infiniteOn) ? 1 : 0;
      }
      return (litPoints.containsEntry(x, y)) ? 1 : 0;
    }
  }
}
