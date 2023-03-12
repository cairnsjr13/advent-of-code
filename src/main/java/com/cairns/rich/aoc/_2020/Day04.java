package com.cairns.rich.aoc._2020;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

class Day04 extends Base2020 {
  private static final Set<String> validEcl = Set.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth");
  private static final Map<String, Predicate<String>> fieldValidators = Map.of(
      "byr", (value) -> numberValid(value, 1920, 2002),
      "iyr", (value) -> numberValid(value, 2010, 2020),
      "eyr", (value) -> numberValid(value, 2020, 2030),
      "hgt", (value) -> {
        String unit = value.substring(value.length() - 2);
        String count = value.substring(0, value.length() - 2);
        return ("cm".equals(unit) && numberValid(count, 150, 193))
            || ("in".equals(unit) && numberValid(count, 59, 76));
      },
      "hcl", (value) -> Pattern.matches("^#[0-9a-z]{6}$", value),
      "ecl", validEcl::contains,
      "pid", (value) -> Pattern.matches("^[0-9]{9}$", value)
  );

  @Override
  protected void run() {
    List<Passport> passports = fullLoader.gDelim("", Passport::new);
    System.out.println(count(passports, Passport::hasAllRequired));
    System.out.println(count(passports, Passport::hasAllRequiredAndAllValid));
  }

  private long count(List<Passport> passports, Predicate<Passport> filter) {
    return passports.stream().filter(filter).count();
  }

  private static boolean numberValid(String value, long lower, long upper) {
    try {
      long number = Long.parseLong(value);
      return (lower <= number) && (number <= upper);
    }
    catch (NumberFormatException e) { }
    return false;
  }

  private static class Passport {
    private final Map<String, String> fields = new HashMap<>();

    private Passport(List<String> specs) {
      for (String spec : specs) {
        for (String field : spec.split(" ")) {
          String[] parts = field.split(":");
          fields.put(parts[0], parts[1]);
        }
      }
    }

    private boolean hasAllRequired() {
      return fields.keySet().containsAll(fieldValidators.keySet());
    }

    private boolean hasAllRequiredAndAllValid() {
      return hasAllRequired()
          && fieldValidators.keySet().stream().allMatch((name) -> fieldValidators.get(name).test(fields.get(name)));
    }
  }
}
