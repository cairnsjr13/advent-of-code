package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day21 extends Base2020 {
  @Override
  protected Object part1(Loader2 loader) {
    State state = new State(loader);
    Set<String> totallySafeIngredients = Sets.difference(state.allIngredients, state.ingredientsThatCouldHaveAllergen);
    return totallySafeIngredients.stream()
        .mapToLong((totallySafe) -> state.foods.stream().filter((food) -> food.ingredients.contains(totallySafe)).count())
        .sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    State state = new State(loader);
    Map<String, String> discovered = new TreeMap<>();
    while (!state.allergensToIngredientOptions.isEmpty()) {
      String discoveredAllergen = findAllergenWithOneOption(state.allergensToIngredientOptions);
      String ingredient = state.allergensToIngredientOptions.get(discoveredAllergen).iterator().next();
      discovered.put(discoveredAllergen, ingredient);
      state.allergensToIngredientOptions.remove(discoveredAllergen, ingredient);
      for (String allergen : state.allergensToIngredientOptions.keySet()) {
        state.allergensToIngredientOptions.get(allergen).remove(ingredient);
      }
      state.allergensToIngredientOptions.removeAll(discoveredAllergen);
    }
    return discovered.values().stream().collect(Collectors.joining(","));
  }

  private String findAllergenWithOneOption(Multimap<String, String> allergensToIngredientOptions) {
    return allergensToIngredientOptions.keys().stream()
        .filter((allergen) -> allergensToIngredientOptions.get(allergen).size() == 1)
        .findFirst().get();
  }

  private static class Food {
    private static Pattern pattern = Pattern.compile("^(.+) \\(contains (.+)\\)$");

    private final Set<String> ingredients;
    private final Set<String> allergens;

    private Food(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.ingredients = new HashSet<>(Arrays.asList(matcher.group(1).split(" ")));
      this.allergens = new HashSet<>(Arrays.asList(matcher.group(2).split(", ")));
    }
  }

  private static final class State {
    private final List<Food> foods;
    private final Set<String> allIngredients;
    private final Multimap<String, String> allergensToIngredientOptions = HashMultimap.create();
    private final Set<String> ingredientsThatCouldHaveAllergen = new HashSet<>();

    private State(Loader2 loader) {
      this.foods = loader.ml(Food::new);
      this.allIngredients = toFieldSet(foods, (food) -> food.ingredients);
      getAllergensToIngredientOptions();
    }

    private void getAllergensToIngredientOptions() {
      for (String allergen : toFieldSet(foods, (food) -> food.allergens)) {
        Set<String> ingredientCandidates = new HashSet<>();
        foods.stream().filter((food) -> food.allergens.contains(allergen)).forEach((food) -> {
          if (ingredientCandidates.isEmpty()) {
            ingredientCandidates.addAll(food.ingredients);
          }
          else {
            ingredientCandidates.retainAll(food.ingredients);
          }
        });
        ingredientsThatCouldHaveAllergen.addAll(ingredientCandidates);
        allergensToIngredientOptions.putAll(allergen, ingredientCandidates);
      }
    }

    private Set<String> toFieldSet(List<Food> foods, Function<Food, Set<String>> toField) {
      return foods.stream().map(toField).flatMap(Set::stream).collect(Collectors.toSet());
    }
  }
}
