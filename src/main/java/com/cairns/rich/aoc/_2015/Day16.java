package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

/**
 * Good ol' aunt Sue gave us a present.  But we have a bunch of them.
 * Let's figure out which of them sent us the gift based on filter restrictions.
 */
class Day16 extends Base2015 {
  private static final Map<String, Att> attLookup = EnumUtils.getLookup(Att.class);
  private static final Function<Att, IntPredicate> toLt = (att) -> (v) -> v < att.target;
  private static final Function<Att, IntPredicate> toGt = (att) -> (v) -> v > att.target;

  /**
   * Finds the correct Sue with filters enforcing equivalence to targets.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    return filter(loader, buildFilters());
  }

  /**
   * Finds the correct Sue with filters that enforce equivalence to targets,
   * but overriding certain ones to be range filters (again against targets).
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    Map<Att, IntPredicate> filters = buildFilters();
    BiConsumer<Att, Function<Att, IntPredicate>> override = (att, cmp) -> filters.put(att, cmp.apply(att));
    override.accept(Att.Cats, toGt);
    override.accept(Att.Trees, toGt);
    override.accept(Att.Pomeranians, toLt);
    override.accept(Att.Goldfish, toLt);
    return filter(loader, filters);
  }

  /**
   * Factory method for building the filters we expect of our Sues.
   */
  private Map<Att, IntPredicate> buildFilters() {
    return attLookup.values().stream().collect(Collectors.toMap(Function.identity(), (att) -> (v) -> v == att.target));
  }

  /**
   * Returns the index of the matching sue.  This is fairly straightforward to detect.
   * The correct Sue is the one who passes all of the restriction tests (or is unspecified).
   */
  private int filter(Loader loader, Map<Att, IntPredicate> tests) {
    List<EnumMap<Att, Integer>> sues = loader.ml(this::parseLine);
    for (int i = 0; i < sues.size(); ++i) {
      EnumMap<Att, Integer> sue = sues.get(i);
      if (tests.keySet().stream().allMatch((att) -> !sue.containsKey(att) || tests.get(att).test(sue.get(att)))) {
        return i + 1;
      }
    }
    throw fail();
  }

  /**
   * Enumeration of the different attributes a Sue can have.
   */
  private enum Att implements HasId<String> {
    Children(3),
    Cats(7),
    Samoyeds(2),
    Pomeranians(3),
    Akitas(0),
    Vizslas(0),
    Goldfish(5),
    Trees(3),
    Cars(2),
    Perfumes(1);

    private final int target;

    private Att(int target) {
      this.target = target;
    }

    @Override
    public String getId() {
      return name().toLowerCase();
    }
  }

  /**
   * Parser function to turn an input line into an {@link EnumMap} describing how many of each {@link Att} a Sue has.
   * Note that missing entries do NOT indicate zero, but rather an unknown value.
   */
  private EnumMap<Att, Integer> parseLine(String line) {
    EnumMap<Att, Integer> sue = new EnumMap<>(Att.class);
    String right = line.substring(line.indexOf(':') + 2);
    for (String attSpec : right.split(", ")) {
      String[] parts = attSpec.split(": ");
      sue.put(attLookup.get(parts[0]), Integer.parseInt(parts[1]));
    }
    return sue;
  }
}
