package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Santa got someone an rpg game.  It uses magic.  We are going to simulate it.
 */
class Day22 extends Base2015 {
  /**
   * Returns the minimum amount of mana a player can use and still defeat the boss.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part1(Loader loader) {
    return runSimulation(loader, 0);
  }

  /**
   * Returns the minimum amount of mana a player can use and still defeat the boss when they are forced to lose 1 hp each turn.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object part2(Loader loader) {
    return runSimulation(loader, 1);
  }

  /**
   * Returns the minimum amount of mana a player can use and still
   * defeat the boss when they lose the given number of hp each turn.
   */
  private int runSimulation(Loader loader, int playerTurnPenalty) {
    List<String> bossLines = loader.ml();
    Stats player = new Stats(50, 0, 500);
    Stats boss = new Stats(bossLines);
    State state = new State(player, boss, playerTurnPenalty);
    playerTurn(state);
    return state.bestManaSoFar;
  }

  /**
   * Inflicts turn penalties on the players hp and then runs the logic for a player's turn.
   * A player's turn consists of trying each of the spells available to it.
   * As this is a recursive method, any changes will be undone when it completes.
   */
  private static void playerTurn(State state) {
    state.player.hp -= state.playerTurnPenalty;
    try {
      if (state.player.hp > 0) {
        tickEffects(state, () -> {
          for (Spell spell : EnumUtils.enumValues(Spell.class)) {
            spell.tryCast(state);
          }
        });
      }
    }
    finally {
      state.player.hp += state.playerTurnPenalty;
    }
  }

  /**
   * Inflicts damage to the player based on current stats.
   * If the player is still alive, will trigger the {@link #playerTurn(State)}.
   * As this is a recursive method, any changes will be undone when it completes.
   */
  private static void bossTurn(State state) {
    tickEffects(state, () -> {
      int damage = Math.max(1, state.boss.damage - state.playerArmor());
      state.player.hp -= damage;
      try {
        if (state.player.hp > 0) {
          playerTurn(state);
        }
      }
      finally {
        state.player.hp += damage;
      }
    });
  }

  /**
   * Helper method to inflict (and eventually undo) any active spell effects around the given turn logic.
   * Part of the process is decrementing the turns remaining for each active spell (represented in the multimap).
   * Will short circuit if the current state has used more mana than our best so far.
   * As this is a recursive method, any changes will be undone when it completes.
   */
  private static void tickEffects(State state, Runnable turnWithEffects) {
    if (state.used >= state.bestManaSoFar) {
      return;
    }
    Set<Spell> effects = new HashSet<>(state.activeEffects.elementSet());
    int bossHpBefore = state.boss.hp;
    int playerManaBefore = state.player.mana;
    effects.forEach(state.activeEffects::remove);
    try {
      if (effects.contains(Spell.Poison)) {
        state.boss.hp -= 3;
      }
      if (effects.contains(Spell.Rechrg)) {
        state.player.mana += 101;
      }
      if (state.boss.hp <= 0) {
        state.bestManaSoFar = state.used;
        return;
      }
      turnWithEffects.run();
    }
    finally {
      effects.forEach(state.activeEffects::add);
      state.player.mana = playerManaBefore;
      state.boss.hp = bossHpBefore;
    }
  }

  /**
   * Container object that holds the fighter stats as well as any active effects.
   * This will make simulating a fight easier as we dont have to pass around a ton of mutable state.
   */
  private static class State {
    private final Stats player;
    private final Stats boss;
    private final Multiset<Spell> activeEffects = HashMultiset.create();
    private final int playerTurnPenalty;

    private int bestManaSoFar = Integer.MAX_VALUE;
    private int used = 0;

    private State(Stats player, Stats boss, int playerTurnPenalty) {
      this.player = player;
      this.boss = boss;
      this.playerTurnPenalty = playerTurnPenalty;
    }

    /**
     * Returns the armor stats for the player given the currently active spells.
     */
    private int playerArmor() {
      return (activeEffects.contains(Spell.Shield)) ? 7 : 0;
    }
  }

  /**
   * Container object to encapsulate the stats for a fighter.
   */
  private static class Stats {
    private int hp;
    private final int damage;
    private int mana;

    private Stats(List<String> lines) {
      Map<String, Integer> stats =
          lines.stream().map((s) -> s.split(": ")).collect(Collectors.toMap((p) -> p[0], (p) -> Integer.parseInt(p[1])));
      this.hp = stats.get("Hit Points");
      this.damage = stats.get("Damage");
      this.mana = 0;
    }

    private Stats(int hp, int damage, int mana) {
      this.hp = hp;
      this.damage = damage;
      this.mana = mana;
    }
  }

  /**
   * The various spells a player can cast along with the various stats for each.
   */
  private enum Spell {
    Missile(53, -4, 0, 0),
    Drain(73, -2, 2, 0),
    Shield(113, 0, 0, 6),
    Poison(173, 0, 0, 6),
    Rechrg(229, 0, 0, 5);

    protected final int cost;
    protected final int deltaBossHp;
    protected final int deltaPlayerHp;
    protected final int timerOnCast;

    private Spell(int cost, int deltaBossHp, int deltaPlayerHp, int timerOnCast) {
      this.cost = cost;
      this.deltaBossHp = deltaBossHp;
      this.deltaPlayerHp = deltaPlayerHp;
      this.timerOnCast = timerOnCast;
    }

    /**
     * Attempts to case this spell on the given state.  Will only do this if the player has enough mana
     * and the spell is not already active.  The impact of the spell is immediately registered and then
     * a {@link Day22#bossTurn(State)} will be triggered.
     * As this is a recursive method, any changes will be undone when it completes.
     */
    private void tryCast(State state) {
      if (state.player.mana >= cost) {
        if (!state.activeEffects.contains(this)) {
          state.activeEffects.add(this, timerOnCast);
          state.player.mana -= cost;
          state.boss.hp += deltaBossHp;
          state.player.hp += deltaPlayerHp;
          state.used += cost;
          try {
            bossTurn(state);
          }
          finally {
            state.used -= cost;
            state.player.hp -= deltaPlayerHp;
            state.boss.hp -= deltaBossHp;
            state.player.mana += cost;
            state.activeEffects.remove(this, timerOnCast);
          }
        }
      }
    }
  }
}
