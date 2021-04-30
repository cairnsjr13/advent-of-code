package com.cairns.rich.aoc._2018;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.List;
import java.util.stream.Collectors;

class Day02 extends Base2018 {
  @Override
  protected void run() {
    List<String> boxIds = fullLoader.ml();
    System.out.println(computeChecksum(boxIds));
    System.out.println(getEqualCharsOfSimilarBoxes(boxIds));
  }
  
  private long computeChecksum(List<String> boxIds) {
    List<Multiset<Character>> charCounts = boxIds.stream().map(this::toCharCount).collect(Collectors.toList());
    return charCounts.stream().filter((charCount) -> hasExactlyCount(charCount, 2)).count()
         * charCounts.stream().filter((charCount) -> hasExactlyCount(charCount, 3)).count();
  }
  
  private boolean hasExactlyCount(Multiset<Character> charCounts, int target) {
    for (char ch : charCounts.elementSet()) {
      if (charCounts.count(ch) == target) {
        return true;
      }
    }
    return false;
  }
  
  private Multiset<Character> toCharCount(String str) {
    Multiset<Character> charCount = HashMultiset.create();
    str.chars().forEach((ch) -> charCount.add((char) ch));
    return charCount;
  }
  
  private String getEqualCharsOfSimilarBoxes(List<String> boxIds) {
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
    if (indexOfIncorrect == -1) throw fail(left + ", " + right);
    return left.substring(0, indexOfIncorrect) + left.substring(indexOfIncorrect + 1);
  }
}
