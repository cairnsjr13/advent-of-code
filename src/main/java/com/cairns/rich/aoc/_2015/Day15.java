package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.EnumMap;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We are apparently amazing cookie makers.  We have ingredients and we
 * need to find the maximum scoring recipe based on various restrictions.
 */
class Day15 extends Base2015 {
  private static final int INITIAL_TEASPOONS = 100;

  /**
   * Finds the score of the best recipe with no calorie restrictions.
   */
  @Override
  protected Object part1(Loader loader) {
    return findBestDistribution(loader, (c) -> 1);
  }

  /**
   * Finds the score of the best recipe with exactly 500 calories.
   */
  @Override
  protected Object part2(Loader loader) {
    return findBestDistribution(loader, (c) -> (c == 500) ? 1 : 0);
  }

  /**
   * Helper method to find the score of the best recipe with the given calorie restriction.
   * All initial recursion state can be handled here instead of duplicated in each part.
   */
  private Object findBestDistribution(Loader loader, IntUnaryOperator calorieScoreHandler) {
    return findBestDistribution(INITIAL_TEASPOONS, 0, loader.ml(Ingredient::new), HashMultiset.create(), calorieScoreHandler);
  }

  /**
   * Recursively computes the best score possible for each amount available for the current ingredient.
   * The last ingredient placed must have the all of the remaining teaspoons.
   * All other ingredients can fill all or none of the remaining teaspoons.
   *
   * @implNote the maximum depth of the recursive tree is the number of ingredients (so no risk of stack overflow)
   */
  private int findBestDistribution(
      int spaceLeft,
      int placeIndex,
      List<Ingredient> ingredients,
      Multiset<Ingredient> distribution,
      IntUnaryOperator calorieScoreHandler
  ) {
    Ingredient ingredient = ingredients.get(placeIndex);
    if (placeIndex == ingredients.size() - 1) {
      distribution.setCount(ingredient, spaceLeft);
      return scoreDistribution(distribution, calorieScoreHandler);
    }
    int maxScore = 0;
    for (int i = 0; i < spaceLeft; ++i) {
      distribution.setCount(ingredient, i);
      int subScore = findBestDistribution(spaceLeft - i, placeIndex + 1, ingredients, distribution, calorieScoreHandler);
      if (maxScore < subScore) {
        maxScore = subScore;
      }
    }
    return maxScore;
  }

  /**
   * Computes the score of the given distribution.  Here are the various rules
   *   - for non calorie metrics, the metricScore is the sum of the metric total across all ingredients
   *   - calorie metrics will be mapped based on the given operator
   *   - any metric score <= 0 will result in the whole score being zeroed out
   *   - the product of all metric scores is the full distribution score.
   */
  private int scoreDistribution(Multiset<Ingredient> distribution, IntUnaryOperator calorieHandler) {
    int score = 1;
    for (Metric metric : EnumUtils.enumValues(Metric.class)) {
      int metricScore = 0;
      for (Ingredient ing : distribution.elementSet()) {
        metricScore += ing.metrics.get(metric) * distribution.count(ing);
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

  /**
   * Enumeration of the various metrics each {@link Ingredient} has.
   */
  private enum Metric {
    Capacity,
    Durability,
    Flavor,
    Texture,
    Calories;
  }

  /**
   * Input class describing the {@link Metric}s for an ingredient.
   */
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
