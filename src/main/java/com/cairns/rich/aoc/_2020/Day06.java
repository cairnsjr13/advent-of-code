package com.cairns.rich.aoc._2020;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Day06 extends Base2020 {
  @Override
  protected void run() {
    List<String> input = fullLoader.ml();
    List<Group> groups = getGroups(input);
    System.out.println(groups.stream().mapToLong(Group::numAny).sum());
    System.out.println(groups.stream().mapToLong(Group::numAll).sum());
  }
  
  private List<Group> getGroups(List<String> input) {
    List<Group> groups = new ArrayList<>();
    Group currentGroup = new Group();
    for (String line : input) {
      if (line.isEmpty()) {
        groups.add(currentGroup);
        currentGroup = new Group();
        continue;
      }
      currentGroup.add(line);
    }
    return groups;
  }
  
  private static class Group {
    private final int[] qs = new int[26];
    private int numPeople = 0;
    
    private void add(String line) {
      ++numPeople;
      for (char ch : line.toCharArray()) {
        ++qs[ch - 'a'];
      }
    }
    
    private long numAny() {
      return Arrays.stream(qs).filter((q) -> q > 0).count();
    }
    
    private long numAll() {
      return Arrays.stream(qs).filter((q) -> q == numPeople).count();
    }
  }
}
