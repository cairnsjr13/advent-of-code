package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.List;
import java.util.function.IntToLongFunction;

class Day02 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    List<String> boxIds = fullLoader.ml();
    IntToLongFunction numWithCount = (target) -> boxIds.stream()
        .map((str) -> str.chars().mapToObj((i) -> (char) i).collect(HashMultiset::create, Multiset::add, Multiset::addAll))
        .filter((cc) -> cc.elementSet().stream().anyMatch((ch) -> target == cc.count(ch)))
        .count();
    return numWithCount.applyAsLong(2) * numWithCount.applyAsLong(3);
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<String> boxIds = fullLoader.ml();
    for (int l = 0; l < boxIds.size(); ++l) {
      String leftBoxId = boxIds.get(l);
      for (int r = l + 1; r < boxIds.size(); ++r) {
        String rightBoxId = boxIds.get(r);
        String matchPart = getEqualPartsOfSimilar(leftBoxId, rightBoxId);
        if (matchPart != null) {
          return matchPart;
        }
      }
    }
    throw fail();
  }

  private String getEqualPartsOfSimilar(String left, String right) {
    int indexOfIncorrect = -1;
    for (int i = 0; i < left.length(); ++i) {
      if (left.charAt(i) != right.charAt(i)) {
        if (indexOfIncorrect != -1) {
          return null;
        }
        indexOfIncorrect = i;
      }
    }
    if (indexOfIncorrect == -1) {
      throw fail(left + ", " + right);
    }
    return left.substring(0, indexOfIncorrect) + left.substring(indexOfIncorrect + 1);
  }
}
