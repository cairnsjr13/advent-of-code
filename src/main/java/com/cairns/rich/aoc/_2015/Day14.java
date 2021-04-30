package com.cairns.rich.aoc._2015;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day14 extends Base2015 {
  @Override
  protected void run() {
    List<Reindeer> reindeers = fullLoader.ml(Reindeer::new);
    System.out.println(getDistanceTravelledByWinner(reindeers, 2503));
    System.out.println(getMaxPoints(reindeers, 2503));
  }
  
  private int getDistanceTravelledByWinner(List<Reindeer> reindeers, int totalSeconds) {
    int maxDistance = 0;
    for (Reindeer reindeer : reindeers) {
      int timeBlock = reindeer.stamina + reindeer.rest;
      int wholeTimeBlocks = totalSeconds / timeBlock;
      int secondsFlying = wholeTimeBlocks * reindeer.stamina
                        + Math.min(reindeer.stamina, totalSeconds - (wholeTimeBlocks * timeBlock));
      int distance = secondsFlying * reindeer.speed;
      maxDistance = Math.max(maxDistance, distance);
    }
    return maxDistance;
  }
  
  private int getMaxPoints(List<Reindeer> reindeers, int totalSeconds) {
    List<State> states = reindeers.stream().map(State::new).collect(Collectors.toList());
    for (int second = 0; second < totalSeconds; ++second) {
      states.forEach(State::tick);
      findLeader(states).forEach((state) -> ++state.points);
    }
    return states.stream().mapToInt((s) -> s.points).max().getAsInt();
  }
  
  private Set<State> findLeader(List<State> states) {
    int leadingDistance = 0;
    Set<State> leaders = new HashSet<>();
    for (State state : states) {
      if (leadingDistance == state.distance) {
        leaders.add(state);
      }
      else if (leadingDistance < state.distance) {
        leadingDistance = state.distance;
        leaders.clear();
        leaders.add(state);
      }
    }
    return leaders;
  }
  
  private static class State {
    private final Reindeer reindeer;
    private int points = 0;
    private int distance = 0;
    private boolean flying = true;
    private int secondsLeft;
    
    private State(Reindeer reindeer) {
      this.reindeer = reindeer;
      this.secondsLeft = reindeer.stamina;
    }
    
    private void tick() {
      if (flying) {
        distance += reindeer.speed;
      }
      --secondsLeft;
      if (secondsLeft == 0) {
        secondsLeft = (flying) ? reindeer.rest : reindeer.stamina;
        flying = !flying;
      }
    }
  }
  
  private static class Reindeer {
    private static final Pattern pattern = Pattern.compile("^[^\\d]+[^\\d]*(\\d+)[^\\d]*(\\d+)[^\\d]*(\\d+).*$");
    
    private final int speed;
    private final int stamina;
    private final int rest;
    
    private Reindeer(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.speed = Integer.parseInt(matcher.group(1));
      this.stamina = Integer.parseInt(matcher.group(2));
      this.rest = Integer.parseInt(matcher.group(3));
    }
  }
}
