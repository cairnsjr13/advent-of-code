package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;

class Day01 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    return getFuelReq(loader, false);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getFuelReq(loader, true);
  }

  private int getFuelReq(Loader2 loader, boolean recursive) {
    return loader.ml(Integer::parseInt).stream().mapToInt((m) -> fuelReq(m, recursive)).sum();
  }

  private int fuelReq(int mass, boolean recursive) {
    int totalFuel = 0;
    do {
      mass = (mass / 3) - 2;
      if (mass <= 0) {
        break;
      }
      totalFuel += mass;
    }
    while (recursive);
    return totalFuel;
  }
}
