package com.cairns.rich.aoc._2021;

import java.util.BitSet;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Day20 extends Base2021 {
  @Override
  protected void run() {
    List<String> input = fullLoader.ml();
    BitSet algorithm = parseAlgorithm(input.get(0));
    Image initialImage = parseInitialImage(input.subList(2, input.size()));
    
    System.out.println(getLitPixelsAfter(algorithm, initialImage, 2));
    System.out.println(getLitPixelsAfter(algorithm, initialImage, 50));
  }
  
  private int getLitPixelsAfter(BitSet algorithm, Image image, int numSteps) {
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
    return (image.getPixelValue(x - 1, y - 1) << 8)
         + (image.getPixelValue(x    , y - 1) << 7)
         + (image.getPixelValue(x + 1, y - 1) << 6)
         + (image.getPixelValue(x - 1, y    ) << 5)
         + (image.getPixelValue(x    , y    ) << 4)
         + (image.getPixelValue(x + 1, y    ) << 3)
         + (image.getPixelValue(x - 1, y + 1) << 2)
         + (image.getPixelValue(x    , y + 1) << 1)
         + (image.getPixelValue(x + 1, y + 1) << 0);
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
        if (infiniteOn) {
          return 1;
        }
      }
      return (litPoints.containsEntry(x, y)) ? 1 : 0;
    }
  }
}
