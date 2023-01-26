package com.cairns.rich.aoc._2021;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class Day03 extends Base2021 {
  @Override
  protected void run() throws Throwable {
    List<String> inputs = fullLoader.ml();
    System.out.println(partOne(inputs));
    System.out.println(partTwo(inputs));
  }
  
  private int partOne(List<String> inputs) {
    int[][] counts = new int[inputs.get(0).length()][2];
    for (String input : inputs) {
      for (int i = 0; i < counts.length; ++i) {
        ++counts[i][input.charAt(i) - '0'];
      }
    }
    
    int gamma = 0;
    int epsilon = 0;
    for (int i = 0; i < counts.length; ++i) {
      int gammaIncValue = (counts[i][0] > counts[i][1]) ? 0 : 1;
      gamma = gamma * 2 + gammaIncValue;
      epsilon = epsilon * 2 + (1 - gammaIncValue);
    }
    return gamma * epsilon;
  }
  
  private int partTwo(List<String> inputs) {
    return lifeSupportPiece(inputs, (zeroCount, oneCount) -> zeroCount > oneCount)
         * lifeSupportPiece(inputs, (zeroCount, oneCount) -> zeroCount <= oneCount);
  }
  
  private int lifeSupportPiece(List<String> inputs, BiPredicate<Integer, Integer> useZero) {
    List<String> current = new ArrayList<>(inputs);
    for (int bitIndex = 0; current.size() > 1; ++bitIndex) {
      List<String> nextIfZero = new ArrayList<>();
      List<String> nextIfOne = new ArrayList<>();
      for (String input : current) {
        ((input.charAt(bitIndex) == '0') ? nextIfZero : nextIfOne).add(input);
      }
      current = (useZero.test(nextIfZero.size(), nextIfOne.size())) ? nextIfZero : nextIfOne;
    }
    return Integer.parseInt(current.get(0), 2);
  }
}
