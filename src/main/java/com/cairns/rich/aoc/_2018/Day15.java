package com.cairns.rich.aoc._2018;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.RelDir;

class Day15 extends Base2018 {
  @Override
  protected void run() {
    char[][] map = loadMap(fullLoader.ml());
    System.out.println(computeOutcome(getResult(map, 3)));
    System.out.println(getOutcomeOfWeakestNoDeathElfVictory(map));
  }
  
  private int getOutcomeOfWeakestNoDeathElfVictory(char[][] map) {
    for (int elfStrength = 4; true; ++elfStrength) {
      Result result = getResult(map, elfStrength);
      if (result.units.stream().filter((u) -> u.marker == 'E').allMatch((e) -> e.hp > 0)) {
        return computeOutcome(result);
      }
    }
  }
  
  private Result getResult(char[][] map, int elfStrength) {
    map = copyMap(map);
    List<Unit> units = findUnits(map, elfStrength);
    for (int turn = 0; true; ++turn) {
      units.sort(Unit.locationCmp);
      for (Unit attacker : units) {
        if (attacker.hp > 0) {
          List<Unit> targets = getTargets(units, attacker);
          if (targets.isEmpty()) {
            return new Result(units, turn);
          }
          List<Unit> inRange = getInRange(targets, attacker);
          if (inRange.isEmpty()) {
            RelDir moveDir = getMoveDir(map, attacker, targets);
            if (moveDir == null) {
              continue;
            }
            map[attacker.location.y()][attacker.location.x()] = '.';
            attacker.location.move(moveDir);
            map[attacker.location.y()][attacker.location.x()] = attacker.marker;
            inRange = getInRange(targets, attacker);
          }
          attack(map, attacker, inRange);
        }
      }
    }
  }
  
  private int computeOutcome(Result result) {
    return result.turns * result.units.stream().mapToInt((u) -> Math.max(0, u.hp)).sum();
  }
  
  private List<Unit> getTargets(List<Unit> units, Unit attacker) {
    return units.stream().filter((u) -> (u.isElf() != attacker.isElf()) && (u.hp > 0)).collect(Collectors.toList());
  }
  
  private List<Unit> getInRange(List<Unit> targets, Unit of) {
    return targets.stream()
        .filter((t) -> 1 == Math.abs(t.location.x() - of.location.x()) + Math.abs((t.location.y() - of.location.y())))
        .collect(Collectors.toList());
  }
  
  private RelDir getMoveDir(char[][] map, Unit mover, List<Unit> targets) {
    Set<Point<?>> attackLocations = getAttackLocations(map, targets);
    Set<Point<?>> visited = new HashSet<>();
    visited.add(mover.location);
    Queue<MoveDesc> candidates = new ArrayDeque<>();
    candidates.add(new MoveDesc(null, null, new ImmutablePoint(mover.location), 0));
    List<MoveDesc> reachable = new ArrayList<>();
    while (!candidates.isEmpty() && (reachable.isEmpty() || (reachable.get(0).numSteps > candidates.peek().numSteps))) {
      MoveDesc candidate = candidates.poll();
      for (RelDir dir : MoveDesc.dirOrder) {
        if (canMove(map, candidate.location, dir)) {
          ImmutablePoint newLocation = candidate.location.move(dir);
          if (!visited.contains(newLocation)) {
            visited.add(newLocation);
            MoveDesc option = new MoveDesc(candidate.initDir, dir, newLocation, candidate.numSteps + 1);
            if (attackLocations.contains(newLocation)) {
              reachable.add(option);
            }
            else {
              candidates.offer(option);
            }
          }
        }
      }
    }
    reachable.sort(MoveDesc.cmp);
    return (!reachable.isEmpty()) ? reachable.get(0).initDir : null;
  }
  
  private boolean canMove(char[][] map, Point<?> location, RelDir dir) {
    int newX = location.x() + dir.dx();
    int newY = location.y() + dir.dy();
    return map[newY][newX] == '.';
  }
  
  private Set<Point<?>> getAttackLocations(char[][] map, List<Unit> targets) {
    Set<Point<?>> attackLocations = new HashSet<>();
    for (Unit target : targets) {
      for (RelDir dir : EnumUtils.enumValues(RelDir.class)) {
        int inspectX = target.location.x() + dir.dx();
        int inspectY = target.location.y() + dir.dy();
        if ((0 <= inspectX) && (inspectX < map[0].length) && (0 <= inspectY) && (inspectY < map.length)) {
          if (map[inspectY][inspectX] == '.') {
            attackLocations.add(new ImmutablePoint(inspectX, inspectY));
          }
        }
      }
    }
    return attackLocations;
  }
  
  private void attack(char[][] map, Unit attacker, List<Unit> inRange) {
    if (!inRange.isEmpty()) {
      inRange.sort(Unit.attackCmp);
      Unit attacked = inRange.get(0);
      attacked.hp -= attacker.strength;
      if (attacked.hp <= 0) {
        map[attacked.location.y()][attacked.location.x()] = '.';
      }
    }
  }
  
  private char[][] loadMap(List<String> lines) {
    char[][] map = new char[lines.size()][lines.get(0).length()];
    for (int y = 0; y < lines.size(); ++y) {
      for (int x = 0; x < lines.get(0).length(); ++x) {
        map[lines.size() - y - 1][x] = lines.get(y).charAt(x);
      }
    }
    return map;
  }
  
  private char[][] copyMap(char[][] orig) {
    char[][] copy = new char[orig.length][];
    for (int i = 0; i < orig.length; ++i) {
      copy[i] = Arrays.copyOf(orig[i], orig[i].length);
    }
    return copy;
  }
  
  private List<Unit> findUnits(char[][] map, int elfStrength) {
    List<Unit> units = new ArrayList<>();
    for (int y = 0; y < map.length; ++y) {
      for (int x = 0; x < map[0].length; ++x) {
        if ((map[y][x] == 'E') || (map[y][x] == 'G')) {
          units.add(new Unit(map[y][x], x, y, elfStrength));
        }
      }
    }
    return units;
  }
  
  private static class Unit {
    private static final Comparator<Unit> locationCmp =
        Comparator.<Unit, Integer>comparing((u) -> -u.location.y()).thenComparing((u) -> u.location.x());
    private static final Comparator<Unit> attackCmp =
        Comparator.<Unit, Integer>comparing((u) -> u.hp).thenComparing(locationCmp);
    
    private final char marker;
    private final MutablePoint location;
    private final int strength;
    private int hp = 200;
    
    private Unit(char marker, int x, int y, int elfStrength) {
      this.marker = marker;
      this.location = new MutablePoint(x, y);
      this.strength = (marker == 'E') ? elfStrength : 3;
    }
    
    private boolean isElf() {
      return marker == 'E';
    }
    
    @Override
      public String toString() {
        return "{" + marker + " " + location + " " + hp + "}";
      }
  }
  
  private static class MoveDesc {
    private static final List<RelDir> dirOrder = List.of(RelDir.Up, RelDir.Left, RelDir.Right, RelDir.Down);
    private static final Comparator<MoveDesc> cmp =
        Comparator.<MoveDesc, Integer>comparing((md) -> -md.location.y()).thenComparing((md) -> md.location.x()).thenComparing((md) -> dirOrder.indexOf(md.initDir));
    
    private final RelDir initDir;
    private final ImmutablePoint location;
    private final int numSteps;
    
    private MoveDesc(RelDir previousMoveDescDir, RelDir dir, ImmutablePoint location, int numSteps) {
      this.initDir = (previousMoveDescDir != null) ? previousMoveDescDir : dir;
      this.location = location;
      this.numSteps = numSteps;
    }
    
    @Override
    public String toString() {
      return "{" + initDir + " " + location + " " + numSteps + "}";
    }
  }
  
  private static class Result {
    private final List<Unit> units;
    private final int turns;
    
    private Result(List<Unit> units, int turns) {
      this.units = units;
      this.turns = turns;
    }
  }
}
