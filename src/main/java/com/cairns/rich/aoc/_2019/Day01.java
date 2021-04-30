package com.cairns.rich.aoc._2019;

import java.util.List;

class Day01 extends Base2019 {
  @Override
  protected void run() {
    List<Integer> masses = fullLoader.ml(Integer::parseInt);
    System.out.println(getFuelReq(masses, false));
    System.out.println(getFuelReq(masses, true));
  }
  
  private int getFuelReq(List<Integer> masses, boolean recursive) {
    return masses.stream().mapToInt((m) -> fuelReq(m, recursive)).sum();
  }
  
  private int fuelReq(int mass, boolean recursive) {
    int totalFuel = 0;
    do {
      mass = (mass / 3) - 2;
      if (mass <= 0) {
        break;
      }
      totalFuel += mass;
    } while (recursive);
    return totalFuel;
  }
}
