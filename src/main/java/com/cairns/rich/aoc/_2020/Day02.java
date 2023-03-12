package com.cairns.rich.aoc._2020;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day02 extends Base2020 {
  @Override
  protected void run() {
    List<Password> passwords = fullLoader.ml(Password::new);
    System.out.println(countValids(passwords, Password::part1IsValid));
    System.out.println(countValids(passwords, Password::part2IsValid));
  }

  private long countValids(List<Password> passwords, Predicate<Password> test) {
    return passwords.stream().filter(test).count();
  }

  private static class Password {
    private static final Pattern pattern = Pattern.compile("^(\\d+)\\-(\\d+) (.): (.+)$");

    private final int param1;
    private final int param2;
    private final char ch;
    private final String password;

    private Password(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.param1 = Integer.parseInt(matcher.group(1));
      this.param2 = Integer.parseInt(matcher.group(2));
      this.ch = matcher.group(3).charAt(0);
      this.password = matcher.group(4);
    }

    private boolean part1IsValid() {
      long count = password.chars().filter((c) -> c == ch).count();
      return (param1 <= count) && (count <= param2);
    }

    private boolean part2IsValid() {
      return (ch == password.charAt(param1 - 1))
          != (ch == password.charAt(param2 - 1));
    }
  }
}
