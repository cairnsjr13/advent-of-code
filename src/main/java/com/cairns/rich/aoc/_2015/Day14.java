package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Reindeer olympics with bursty reindeer flying!  Let's figure out who wins based on the different scoring styles.
 */
class Day14 extends Base2015 {
  private static final ConfigToken<Integer> raceLength = ConfigToken.of("raceLength", Integer::parseInt);

  /**
   * Computes the distance traveled by the winning reindeer after the configured number of seconds.
   * We can compute how far a reindeer travels by computing the number of "time block"s they will (at least partially)
   * go through during the race.  A time block consists of flying and resting.  We can compute the amount of time flying
   * (and thus distance traveled) by looking at whole time blocks and any time spent flying in the last partial time block.
   */
  @Override
  protected Object part1(Loader loader) {
    List<Reindeer> reindeers = loader.ml(Reindeer::new);
    int totalSeconds = loader.getConfig(raceLength);
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

  /**
   * Computes the maximum number of points earned by any reindeer during the configured number of seconds.
   * A reindeer earns a point by being in the lead at the end of each second (ties allowed).
   * We can compute which reindeer is winning (and thus gets a point) by ticking each reindeer each second
   * and then inspecting who is in the lead.  A tick comprises of moving the reindeer forward (if flying),
   * or reducing the rest time left (if resting).
   */
  @Override
  protected Object part2(Loader loader) {
    List<Reindeer> reindeers = loader.ml(Reindeer::new);
    int totalSeconds = loader.getConfig(raceLength);
    for (int second = 0; second < totalSeconds; ++second) {
      reindeers.forEach(Reindeer::tick);
      findLeader(reindeers).forEach((reindeer) -> ++reindeer.points);
    }
    return reindeers.stream().mapToInt((s) -> s.points).max().getAsInt();
  }

  /**
   * Finds the reindeers who are currently in the lead in the race.  Any reindeer with the max distance are considered leading.
   */
  private Set<Reindeer> findLeader(List<Reindeer> reindeers) {
    int maxDistance = getMax(reindeers, (r) -> r.distance).distance;
    return reindeers.stream().filter((r) -> r.distance == maxDistance).collect(Collectors.toSet());
  }


  /**
   * Input class that describes how fast a reindeer is, how long it can go without resting, and how long it must rest.
   */
  private static class Reindeer {
    private static final Pattern pattern = Pattern.compile("^[^\\d]+[^\\d]*(\\d+)[^\\d]*(\\d+)[^\\d]*(\\d+).*$");

    private final int speed;
    private final int stamina;
    private final int rest;

    private int points = 0;
    private int distance = 0;
    private boolean flying = true;
    private int secondsLeft;

    private Reindeer(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.speed = Integer.parseInt(matcher.group(1));
      this.stamina = Integer.parseInt(matcher.group(2));
      this.rest = Integer.parseInt(matcher.group(3));

      this.secondsLeft = stamina;
    }

    /**
     * Updates this reindeer's status based on its current flying/resting state.
     *   - Flying will result in distance increasing by speed
     *   - A second is removed from time left in current status
     *   - Becoming fully rested will result in status changing and time reset
     */
    private void tick() {
      if (flying) {
        distance += speed;
      }
      --secondsLeft;
      if (secondsLeft == 0) {
        secondsLeft = (flying) ? rest : stamina;
        flying = !flying;
      }
    }
  }
}
