package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.List;

class Day05 extends Base2020 {
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(this::parseSeatId).stream().mapToInt(Integer::intValue).max().getAsInt();
  }

  @Override
  protected Object part2(Loader loader) {
    List<Integer> seatIds = loader.ml(this::parseSeatId);
    seatIds.sort(Integer::compare);
    for (int i = 0; i < seatIds.size(); ++i) {
      if (seatIds.get(i) + 1 != seatIds.get(i + 1)) {
        return seatIds.get(i) + 1;
      }
    }
    throw fail();
  }

  private int parseSeatId(String spec) {
    int row = pos(spec, 'B', 0, 6) + pos(spec, 'B', 1, 5) + pos(spec, 'B', 2, 4) + pos(spec, 'B', 3, 3)
            + pos(spec, 'B', 4, 2) + pos(spec, 'B', 5, 1) + pos(spec, 'B', 6, 0);
    int col = pos(spec, 'R', 7, 2) + pos(spec, 'R', 8, 1) + pos(spec, 'R', 9, 0);
    return row * 8 + col;
  }

  private int pos(String spec, char one, int position, int shift) {
    return (spec.charAt(position) == one) ? (1 << shift) : 0;
  }
}
