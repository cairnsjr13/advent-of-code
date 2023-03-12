package com.cairns.rich.aoc._2020;

import com.google.common.collect.HashMultimap;
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
  protected void run() {
    List<Food> foods = fullLoader.ml(Food::new);
    Set<String> allAllergens = toFieldSet(foods, (food) -> food.allergens);
    Set<String> allIngredients = toFieldSet(foods, (food) -> food.ingredients);

    HashMultimap<String, String> allergensToIngredientOptions = HashMultimap.create();
    Set<String> ingredientsThatCouldHaveAllergen = new HashSet<>();
    getAllergensToIngredientOptions(foods, allAllergens, allergensToIngredientOptions, ingredientsThatCouldHaveAllergen);

    System.out.println(countOccurancesOfTotallySafes(foods, allIngredients, ingredientsThatCouldHaveAllergen));
    System.out.println(getCanonicalDangerousList(allergensToIngredientOptions));
  }

  private void getAllergensToIngredientOptions(
      List<Food> foods,
      Set<String> allAllergens,
      HashMultimap<String, String> allergensToIngredientOptions,
      Set<String> ingredientsThatCouldHaveAllergen
  ) {
    for (String allergen : allAllergens) {
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

  private long countOccurancesOfTotallySafes(
      List<Food> foods,
      Set<String> allIngredients,
      Set<String> ingredientsThatCouldHaveAllergen
  ) {
    Set<String> totallySafeIngredients = Sets.difference(allIngredients, ingredientsThatCouldHaveAllergen);
    return totallySafeIngredients.stream()
        .mapToLong((totallySafe) -> foods.stream().filter((food) -> food.ingredients.contains(totallySafe)).count())
        .sum();
  }

  private String getCanonicalDangerousList(HashMultimap<String, String> allergensToIngredientOptions) {
    Map<String, String> discovered = new TreeMap<>();
    while (!allergensToIngredientOptions.isEmpty()) {
      String discoveredAllergen = findAllergenWithOneOption(allergensToIngredientOptions);
      String ingredient = allergensToIngredientOptions.get(discoveredAllergen).iterator().next();
      discovered.put(discoveredAllergen, ingredient);
      allergensToIngredientOptions.remove(discoveredAllergen, ingredient);
      for (String allergen : allergensToIngredientOptions.keySet()) {
        allergensToIngredientOptions.get(allergen).remove(ingredient);
      }
      allergensToIngredientOptions.removeAll(discoveredAllergen);
    }
    return discovered.values().stream().collect(Collectors.joining(","));
  }

  private String findAllergenWithOneOption(HashMultimap<String, String> allergensToIngredientOptions) {
    return allergensToIngredientOptions.keys().stream()
        .filter((allergen) -> allergensToIngredientOptions.get(allergen).size() == 1)
        .findFirst().get();
  }

  private Set<String> toFieldSet(List<Food> foods, Function<Food, Set<String>> toField) {
    return foods.stream().map(toField).flatMap(Set::stream).collect(Collectors.toSet());
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
}
