package com.cairns.rich.aoc._2019;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day12 extends Base2019 {
  @Override
  protected void run() {
    List<Moon> moons = fullLoader.ml(Moon::new);
    System.out.println(findTotalEnergyAfter1000(moons));
    findRepetition(fullLoader.ml(Moon::new));
  }
  
  private int findTotalEnergyAfter1000(List<Moon> moons) {
    for (int t = 0; t < 1000; ++t) {
      tick(moons);
    }
    return moons.stream().mapToInt(Moon::calculateTotalEnergy).sum();
  }
  
  private void findRepetition(List<Moon> moons) {
    List<ToIntFunction<Xyz>> toCoords = List.of((xyz) -> xyz.x, (xyz) -> xyz.y, (xyz) -> xyz.z);
    Map<ToIntFunction<Xyz>, Set<List<Integer>>> toCoordToStatesSeen = toCoords.stream().collect(Collectors.toMap(
        Function.identity(),
        (i) -> new HashSet<>()
    ));
    Map<ToIntFunction<Xyz>, Integer> repetitionAt = new HashMap<>();
    for (int t = 0; repetitionAt.size() < 3; ++t) {
      tick(moons);
      for (ToIntFunction<Xyz> toCoord : toCoords) {
        if (!repetitionAt.containsKey(toCoord)) {
          if (!toCoordToStatesSeen.get(toCoord).add(toState(moons, toCoord))) {
            repetitionAt.put(toCoord, t);
          }
        }
      }
    }
    System.out.println("LCM" + repetitionAt.values());
  }
  
  private void tick(List<Moon> moons) {
    for (Moon first : moons) {
      for (Moon second : moons) {
        first.velocity.x += velocityChange(first, second, (xyz) -> xyz.x);
        first.velocity.y += velocityChange(first, second, (xyz) -> xyz.y);
        first.velocity.z += velocityChange(first, second, (xyz) -> xyz.z);
      }
    }
    moons.forEach(Moon::applyVelocity);
  }
  
  private int velocityChange(Moon first, Moon second, ToIntFunction<Xyz> toCoord) {
    return (int) Math.signum(Integer.compare(toCoord.applyAsInt(second.position), toCoord.applyAsInt(first.position)));
  }
  
  private List<Integer> toState(List<Moon> moons, ToIntFunction<Xyz> toCoord) {
    List<Integer> coordValues = new ArrayList<>();
    moons.forEach((moon) -> coordValues.add(toCoord.applyAsInt(moon.position)));
    moons.forEach((moon) -> coordValues.add(toCoord.applyAsInt(moon.velocity)));
    return coordValues;
  }
  
  private static final class Moon {
    private static final Pattern pattern = Pattern.compile("^<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>$");
    
    private final Xyz position;
    private final Xyz velocity = new Xyz(0, 0, 0);
    
    private Moon(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.position = new Xyz(num(matcher, 1), num(matcher, 2), num(matcher, 3));
    }
    
    private void applyVelocity() {
      position.add(velocity);
    }
    
    private int calculateTotalEnergy() {
      return position.absSum() * velocity.absSum();
    }
  }
  
  private static final class Xyz {
    private int x;
    private int y;
    private int z;
    
    private Xyz(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    
    private void add(Xyz delta) {
      x += delta.x;
      y += delta.y;
      z += delta.z;
    }
    
    private int absSum() {
      return IntStream.of(x, y, z).map(Math::abs).sum();
    }
  }
}
