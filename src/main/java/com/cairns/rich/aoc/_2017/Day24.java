package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.Set;

/**
 * We need to build a bridge to the cpu.  We have a bunch of components with different size pin
 * connectors that we can piece together to get across.  We need to find the strongest and longest!
 */
class Day24 extends Base2017 {
  /**
   * Finds the strength of the strongest bridge that we can construct.
   */
  @Override
  protected Object part1(Loader loader) {
    return findBestStrength(loader, BestBridge::isWeaker);
  }

  /**
   * Finds the strength of the longest bridge that we can construct (breaking ties by strength).
   */
  @Override
  protected Object part2(Loader loader) {
    return findBestStrength(loader, BestBridge::isShorterOrSameLengthAndWeaker);
  }

  /**
   * Returns the strength of the best bridge defined by the given isBetter check.
   */
  private int findBestStrength(Loader loader, NeedsUpdateCheck isBetter) {
    Multimap<Integer, Component> lookupByPin = Component.getLookupByPin(loader);
    BestBridge best = new BestBridge();
    findBest(isBetter, best, lookupByPin, new HashSet<>(), 0, 0);
    return best.strength;
  }

  /**
   * Finds the best bridge possible using the given "better" definition.
   * A bridge can be constructed by starting with a 0 sized pin connection and
   * chaining the components together based on the required pin size using either
   * side of the remaining components until we can reach no further (out of options).
   */
  private void findBest(
      NeedsUpdateCheck needsUpdateCheck,
      BestBridge best,
      Multimap<Integer, Component> lookupByPin,
      Set<Component> used,
      int currentStrength,
      int needPinSize
  ) {
    if (needsUpdateCheck.isBetterThanBest(best, used.size(), currentStrength)) {
      best.length = used.size();
      best.strength = currentStrength;
    }
    for (Component option : lookupByPin.get(needPinSize)) {
      if (!used.contains(option)) {
        used.add(option);
        int newStrength = currentStrength + option.first + option.second;
        int newNeedPinSize = (option.first != needPinSize) ? option.first : option.second;
        findBest(needsUpdateCheck, best, lookupByPin, used, newStrength, newNeedPinSize);
        used.remove(option);
      }
    }
  }

  /**
   * Container object describing the "best" bridge that we have seen so far.
   */
  private static class BestBridge {
    private int length;
    private int strength;

    /**
     * Returns true if this bridge is weaker than the proposed bridge regardless of length.
     */
    private boolean isWeaker(int length, int strength) {
      return this.strength < strength;
    }

    /**
     * Returns true if this bridge is shorter or the same size but weaker than the proposed bridge.
     */
    private boolean isShorterOrSameLengthAndWeaker(int length, int strength) {
      return (this.length < length)
          || ((this.length == length) && (this.strength < strength));
    }
  }

  /**
   * Functional interface that checks if the given length/strength are "better" than the given current best.
   */
  private interface NeedsUpdateCheck {
    /**
     * Returns true if the new bridge is "better" than the current best and it needs to be updated.
     */
    boolean isBetterThanBest(BestBridge than, int length, int strength);
  }

  /**
   * Descriptor object that contains pin size information for a connector.
   */
  private static class Component {
    private final int first;
    private final int second;

    private Component(String spec) {
      String[] parts = spec.split("/");
      this.first = Integer.parseInt(parts[0]);
      this.second = Integer.parseInt(parts[1]);
    }

    /**
     * Parsing method that creates a lookup that maps connector pin size to the component it correlates to.
     * We use a {@link Multimap} because each size can correspond to numerous components.
     */
    private static Multimap<Integer, Component> getLookupByPin(Loader loader) {
      Multimap<Integer, Component> lookupByPin = HashMultimap.create();
      loader.ml(Component::new).forEach((component) -> {
        lookupByPin.put(component.first, component);
        lookupByPin.put(component.second, component);
      });
      return lookupByPin;
    }
  }
}
