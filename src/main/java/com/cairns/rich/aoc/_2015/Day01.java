package com.cairns.rich.aoc._2015;

class Day01 extends Base2015 {
  @Override
  protected void run() {
    String input = fullLoader.sl();
    System.out.println(getFinalFloor(input));
    System.out.println(getFirstBasementInstruction(input));
  }

  private int getFinalFloor(String input) {
    int floor = 0;
    for (int i = 0; i < input.length(); ++i) {
      floor += (input.charAt(i) == '(') ? 1 : -1;
    }
    return floor;
  }

  private int getFirstBasementInstruction(String input) {
    int floor = 0;
    for (int i = 0; i < input.length(); ++i) {
      floor += (input.charAt(i) == '(') ? 1 : -1;
      if (floor == -1) {
        return i + 1;
      }
    }
    throw fail();
  }
}
