package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Stack;

// TODO: Part2 is nondeterministic
class Day24 extends Base2017 {
  @Override
  protected Object part1(Loader loader) {
    Multimap<Integer, Component> lookupByPin = getLookupByPin(loader);
    return maxFrom(new BestBridge(), lookupByPin, new Stack<>(), 0, 0);
  }

  @Override
  protected Object part2(Loader loader) {
    Multimap<Integer, Component> lookupByPin = getLookupByPin(loader);
    BestBridge best = new BestBridge();
    maxFrom(best, lookupByPin, new Stack<>(), 0, 0);
    return best.strength;
  }

  private Multimap<Integer, Component> getLookupByPin(Loader loader) {
    Multimap<Integer, Component> lookupByPin = HashMultimap.create();
    loader.ml(Component::new).forEach((component) -> {
      lookupByPin.put(component.first, component);
      lookupByPin.put(component.second, component);
    });
    return lookupByPin;
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
