package com.cairns.rich.aoc._2015;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

class Day22 extends Base2015 {
  private static final Spell[] spells = Spell.values();
  
  @Override
  protected void run() {
    System.out.println(runSimulation(false));
    System.out.println(runSimulation(true));
  }
  
  private int runSimulation(boolean hardMode) {
    State state = new State(hardMode);
    playerTurn(state);
    return state.bestManaSoFar;
  }
  
  private static void playerTurn(State state) {
    state.player.hp -= state.playerPenalty;
    try {
      if (state.player.hp > 0) {
        tickEffects(state, () -> {
          if (state.player.mana >= 53) {
            for (Spell spell : spells) {
              spell.tryCast(state);
            }
          }
        });
      }
    }
    finally {
      state.player.hp += state.playerPenalty;
    }
  }
  
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
  
  private static class State {
    private final Stats player = new Stats(50, 0, 500);
    private final Stats boss = new Stats(51, 9, 0);
    private final Multiset<Spell> activeEffects = HashMultiset.create();
    private final int playerPenalty;

    private int bestManaSoFar = Integer.MAX_VALUE;
    private int used = 0;
    
    private State(boolean hardMode) {
      this.playerPenalty = (hardMode) ? 1 : 0;
    }
    
    private int playerArmor() {
      return (activeEffects.contains(Spell.Shield)) ? 7 : 0;
    }
  }
  
  private static class Stats {
    private int hp;
    private final int damage;
    private int mana;
    
    private Stats(int hp, int damage, int mana) {
      this.hp = hp;
      this.damage = damage;
      this.mana = mana;
    }
  }
  
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
