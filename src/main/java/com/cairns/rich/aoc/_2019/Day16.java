package com.cairns.rich.aoc._2019;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day16 extends Base2019 {
  private int[] multiplierGroups = { 0, 1, 0, -1 };

  @Override
  protected void run() {
    List<Integer> input = fullLoader.sl("", Integer::parseInt);
    System.out.println(fullImpl(input));
    System.out.println(offsetImpl(input));
  }

  private String fullImpl(List<Integer> input) {
    return exec(
        new ArrayList<>(input),
        (in, next, outPos) -> IntStream.range(0, in.size()).map((inPos) -> in.get(inPos) * getMultiplier(outPos, inPos)).sum()
    );
  }

  private String offsetImpl(List<Integer> input) {
    return exec(
        buildLargeInput(input),
        (in, next, outPos) -> in.get(outPos) + ((outPos < next.size() - 1) ? next.get(outPos + 1) : 0)
    );
  }

  private List<Integer> buildLargeInput(List<Integer> input) {
    int offset = getOffset(input);
    List<Integer> largeInput = new ArrayList<>();
    largeInput.addAll(input.subList(offset % input.size(), input.size()));
    for (int i = offset / input.size() + 1; i < 10_000; ++i) {
      largeInput.addAll(input);
    }
    return largeInput;
  }

  private int getOffset(List<Integer> input) {
    int offset = 0;
    for (int i = 0; i < 7; ++i) {
      offset = (offset * 10) + input.get(i);
    }
    return offset;
  }

  private int getMultiplier(int outputPosition, int inputPosition) {
    return safeGet(multiplierGroups, (inputPosition + 1) / (outputPosition + 1));
  }

  private String exec(List<Integer> input, Impl impl) {
    List<Integer> next = new ArrayList<>(input);
    for (int i = 0; i < 100; ++i) {
      for (int outPos = input.size() - 1; outPos >= 0; --outPos) {
        next.set(outPos, Math.abs(impl.computeOutput(input, next, outPos) % 10));
      }

      List<Integer> cache = input;
      input = next;
      next = cache;
    }
    return input.subList(0, 8).stream().map(Object::toString).collect(Collectors.joining());
  }

  private interface Impl {
    int computeOutput(List<Integer> input, List<Integer> next, int outPos);
  }
}
