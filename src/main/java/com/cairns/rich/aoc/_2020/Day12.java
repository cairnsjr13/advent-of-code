package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.MutablePoint;
import java.util.List;

class Day12 extends Base2020 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Inst> insts = loader.ml(Inst::new);
    MutablePoint ship = MutablePoint.origin();
    MutablePoint delta = new MutablePoint(1, 0);
    return getManDistance(insts, ship, ship, delta);
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Inst> insts = loader.ml(Inst::new);
    MutablePoint ship = MutablePoint.origin();
    MutablePoint way = new MutablePoint(10, 1);
    return getManDistance(insts, way, ship, way);
  }

  private int getManDistance(
      List<Inst> insts,
      MutablePoint cardinal,
      MutablePoint forward,
      MutablePoint turn
  ) {
    for (Inst inst : insts) {
      if (inst.dir == 'N') {
        cardinal.mutateY(inst.mag);
      }
      else if (inst.dir == 'S') {
        cardinal.mutateY(-inst.mag);
      }
      else if (inst.dir == 'E') {
        cardinal.mutateX(inst.mag);
      }
      else if (inst.dir == 'W') {
        cardinal.mutateX(-inst.mag);
      }
      else if (inst.dir == 'F') {
        forward.mutateX(turn.x() * inst.mag);
        forward.mutateY(turn.y() * inst.mag);
      }
      else if (inst.mag == 180) {
        turn.x(-turn.x());
        turn.y(-turn.y());
      }
      else if (   ((inst.dir == 'L') && (inst.mag == 90))
               || ((inst.dir == 'R') && (inst.mag == 270)))
      {
        int previousX = turn.x();
        turn.x(-turn.y());
        turn.y(previousX);
      }
      else {
        int previousX = turn.x();
        turn.x(turn.y());
        turn.y(-previousX);
      }
    }
    return Math.abs(forward.x()) + Math.abs(forward.y());
  }

  private static class Inst {
    private final char dir;
    private final int mag;

    private Inst(String spec) {
      this.dir = spec.charAt(0);
      this.mag = Integer.parseInt(spec.substring(1));
    }
  }
}
