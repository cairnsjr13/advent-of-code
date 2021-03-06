package com.cairns.rich.aoc._2017;

import java.util.List;
import java.util.Stack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

class Day24 extends Base2017 {
  @Override
  protected void run() {
    List<Component> components = fullLoader.ml(Component::new);
    Multimap<Integer, Component> lookupByPin = HashMultimap.create();
    components.forEach((component) -> {
      lookupByPin.put(component.first, component);
      lookupByPin.put(component.second, component);
    });
    BestBridge best = new BestBridge();
    int strongest = maxFrom(best, lookupByPin, new Stack<>(), 0, 0);
    System.out.println(strongest);
    System.out.println(best.strength);
  }
  
  private int maxFrom(
      BestBridge best,
      Multimap<Integer, Component> lookupByPin,
      Stack<Component> used,
      int currentStrength,
      int need
  ) {
    if (used.size() == best.length) {
      best.strength = Math.max(best.strength, used.size());
    }
    else if (used.size() > best.length) {
      best.length = used.size();
      best.strength = currentStrength;
    }
    int maxFrom = currentStrength;
    for (Component option : lookupByPin.get(need)) {
      if (!used.contains(option)) {
        used.push(option);
        maxFrom = Math.max(
            maxFrom,
            maxFrom(best, lookupByPin, used, currentStrength + option.strength(), (option.first != need) ? option.first : option.second)
        );
        used.pop();
      }
    }
    return maxFrom;
  }
  
  private static class BestBridge {
    private int length;
    private int strength;
  }
  
  private static class Component {
    private final int first;
    private final int second;
    
    private Component(String spec) {
      String[] parts = spec.split("/");
      this.first = Integer.parseInt(parts[0]);
      this.second = Integer.parseInt(parts[1]);
    }
    
    private int strength() {
      return first + second;
    }
  }
}
