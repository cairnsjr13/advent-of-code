package com.cairns.rich.aoc._2015;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

class Day21 extends Base2015 {
  private static final List<Item> weapons = Arrays.asList(
      new Item("Dagger", 8, 4, 0),
      new Item("Shortsword", 10, 5, 0),
      new Item("Warhammer", 25, 6, 0),
      new Item("Longsword", 40, 7, 0),
      new Item("Greataxe", 74, 8, 0)
  );
  private static final List<Item> armors = Arrays.asList(
      new Item("Leather", 13, 0, 1),
      new Item("Chainmail", 31, 0, 2),
      new Item("Splinmail", 53, 0, 3),
      new Item("Bandedmail", 75, 0, 4),
      new Item("Platemail", 102, 0, 5),
      new Item("No Armor", 0, 0, 0)
  );
  private static final List<Item> rings = Arrays.asList(
      new Item("Damage +1", 25, 1, 0),
      new Item("Damage +2", 50, 2, 0),
      new Item("Damage +3", 100, 3, 0),
      new Item("Defense +1", 20, 0, 1),
      new Item("Defense +2", 40, 0, 2),
      new Item("Defense +3", 80, 0, 3),
      new Item("No Ring 1", 0, 0, 0),
      new Item("No Ring 2", 0, 0, 0)
  );

  @Override
  protected void run() {
    Stats boss = new Stats(8, 2);
    System.out.println(computeCost(boss, Integer.MAX_VALUE, true, (l, r) -> l < r));
    System.out.println(computeCost(boss, 0, false, (l, r) -> l > r));
  }

  private int computeCost(
      Stats boss,
      int seed,
      boolean desiredResult,
      BiPredicate<Integer, Integer> cmp
  ) {
    int bestCost = seed;
    for (Item weapon : weapons) {
      for (Item armor : armors) {
        for (int leftRingI = 0; leftRingI < rings.size(); ++leftRingI) {
          Item leftRing = rings.get(leftRingI);
          for (int rightRingI = leftRingI + 1; rightRingI < rings.size(); ++rightRingI) {
            Item rightRing = rings.get(rightRingI);
            Stats player = new Stats(100, weapon, armor, leftRing, rightRing);
            if (!cmp.test(bestCost, player.cost) && (desiredResult == doesPlayerWins(player, boss))) {
              bestCost = player.cost;
            }
          }
        }
      }
    }
    return bestCost;
  }

  private boolean doesPlayerWins(Stats player, Stats boss) {
    int playerHp = 100;
    int bossHp = 100;
    while (true) {
      bossHp -= Math.max(1, player.damage - boss.armor);
      if (bossHp <= 0) {
        return true;
      }
      playerHp -= Math.max(1, boss.damage - player.armor);
      if (playerHp <= 0) {
        return false;
      }
    }
  }

  private static class Stats {
    private final int damage;
    private final int armor;
    private final int cost;
    private final Item[] items;

    private Stats(int damage, int armor) {
      this.damage = damage;
      this.armor = armor;
      this.cost = 0;
      items = null;
    }

    private Stats(int hp, Item... items) {
      this.damage = Arrays.asList(items).stream().mapToInt((item) -> item.damage).sum();
      this.armor = Arrays.asList(items).stream().mapToInt((item) -> item.armor).sum();
      this.cost = Arrays.asList(items).stream().mapToInt((item) -> item.cost).sum();
      this.items = items;
    }

    @Override
    public String toString() {
      return Arrays.toString(items);
    }
  }

  private static class Item {
    private final String name;
    private final int cost;
    private final int damage;
    private final int armor;

    private Item(String name, int cost, int damage, int armor) {
      this.name = name;
      this.cost = cost;
      this.damage = damage;
      this.armor = armor;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
