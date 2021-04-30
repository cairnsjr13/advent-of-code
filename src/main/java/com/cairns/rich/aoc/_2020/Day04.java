package com.cairns.rich.aoc._2020;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

class Day04 extends Base2020 {
  private static final Set<String> requiredFields =
      Set.of("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid");
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
  private static final Set<Character> hexes = new HashSet<>();
  
  static {
    hexes.add('0'); hexes.add('1'); hexes.add('2'); hexes.add('3'); hexes.add('4');
    hexes.add('5'); hexes.add('6'); hexes.add('7'); hexes.add('8'); hexes.add('9');
    hexes.add('a'); hexes.add('b'); hexes.add('c'); hexes.add('d'); hexes.add('e'); hexes.add('f');
  }
  
  @Override
  protected void run() {
    List<Line> lines = fullLoader.ml(Line::new);
    List<Map<String, String>> passports = new ArrayList<>();
    Line curLine = null;
    for (Line line : lines) {
      if (curLine == null) {
        curLine = line;
      }
      else if (line.fields.isEmpty()) {
        passports.add(curLine.fields);
        curLine = null;
      }
      else {
        curLine.merge(line);
      }
    }
    passports.add(curLine.fields);
    
    System.out.println(countNumValid(passports));
    System.out.println(countNumValidAndCorrect(passports));
  }
  
  private int countNumValid(List<Map<String, String>> passports) {
    int numValid = 0;
    for (Map<String, String> passport : passports) {
      if (passport.keySet().containsAll(requiredFields)) {
        ++numValid;
      }
    }
    return numValid;
  }
  
  private boolean allFieldsValid(Map<String, String> passport) {
    if (!passport.keySet().containsAll(requiredFields)) {
      return false;
    }
    for (String fieldName : fieldValidators.keySet()) {
      String value = passport.get(fieldName);
      if (!fieldValidators.get(fieldName).test(value)) {
        return false;
      }
    }
    return true;
  }
  
  private int countNumValidAndCorrect(List<Map<String, String>> passports) {
    int numValid = 0;
    for (Map<String, String> passport : passports) {
      if (allFieldsValid(passport)) {
        ++numValid;
      }
    }
    return numValid;
  }
  
  private static boolean numberValid(String value, long lower, long upper) {
    try {
      long number = Long.parseLong(value);
      return (lower <= number) && (number <= upper);
    }
    catch (NumberFormatException e) { }
    return false;
  }
  
  private static class Line {
    private final Map<String, String> fields = new HashMap<>();
   
    private Line(String spec) {
      if (spec.length() == 0) {
        return;
      }
      String[] fieldSpecs = spec.split(" ");
      for (String field : fieldSpecs) {
        String[] parts = field.split(":");
        fields.put(parts[0], parts[1]);
      }
    }
    
    private void merge(Line other) {
      fields.putAll(other.fields);
    }
  }
}
