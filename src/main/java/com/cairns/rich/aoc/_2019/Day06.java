package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Loader2;
import java.util.HashMap;
import java.util.Map;

class Day06 extends Base2019 {
  @Override
  protected void run() {
    Map<String, String> orbits = parseOrbits(fullLoader);
    System.out.println(getTotalNumOrbits(orbits));
    System.out.println(computeMinOrbitJumps(orbits));
  }
  
  private int getTotalNumOrbits(Map<String, String> orbits) {
    Map<String, Integer> countCache = new HashMap<>();
    countCache.put("COM", 0);
    orbits.keySet().forEach((planet) -> compNumOrbits(orbits, planet, countCache));
    return countCache.values().stream().mapToInt(Integer::intValue).sum();
  }
  
  private int compNumOrbits(Map<String, String> orbits, String from, Map<String, Integer> countCache) {
    if (!countCache.containsKey(from)) {
      countCache.put(from, 1 + compNumOrbits(orbits, orbits.get(from), countCache));
    }
    return countCache.get(from);
  }
  
  private int computeMinOrbitJumps(Map<String, String> orbits) {
    Map<String, Integer> youChain = new HashMap<>();
    int youHops = 0;
    for (String youCurr = "YOU"; youCurr != null; youCurr = orbits.get(youCurr), ++youHops) {
      youChain.put(youCurr, youHops);
    }
    int sanHops = 0;
    for (String sanCurr = "SAN"; true; sanCurr = orbits.get(sanCurr), ++sanHops) {
      if (youChain.containsKey(sanCurr)) {
        return sanHops + youChain.get(sanCurr)  - 2;
      }
    }
  }
  
  private Map<String, String> parseOrbits(Loader2 loader) {
    Map<String, String> orbits = new HashMap<>();
    loader.ml().stream().map((spec) -> spec.split("\\)")).forEach((orbit) -> orbits.put(orbit[1], orbit[0]));
    return orbits;
  }
}
