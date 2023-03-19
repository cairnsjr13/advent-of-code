package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day23 extends Base2018 {
  private static final int INIT_SHIFT = 23;

  @Override
  protected Object part1(Loader2 loader) {
    List<Bot> bots = loader.ml(Bot::new);
    Bot strongest = getMax(bots, (bot) -> bot.r);
    return bots.stream().filter((bot) -> manhattan(0, strongest, bot.x, bot.y, bot.z) <= strongest.r).count();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Bot> bots = loader.ml(Bot::new);
    Table<Character, StatType, Integer> stats = initStats(bots);
    for (int shift = INIT_SHIFT; shift >= 0; --shift) {
      int maxNumInRange = 0;
      for (int x = stats.get('x', StatType.Min); x <= stats.get('x', StatType.Max); ++x) {
        for (int y = stats.get('y', StatType.Min); y <= stats.get('y', StatType.Max); ++y) {
          for (int z = stats.get('z', StatType.Min); z <= stats.get('z', StatType.Max); ++z) {
            int numInRange = 0;
            for (Bot bot : bots) {
              if (manhattan(shift, bot, x, y, z) <= (bot.r >> shift)) {
                ++numInRange;
              }
            }
            if (maxNumInRange < numInRange) {
              maxNumInRange = numInRange;
              stats.put('x', StatType.Best, x);
              stats.put('y', StatType.Best, y);
              stats.put('z', StatType.Best, z);
            }
          }
        }
      }
      updateStats(stats);
    }
    return Bot.coords.keySet().stream().mapToInt((c) -> Math.abs(stats.get(c, StatType.Best))).sum();
  }

  private Table<Character, StatType, Integer> initStats(List<Bot> bots) {
    Table<Character, StatType, Integer> stats = HashBasedTable.create();
    Bot.coords.forEach((coord, to) -> {
      stats.put(coord, StatType.Min, to.applyAsInt(getMin(bots, to::applyAsInt)) >> INIT_SHIFT);
      stats.put(coord, StatType.Max, to.applyAsInt(getMax(bots, to::applyAsInt)) >> INIT_SHIFT);
      stats.put(coord, StatType.Best, Integer.MIN_VALUE);
    });
    return stats;
  }

  private void updateStats(Table<Character, StatType, Integer> stats) {
    Bot.coords.keySet().forEach((coord) -> {
      int best = stats.get(coord, StatType.Best);
      stats.put(coord, StatType.Min, (best - 1) * 2);
      stats.put(coord, StatType.Max, (best + 1) * 2);
    });
  }

  private int manhattan(int shift, Bot bot, int x, int y, int z) {
    return Math.abs((bot.x >> shift) - x) + Math.abs((bot.y >> shift) - y) + Math.abs((bot.z >> shift) - z);
  }

  private static class Bot {
    private static final Pattern pattern = Pattern.compile("^pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)$");
    private static final Map<Character, ToIntFunction<Bot>> coords =
        Map.of('x', (b) -> b.x, 'y', (b) -> b.y, 'z', (b) -> b.z);

    private final int x;
    private final int y;
    private final int z;
    private final int r;

    private Bot(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.x = num(matcher, 1);
      this.y = num(matcher, 2);
      this.z = num(matcher, 3);
      this.r = num(matcher, 4);
    }
  }

  private enum StatType {
    Min,
    Max,
    Best;
  }
}
