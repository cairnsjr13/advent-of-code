package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.Point;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.mutable.MutableInt;

class Day15 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    char[][] map = fullLoader.ml(String::toCharArray).stream().toArray(char[][]::new);
    return computeOutcome(getResult(map, 3));
  }

  @Override
  protected Object part2(Loader2 loader) {
    char[][] map = fullLoader.ml(String::toCharArray).stream().toArray(char[][]::new);
    for (int elfStrength = 4; true; ++elfStrength) {
      Result result = getResult(map, elfStrength);
      if (result.units.stream().filter(Unit::isElf).allMatch((e) -> e.hp > 0)) {
        return computeOutcome(result);
      }
    }
  }

  private Result getResult(char[][] map, int elfStrength) {
    map = Arrays.stream(map).map((r) -> Arrays.copyOf(r, r.length)).toArray(char[][]::new);
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
            ReadDir moveDir = getMoveDir(map, attacker, targets);
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

  // TODO: NOTE: this is slower than the original impl because its an exhaustive search without a quick hook terminal
  private ReadDir getMoveDir(char[][] map, Unit mover, List<Unit> targets) {
    Set<Point<?>> attackLocations = getAttackLocations(map, targets);
    MutableInt stepsToReachables = new MutableInt();
    List<MoveDesc> reachable = new ArrayList<>();
    bfs(
        new MoveDesc(null, null, new ImmutablePoint(mover.location)),
        (s, steps) -> !reachable.isEmpty() && (steps == stepsToReachables.intValue()),  // TODO: exhaustive search but terminate fast
        SearchState::getNumSteps,
        (current, steps, registrar) -> {
          for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
            if (canMove(map, current.location, dir)) {
              MoveDesc option = new MoveDesc(current.initDir, dir, current.location.move(dir));
              if (attackLocations.contains(option.location)) {
                reachable.add(option);
                stepsToReachables.setValue(steps + 1);
              }
              else {
                registrar.accept(option);
              }
            }
          }
        }
    );
    reachable.sort(MoveDesc.cmp);
    return (reachable.isEmpty()) ? null : reachable.get(0).initDir;
  }

  private boolean canMove(char[][] map, Point<?> location, ReadDir dir) {
    int newX = location.x() + dir.dx();
    int newY = location.y() + dir.dy();
    return map[newY][newX] == '.';
  }

  private Set<Point<?>> getAttackLocations(char[][] map, List<Unit> targets) {
    Set<Point<?>> attackLocations = new HashSet<>();
    for (Unit target : targets) {
      for (ReadDir dir : EnumUtils.enumValues(ReadDir.class)) {
        int inspectX = target.location.x() + dir.dx();
        int inspectY = target.location.y() + dir.dy();
        if (map[inspectY][inspectX] == '.') {
          attackLocations.add(new ImmutablePoint(inspectX, inspectY));
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
        Comparator.<Unit>comparingInt((u) -> u.location.y()).thenComparingInt((u) -> u.location.x());
    private static final Comparator<Unit> attackCmp = Comparator.<Unit>comparingInt((u) -> u.hp).thenComparing(locationCmp);

    private final char marker;
    private final MutablePoint location;
    private final int strength;
    private int hp = 200;

    private Unit(char marker, int x, int y, int elfStrength) {
      this.marker = marker;
      this.location = new MutablePoint(x, y);
      this.strength = (isElf()) ? elfStrength : 3;
    }

    private boolean isElf() {
      return marker == 'E';
    }
  }

  private static class MoveDesc {
    private static final List<ReadDir> dirOrder = List.of(ReadDir.Up, ReadDir.Left, ReadDir.Right, ReadDir.Down);
    private static final Comparator<MoveDesc> cmp = Comparator
        .<MoveDesc>comparingInt((md) -> md.location.y())
        .thenComparingInt((md) -> md.location.x())
        .thenComparingInt((md) -> dirOrder.indexOf(md.initDir));

    private final ReadDir initDir;
    private final ImmutablePoint location;

    private MoveDesc(ReadDir previousMoveDescDir, ReadDir dir, ImmutablePoint location) {
      this.initDir = (previousMoveDescDir != null) ? previousMoveDescDir : dir;
      this.location = location;
    }

    @Override
    public boolean equals(Object other) {
      return location.equals(((MoveDesc) other).location)
          && (initDir == ((MoveDesc) other).initDir);
    }

    @Override
    public int hashCode() {
      return location.hashCode();
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
