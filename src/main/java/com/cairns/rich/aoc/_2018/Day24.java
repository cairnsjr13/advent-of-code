package com.cairns.rich.aoc._2018;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.cairns.rich.aoc.EnumUtils;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

class Day24 extends Base2018 {
  private static final Map<String, AttackType> attackTypesLookup = EnumUtils.getLookup(AttackType.class);
  private static final Comparator<Group> targetSelectionChooseCmp = Comparator
      .<Group>comparingInt((g) -> -g.effectivePower())
      .thenComparing((g) -> -g.initiative);
  private static final Comparator<Group> attackOrderCmp = Comparator.<Group>comparingInt((g) -> -g.initiative);
  
  @Override
  protected void run() {
    List<String> lines = fullLoader.ml();
    int idxOfBlank = lines.indexOf("");
    List<Group> allGroups = new ArrayList<>();
    lines.subList(1, idxOfBlank).stream().map((spec) -> new Group("imm", spec)).forEach(allGroups::add);
    lines.subList(idxOfBlank + 2, lines.size()).stream().map((spec) -> new Group("inf", spec)).forEach(allGroups::add);
    
    System.out.println(fight(allGroups, 0));
    for (int immBoost = 1; true; ++immBoost) {
      Pair<String, Integer> result = fight(allGroups, immBoost);
      if ("imm".equals(result.getLeft())) {
        System.out.println(immBoost + " - " + result);
        break;
      }
    }
  }
  
  private Pair<String, Integer> fight(List<Group> allGroups, int immBoost) {
    allGroups = allGroups.stream().map((g) -> g.clone()).collect(Collectors.toList());
    allGroups.stream().filter((g) -> "imm".equals(g.team)).forEach((g) -> g.attackDamage += immBoost);
    while (shouldContinueAfterAttack(allGroups, computeAttacks(allGroups))) {
      // all actions taken in above call
    }
    return Pair.of(
        allGroups.stream().filter((g) -> g.numUnits > 0).findFirst().get().team,
        allGroups.stream().mapToInt((g) -> g.numUnits).sum()
    );
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
  
  private boolean shouldContinueAfterAttack(List<Group> allGroups, Map<Group, Group> toAttack) {
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
    return totalNumKilled > 0;
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
    private static final Pattern pattern = Pattern.compile(
        "^(\\d+) units each with (\\d+) hit points (\\(([^)]+)\\) )?"
      + "with an attack that does (\\d+) ([^ ]+) damage "
      + "at initiative (\\d+)$"
    );
    
    private final String team;
    private final int index;
    private int numUnits;
    private final int hitPoints;
    private final EnumMap<AttackType, Integer> attackFactors;
    private int attackDamage;
    private final AttackType attackType;
    private final int initiative;
    
    private Group(String team, String spec) {
      this.team = team;
      teamCounts.add(team);
      this.index = teamCounts.count(team);
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
      if (team.equals(attacker.team)) {
        return 0;
      }
      return attacker.effectivePower() * attackFactors.getOrDefault(attacker.attackType, 1);
    }
    
    @Override
    public String toString() {
//      return "{" + team + ", " + index + ", " + effectivePower() + "}";
      return "{" + index + " (" + initiative + ") " + team + " " + numUnits + "@" + hitPoints + "hp " + attackDamage + "/" + attackType + " " + attackFactors + "}";
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
