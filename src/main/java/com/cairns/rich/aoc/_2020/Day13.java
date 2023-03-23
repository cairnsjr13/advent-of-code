package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Day13 extends Base2020 {
  @Override
  protected Object part1(Loader loader) {
    List<String> input = loader.ml();
    double earliest = Double.parseDouble(input.get(0));
    String[] busses = input.get(1).split(",");

    double min = Double.MAX_VALUE;
    int minBus = Integer.MAX_VALUE;

    List<Integer> busIds = Arrays.stream(busses)
        .filter((i) -> !"x".equals(i)).map(Integer::parseInt)
        .collect(Collectors.toList());
    for (int bus : busIds) {
      double pickup = Math.ceil(earliest / bus) * bus;
      if (pickup < min) {
        min = pickup;
        minBus = bus;
      }
    }
    return (int) (minBus * (min - earliest));
  }

  @Override
  protected Object part2(Loader loader) {
    String[] consts = loader.ml().get(1).split(",");
    List<Equation> eqs = new ArrayList<>();
    for (int i = 0; i < consts.length; ++i) {
      if (!"x".equals(consts[i])) {
        int mod = Integer.parseInt(consts[i]);
        eqs.add(new Equation(i, mod));
      }
    }
    long t = 0;
    long step = eqs.get(0).mod;
    for (int i = 1; i < eqs.size(); ++i) {
      Equation e = eqs.get(i);
      while (true) {
        t += step;
        if ((t + e.congruentTo) % e.mod == 0) {
          step *= e.mod;
          break;
        }
      }
    }
    return t;
  }

  private static class Equation {
    private final long congruentTo;
    private final long mod;

    private Equation(long congruentTO, long mod) {
      this.congruentTo = congruentTO;
      this.mod = mod;
    }
  }
}
