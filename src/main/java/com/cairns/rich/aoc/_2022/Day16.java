package com.cairns.rich.aoc._2022;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 extends Base2022 {
  @Override
  protected void run() throws Throwable {
    Map<String, Valve> lookup = testLoader.ml(Valve::new).stream().collect(Collectors.toMap((v) -> v.id, Function.identity()));
    initMinDists(lookup);
    Set<String> initOpen = lookup.keySet().stream().filter((v) -> lookup.get(v).rate == 0).collect(Collectors.toSet());
    long mark = System.currentTimeMillis();
    long answer = maxReleased(lookup, 30, "AA", initOpen, 0);
    System.out.println((System.currentTimeMillis() - mark) + "ms\t- ANSWER: " + answer);
    
    mark = System.currentTimeMillis();
    answer = maxReleased(lookup, 30, initPq(1), initOpen, new HashSet<>(), 0);
    System.out.println((System.currentTimeMillis() - mark) + "ms\t- ANSWER: " + answer);
    
    
    mark = System.currentTimeMillis();
    answer = maxReleased(lookup, 26, initPq(2), initOpen, new HashSet<>(), 0);
    System.out.println((System.currentTimeMillis() - mark) + "ms\t- ANSWER: " + answer);
  }
  
  private PriorityQueue<InMotion> initPq(int numPlayers) {
    return IntStream.range(0, numPlayers)
        .mapToObj((i) -> new InMotion("AA", 0))
        .collect(PriorityQueue::new, PriorityQueue::add, PriorityQueue::addAll);
  }
  
  private long maxReleased(
      Map<String, Valve> lookup,
      long timeLeft,
      PriorityQueue<InMotion> inMotions,
      Set<String> opens,
      Set<String> inTransits,
      long rate
  ) {
    if (timeLeft < 0) {
      throw fail();
    }
    if (timeLeft == 0) {
      return 0;
    }
    long result = timeLeft * rate;
    if (!inMotions.isEmpty()) {
      InMotion current = inMotions.poll();
      long pressureWhileTravelling = rate * current.timeLeft;
      rate += lookup.get(current.towards).rate;
      timeLeft -= current.timeLeft;
      inMotions.forEach((im) -> im.timeLeft -= current.timeLeft);
      opens.add(current.towards);
      result = Math.max(result, pressureWhileTravelling + maxReleased(lookup, timeLeft, inMotions, opens, inTransits, rate));
      for (String next : lookup.keySet()) {
        if (!opens.contains(next) && !inTransits.contains(next)) {
          if (next.equals(current.towards)) {
            throw fail(next);
          }
          long timeForMoveAndOpen = 1 + lookup.get(current.towards).minDists.get(next);
          if (timeForMoveAndOpen <= timeLeft) {
            InMotion newMotion = new InMotion(next, timeForMoveAndOpen);
            inMotions.offer(newMotion);
            inTransits.add(next);
            result = Math.max(result, pressureWhileTravelling + maxReleased(lookup, timeLeft, inMotions, opens, inTransits, rate));
            inTransits.remove(next);
            inMotions.remove(newMotion);
          }
        }
      }
      if (!"AA".equals(current.towards)) {
        opens.remove(current.towards);
      }
      inMotions.forEach((im) -> im.timeLeft += current.timeLeft);
      timeLeft += current.timeLeft;
      rate -= lookup.get(current.towards).rate;
      inMotions.offer(current);
    }
    return result;
  }
  
  private long maxReleased(
      Map<String, Valve> lookup,
      long timeLeft,
      String location,
      Set<String> opens,
      long rate
  ) {
    if (timeLeft < 0) {
      throw fail();
    }
    if (timeLeft == 0) {
      return 0;
    }
    long result = timeLeft * rate;
    for (String next : lookup.keySet()) {
      if (!opens.contains(next)) {
        long timeForMoveAndOpen = lookup.get(location).minDists.get(next) + 1;
        if (timeForMoveAndOpen <= timeLeft) {
          opens.add(next);
          long totalFromThisStep = (timeForMoveAndOpen * rate) + maxReleased(
              lookup,
              timeLeft - timeForMoveAndOpen,
              next,
              opens,
              rate + lookup.get(next).rate
          );
          result = Math.max(result, totalFromThisStep);
          opens.remove(next);
        }
      }
    }
    return result;
  }
  
  private void initMinDists(Map<String, Valve> lookup) {
    for (Valve start : lookup.values()) {
      if ((start.rate > 0) || "AA".equals(start.id)) {
        for (Valve end : lookup.values()) {
          if ((start != end) && (end.rate > 0) && !start.minDists.containsKey(end.id)) {
            long minDist = bfs(start.id, end.id::equals, SearchState::getNumSteps, (current, registrar) -> {
              lookup.get(current).paths.forEach(registrar::accept);
            }).get().getNumSteps();
            start.minDists.put(end.id, minDist);
            end.minDists.put(start.id, minDist);
          }
        }
      }
    }
  }
  
  private static class InMotion implements Comparable<InMotion> {
    private final String towards;
    private long timeLeft;
    
    private InMotion(String towards, long timeLeft) {
      this.towards = towards;
      this.timeLeft = timeLeft;
    }
    
    @Override
    public int compareTo(InMotion other) {
      return Long.compare(timeLeft, other.timeLeft);
    }
    
    @Override
      public String toString() {
        return towards + timeLeft;
      }
  }
  
  private static class Valve {
    private static final Pattern pattern = Pattern.compile("^Valve (..) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$");
    
    private final String id;
    private final int rate;
    private final Set<String> paths;
    private final Map<String, Long> minDists = new HashMap<>();
    
    private Valve(String line) {
      Matcher matcher = matcher(pattern, line);
      this.id = matcher.group(1);
      this.rate = num(matcher, 2);
      this.paths = Arrays.stream(matcher.group(3).split(", ")).collect(Collectors.toSet());
    }
  }
}