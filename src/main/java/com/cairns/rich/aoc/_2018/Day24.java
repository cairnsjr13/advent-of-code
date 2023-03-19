package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

class Day24 extends Base2018 {
  private static final String IMM = "Immune System:";
  private static final Comparator<Group> targetSelectionChooseCmp = Comparator
      .<Group>comparingInt((g) -> -g.effectivePower())
      .thenComparingInt((g) -> -g.initiative);
  private static final Comparator<Group> attackOrderCmp = Comparator.<Group>comparingInt((g) -> -g.initiative);

  @Override
  protected Object part1(Loader2 loader) {
    return getFightResultsWhen(loader, (result) -> true);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getFightResultsWhen(loader, (result) -> IMM.equals(result.getLeft()));
  }

  private int getFightResultsWhen(Loader2 loader, Predicate<Pair<String, Integer>> when) {
    List<Group> groups = fullLoader.gDelim("", Group::parseTeam).stream().flatMap(List::stream).collect(Collectors.toList());
    for (int immBoost = 0; true; ++immBoost) {
      Pair<String, Integer> result = fight(groups, immBoost);
      if (when.test(result)) {
        return result.getRight();
      }
    }
  }

  private Pair<String, Integer> fight(List<Group> allGroups, int immBoost) {
    allGroups = allGroups.stream().map(Group::clone).collect(Collectors.toList());
    allGroups.stream().filter((g) -> IMM.equals(g.team)).forEach((g) -> g.attackDamage += immBoost);
    while (true) {
      if (shouldStopAfterAttack(allGroups, computeAttacks(allGroups))) {
        return Pair.of(
            allGroups.stream().filter((g) -> g.numUnits > 0).findFirst().get().team,
            allGroups.stream().mapToInt((g) -> g.numUnits).sum()
        );
      }
    }
  }

  private Map<Group, Group> computeAttacks(List<Group> allGroups) {
    Map<Group, Group> toAttack = new HashMap<>();
    Collections.sort(allGroups, targetSelectionChooseCmp);
    for (Group attacker : allGroups) {
      if (attacker.effectivePower() > 0) {
        List<Group> attackCandidates = attackCandidates(attacker, allGroups, toAttack.values());
        Collections.sort(attackCandidates, Comparator
            .<Group>comparingInt((g) -> -g.damageBy(attacker))
            .thenComparingInt((g) -> -g.effectivePower())
            .thenComparingInt((g) -> -g.initiative)
        );
        if (!attackCandidates.isEmpty()) {
          toAttack.put(attacker, attackCandidates.get(0));
        }
      }
    }
    return toAttack;
  }

  private boolean shouldStopAfterAttack(List<Group> allGroups, Map<Group, Group> toAttack) {
    int totalNumKilled = 0;
    Collections.sort(allGroups, attackOrderCmp);
    for (Group attacker : allGroups) {
      if ((attacker.effectivePower() > 0) && toAttack.containsKey(attacker)) {
        Group attacked = toAttack.get(attacker);
        int damage = attacked.damageBy(attacker);
        int unitsKilled = Math.min(attacked.numUnits, damage / attacked.hitPoints);
        totalNumKilled += unitsKilled;
        attacked.numUnits -= unitsKilled;
      }
    }
    return totalNumKilled == 0;
  }

  private List<Group> attackCandidates(
      Group attacker,
      List<Group> allGroups,
      Collection<Group> underAttack
  ) {
    return allGroups.stream()
        .filter((g) -> !underAttack.contains(g))
        .filter((g) -> !attacker.team.equals(g.team))
        .filter((g) -> g.numUnits > 0)
        .filter((g) -> g.damageBy(attacker) > 0)
        .collect(Collectors.toList());
  }

  private static final Multiset<String> teamCounts = HashMultiset.create();

  private static class Group implements Cloneable {
    private static final Map<String, AttackType> attackTypesLookup = EnumUtils.getLookup(AttackType.class);
    private static final Pattern pattern = Pattern.compile(
        "^(\\d+) units each with (\\d+) hit points (\\(([^)]+)\\) )?"
      + "with an attack that does (\\d+) ([^ ]+) damage "
      + "at initiative (\\d+)$"
    );

    private final String team;
    private int numUnits;
    private final int hitPoints;
    private final EnumMap<AttackType, Integer> attackFactors;
    private int attackDamage;
    private final AttackType attackType;
    private final int initiative;

    private static List<Group> parseTeam(List<String> lines) {
      String team = lines.get(0);
      return lines.subList(1, lines.size()).stream().map((line) -> new Group(team, line)).collect(Collectors.toList());
    }

    private Group(String team, String spec) {
      this.team = team;
      teamCounts.add(team);
      Matcher matcher = matcher(pattern, spec);
      this.numUnits = num(matcher, 1);
      this.hitPoints = num(matcher, 2);
      this.attackFactors = buildAttackFactors(matcher.group(4));
      this.attackDamage = num(matcher, 5);
      this.attackType = attackTypesLookup.get(matcher.group(6));
      this.initiative = num(matcher, 7);
    }

    private EnumMap<AttackType, Integer> buildAttackFactors(String defenseTypeSpec) {
      EnumMap<AttackType, Integer> attackFactors = new EnumMap<>(AttackType.class);
      if (defenseTypeSpec != null) {
        for (String defensePiece : defenseTypeSpec.split("; ")) {
          String[] typePieces = defensePiece.split(",? ");
          int factor = ("weak".equals(typePieces[0])) ? 2 : 0;
          for (int i = 2; i < typePieces.length; ++i) {
            attackFactors.put(attackTypesLookup.get(typePieces[i]), factor);
          }
        }
      }
      return attackFactors;
    }

    private int effectivePower() {
      return numUnits * attackDamage;
    }

    private int damageBy(Group attacker) {
      return (team.equals(attacker.team))
           ? 0
           : attacker.effectivePower() * attackFactors.getOrDefault(attacker.attackType, 1);
    }

    @Override
    public Group clone() {
      return quietly(() -> (Group) super.clone());
    }
  }

  private enum AttackType implements HasId<String> {
    Radiation,
    Bludgeoning,
    Slashing,
    Fire,
    Cold;

    @Override
    public String getId() {
      return name().toLowerCase();
    }
  }
}
