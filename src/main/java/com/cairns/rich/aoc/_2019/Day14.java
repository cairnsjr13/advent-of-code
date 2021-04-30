package com.cairns.rich.aoc._2019;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableLong;

class Day14 extends Base2019 {
  @Override
  protected void run() {
    Map<String, Reaction> reactions = fullLoader.ml(Reaction::new).stream().collect(Collectors.toMap(
        (reaction) -> reaction.output.elem,
        Function.identity()
    ));
    MutableLong numOre = new MutableLong(0);
    Map<String, MutableLong> avail = new HashMap<>();
    System.out.println(getOreRequiredForNFuel(reactions, numOre, avail, 1));
    System.out.println(getFuelPossibleWithTrillionOre(reactions, numOre, avail, numOre.longValue()));
  }
  
  private long getFuelPossibleWithTrillionOre(
      Map<String, Reaction> reactions,
      MutableLong numOre,
      Map<String, MutableLong> avail,
      long oreFor1Fuel
  ) {
    long trill = 1_000_000_000_000L;
    long minFuel = trill / oreFor1Fuel;
    long maxFuel = 2 * minFuel;
    while (minFuel != maxFuel - 1) {
      long midFuel = (maxFuel + minFuel) / 2;
      long oreRequired = getOreRequiredForNFuel(reactions, numOre, avail, midFuel);
      if (oreRequired <= trill) {
        minFuel = midFuel;
      }
      else {
        maxFuel = midFuel;
      }
    }
    return minFuel;
  }
  
  private long getOreRequiredForNFuel(
      Map<String, Reaction> reactions,
      MutableLong numOre,
      Map<String, MutableLong> avail,
      long nFuel
  ) {
    initAvail(numOre, reactions, avail);
    ensureEnough(reactions, numOre, avail, "FUEL", nFuel);
    return numOre.longValue();
  }
  
  private void ensureEnough(
      Map<String, Reaction> reactions,
      MutableLong numOre,
      Map<String, MutableLong> avail,
      String elem,
      long quantity
  ) {
    MutableLong elemAvail = avail.get(elem);
    long shortBy = quantity - elemAvail.longValue();
    if (shortBy > 0) {
      if ("ORE".equals(elem)) {
        numOre.add(shortBy);
        elemAvail.add(shortBy);
      }
      else {
        Reaction reaction = reactions.get(elem);
        long numBatches = (shortBy / reaction.output.qty) + ((shortBy % reaction.output.qty == 0) ? 0 : 1);
        for (Component input : reaction.inputs) {
          ensureEnough(reactions, numOre, avail, input.elem, numBatches * input.qty);
          avail.get(input.elem).subtract(numBatches * input.qty);
        }
        elemAvail.add(numBatches * reaction.output.qty);
      }
    } 
  }
  
  private void initAvail(MutableLong numOre, Map<String, Reaction> reactions, Map<String, MutableLong> avail) {
    Consumer<String> initer = (output) -> avail.computeIfAbsent(output, (i) -> new MutableLong(0)).setValue(0);
    numOre.setValue(0);
    reactions.keySet().forEach(initer);
    initer.accept("ORE");
  }
  
  private static final class Reaction {
    private final Component output;
    private final Set<Component> inputs; 
    
    private Reaction(String spec) {
      String[] ioParts = spec.split(" => ");
      this.output = new Component(ioParts[1]);
      this.inputs = Arrays.stream(ioParts[0].split(", ")).map(Component::new).collect(Collectors.toSet());
    }
  }
  
  private static final class Component {
    private final long qty;
    private final String elem;
    
    private Component(String spec) {
      String[] parts = spec.split(" ");
      this.qty = Long.parseLong(parts[0]);
      this.elem = parts[1];
    }
  }
}
