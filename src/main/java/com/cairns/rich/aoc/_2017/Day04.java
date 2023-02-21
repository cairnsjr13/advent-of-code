package com.cairns.rich.aoc._2017;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

class Day04 extends Base2017 {
  @Override
  protected void run() {
    List<String[]> passphrases = fullLoader.ml((line) -> line.split(" +"));
    System.out.println(passphrases.stream().filter(this::isSimpleValid).count());
    System.out.println(passphrases.stream().filter(this::isComplexValid).count());
  }

  private boolean isSimpleValid(String[] passphrase) {
    return isValid(passphrase, Function.identity());
  }

  private boolean isComplexValid(String[] passphrase) {
    return isValid(passphrase, (word) -> {
      char[] chs = word.toCharArray();
      Arrays.sort(chs);
      return new String(chs);
    });
  }

  private <T> boolean isValid(String[] passphrase, Function<String, T> transform) {
    Set<T> seen = new HashSet<>();
    for (String word : passphrase) {
      if (!seen.add(transform.apply(word))) {
        return false;
      }
    }
    return true;
  }
}
