package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day15 extends Base2015 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Ingredient> ingredients = loader.ml(Ingredient::new);
    return findBestDistribution(100, 0, ingredients, new HashMap<>(), (c) -> 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Ingredient> ingredients = loader.ml(Ingredient::new);
    return findBestDistribution(100, 0, ingredients, new HashMap<>(), (c) -> (c == 500) ? 1 : 0);
  }

  private int findBestDistribution(
      int spaceLeft,
      int placeIndex,
      List<Ingredient> ingredients,
      Map<Ingredient, Integer> distribution,
      IntUnaryOperator calorieHandler
  ) {
    if (placeIndex == ingredients.size() - 1) {
      distribution.put(ingredients.get(placeIndex), spaceLeft);
      return scoreDistribution(distribution, calorieHandler);
    }
    int maxScore = 0;
    for (int i = 0; i < spaceLeft; ++i) {
      distribution.put(ingredients.get(placeIndex), i);
      int subScore = findBestDistribution(spaceLeft - i, placeIndex + 1, ingredients, distribution, calorieHandler);
      if (maxScore < subScore) {
        maxScore = subScore;
      }
    }
    return maxScore;
  }

  private int scoreDistribution(Map<Ingredient, Integer> distribution, IntUnaryOperator calorieHandler) {
    int score = 1;
    for (Metric metric : EnumUtils.enumValues(Metric.class)) {
      int metricScore = 0;
      for (Ingredient ing : distribution.keySet()) {
        metricScore += ing.metrics.get(metric) * distribution.get(ing);
      }
      if (metric == Metric.Calories) {
        metricScore = calorieHandler.applyAsInt(metricScore);
      }
      if (metricScore <= 0) {
        return 0;
      }
      score *= metricScore;
    }
    return score;
  }

  private enum Metric {
    Capacity,
    Durability,
    Flavor,
    Texture,
    Calories;
  }

  private static class Ingredient {
    private static final Pattern pattern = Pattern.compile(
        ".+: capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)"
    );

    private final EnumMap<Metric, Integer> metrics = new EnumMap<>(Metric.class);

    private Ingredient(String spec) {
      Matcher matcher = matcher(pattern, spec);
      for (Metric metric : EnumUtils.enumValues(Metric.class)) {
        metrics.put(metric, num(matcher, 1 + metric.ordinal()));
      }
    }
  }
}
