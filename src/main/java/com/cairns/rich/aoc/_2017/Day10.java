package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cairns.rich.aoc._2017.KnotHash.State;

class Day10 extends Base2017 {
  @Override
  protected void run() {
    List<String> input = fullLoader.ml();
    System.out.println(getProductOfFirstTwoInKnotHash(256, loadPart1(input)));
    System.out.println(KnotHash.getKnotHash(256, loadPart2(input)));
  }
  
  private int getProductOfFirstTwoInKnotHash(int size, List<Integer> lengths) {
    State state = new State(size);
    state.knot(lengths);
    return state.list[0] * state.list[1];
  }
  
  private List<Integer> loadPart1(List<String> input) {
    return Arrays.stream(input.get(0).split(", *")).map(Integer::parseInt).collect(Collectors.toList());
  }
  
  private List<Integer> loadPart2(List<String> input) {
    return KnotHash.getLengthsFromString(input.get(0));
  }
}
