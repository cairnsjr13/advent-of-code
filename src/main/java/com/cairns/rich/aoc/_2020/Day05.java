package com.cairns.rich.aoc._2020;

import java.util.List;
import java.util.stream.Collectors;

class Day05 extends Base2020 {
  @Override
  protected void run() {
    List<Seat> seats = fullLoader.ml(Seat::new);
    System.out.println(findHighestSeatId(seats));
    System.out.println(getMissingId(seats));
  }
  
  private int findHighestSeatId(List<Seat> seats) {
    return seats.stream().mapToInt(Seat::getId).max().getAsInt();
  }
  
  private int getMissingId(List<Seat> seats) {
    List<Integer> ids = seats.stream().mapToInt(Seat::getId).sorted().boxed().collect(Collectors.toList());
    for (int i = 0; i < ids.size() - 1; ++i) {
      if (ids.get(i) + 1 != ids.get(i + 1)) {
        return ids.get(i) + 1;
      }
    }
    throw fail();
  }
  
  private static class Seat {
    private int row;
    private int col;
    
    private Seat(String spec) {
      this.row = pos(spec, 'B', 0, 6) + pos(spec, 'B', 1, 5) + pos(spec, 'B', 2, 4) + pos(spec, 'B', 3, 3)
               + pos(spec, 'B', 4, 2) + pos(spec, 'B', 5, 1) + pos(spec, 'B', 6, 0);
      this.col = pos(spec, 'R', 7, 2) + pos(spec, 'R', 8, 1) + pos(spec, 'R', 9, 0);
    }
    
    private int pos(String spec, char one, int position, int shift) {
      return (spec.charAt(position) == one) ? (1 << shift) : 0;
    }
    
    private int getId() {
      return row * 8 + col;
    }
  }
}
