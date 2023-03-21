package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day02 extends Base2020 {
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Password::new).stream().filter(Password::part1IsValid).count();
  }

  @Override
  protected Object part2(Loader loader) {
    return loader.ml(Password::new).stream().filter(Password::part2IsValid).count();
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
