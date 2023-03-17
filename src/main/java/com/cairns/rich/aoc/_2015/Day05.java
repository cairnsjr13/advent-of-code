package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day05 extends Base2015 {
  private static final Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
  private static final Set<String> illegals = Set.of("ab", "cd", "pq", "xy");
  private static final Pattern twoCharRepeat = Pattern.compile("^.*(..).*\\1.*$");
  private static final Pattern oneCharRepeat = Pattern.compile("^.*(.).\\1.*$");

  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<String> inputs = loader.ml();
    result.part1(numNice(inputs, this::isOriginallyNice));
    result.part2(numNice(inputs, this::isNewNice));
  }

  private long numNice(List<String> inputs, Predicate<String> filter) {
    return inputs.stream().filter(filter).count();
  }

  private boolean isOriginallyNice(String input) {
    int numVowels = (vowels.contains(input.charAt(0))) ? 1 : 0;
    boolean hasRepeat = false;
    for (int pi = 0, ci = 1; ci < input.length(); ++pi, ++ci) {
      char pCh = input.charAt(pi);
      char cCh = input.charAt(ci);
      if (vowels.contains(cCh)) {
        ++numVowels;
      }
      if (pCh == cCh) {
        hasRepeat = true;
      }
      if (illegals.contains(pCh + "" + cCh)) {
        return false;
      }
    }
    return (numVowels >= 3) && hasRepeat;
  }

  private boolean isNewNice(String input) {
    Matcher twoCharMatcher = twoCharRepeat.matcher(input);
    Matcher oneCharMatcher = oneCharRepeat.matcher(input);
    return twoCharMatcher.matches() && oneCharMatcher.matches();
  }
}
