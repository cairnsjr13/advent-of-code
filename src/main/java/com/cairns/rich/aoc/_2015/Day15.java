package com.cairns.rich.aoc._2015;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day15 extends Base2015 {
  private static final Metric[] metrics = Metric.values();
  
  @Override
  protected void run() {
    List<Ingredient> ingredients = fullLoader.ml(Ingredient::new);
    int best = findBestDistribution(100, 0, ingredients, new HashMap<>(), (c) -> 1);
    System.out.println(best);
    int healthyBest = findBestDistribution(100, 0, ingredients, new HashMap<>(), (c) -> (c == 500) ? 1 : 0);
    System.out.println(healthyBest);
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
    for (Metric metric : metrics) {
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
      metrics.put(Metric.Capacity, Integer.parseInt(matcher.group(1)));
      metrics.put(Metric.Durability, Integer.parseInt(matcher.group(2)));
      metrics.put(Metric.Flavor, Integer.parseInt(matcher.group(3)));
      metrics.put(Metric.Texture, Integer.parseInt(matcher.group(4)));
      metrics.put(Metric.Calories, Integer.parseInt(matcher.group(5)));
    }
  }
}
