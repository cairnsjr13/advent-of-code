package com.cairns.rich.aoc._2021;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cairns.rich.aoc.grid.MutablePoint;

public class Day02 extends Base2021 {
  @Override
  protected void run() throws Throwable {
	List<MoveAndMagnitude> insts = fullLoader.ml(MoveAndMagnitude::new);
	System.out.println(part1(insts));
	System.out.println(part2(insts));
  }
  
  private int part1(List<MoveAndMagnitude> insts) {
    int horizontalMovement = insts.stream().filter((mm) -> "forward".equals(mm.move)).mapToInt((mm) -> mm.magnitude).sum();
    int depthChange = insts.stream()
        .filter((mm) -> !"forward".equals(mm.move))
        .mapToInt((mm) -> mm.magnitude * (("up".equals(mm.move)) ? -1 : 1))
        .sum();
    return horizontalMovement * depthChange;
  }
  
  private int part2(List<MoveAndMagnitude> insts) {
    MutablePoint location = new MutablePoint(0, 0);
    int aim = 0;
    for (MoveAndMagnitude inst : insts) {
      if ("forward".equals(inst.move)) {
        location.mutateX(inst.magnitude);
        location.mutateY(aim * inst.magnitude);
      }
      else {
        aim += inst.magnitude * (("up".equals(inst.move)) ? -1 : 1);
      }
    }
    return location.x() * location.y();
  }
  
  private static class MoveAndMagnitude {
    private static final Pattern pattern = Pattern.compile("^(forward|up|down) (\\d+)$");
    
    private final String move;
    private final int magnitude;
    
    private MoveAndMagnitude(String line) {
      Matcher matcher = matcher(pattern, line);
      this.move = matcher.group(1);
      this.magnitude = num(matcher, 2);
    }
  }
}
