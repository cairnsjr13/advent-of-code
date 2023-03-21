package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

class Day04 extends Base2017 {
  @Override
  protected Object part1(Loader loader) {
    List<String[]> passphrases = loader.ml((line) -> line.split(" +"));
    return passphrases.stream().filter((passphrase) -> isValid(passphrase, Function.identity())).count();
  }

  @Override
  protected Object part2(Loader loader) {
    List<String[]> passphrases = loader.ml((line) -> line.split(" +"));
    return passphrases.stream().filter((passphrase) -> isValid(passphrase, (word) -> {
      char[] chs = word.toCharArray();
      Arrays.sort(chs);
      return new String(chs);
    })).count();
  }

  private <T> boolean isValid(String[] passphrase, Function<String, T> transform) {
    Set<T> seen = new HashSet<>();
    return Arrays.stream(passphrase).map(transform).allMatch(seen::add);
  }
}
