package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class Day06 extends Base2019 {
  @Override
  protected Object part1(Loader2 loader) {
    Map<String, String> orbits =
        loader.ml((s) -> s.split("\\)")).stream().collect(Collectors.toMap((o) -> o[1], (o) -> o[0]));
    Map<String, Integer> countCache = new HashMap<>();
    countCache.put("COM", 0);
    orbits.keySet().forEach((planet) -> compNumOrbits(orbits, planet, countCache));
    return countCache.values().stream().mapToInt(Integer::intValue).sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    Map<String, String> orbits =
        loader.ml((s) -> s.split("\\)")).stream().collect(Collectors.toMap((o) -> o[1], (o) -> o[0]));
    Map<String, Integer> youChain = new HashMap<>();
    int youHops = 0;
    for (String youCurr = "YOU"; youCurr != null; youCurr = orbits.get(youCurr), ++youHops) {
      youChain.put(youCurr, youHops);
    }
    int sanHops = 0;
    for (String sanCurr = "SAN"; true; sanCurr = orbits.get(sanCurr), ++sanHops) {
      if (youChain.containsKey(sanCurr)) {
        return sanHops + youChain.get(sanCurr) - 2;
      }
    }
  }

  private int compNumOrbits(Map<String, String> orbits, String from, Map<String, Integer> countCache) {
    if (!countCache.containsKey(from)) {
      countCache.put(from, 1 + compNumOrbits(orbits, orbits.get(from), countCache));
    }
    return countCache.get(from);
  }
}
