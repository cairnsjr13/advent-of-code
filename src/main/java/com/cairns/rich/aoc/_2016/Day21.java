package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class Day21 extends Base2016 {
  private static final Map<Integer, Integer> finalIndexToLeftRotation = Map.of(
      0, 9,
      1, 1,
      2, 6,
      3, 2,
      4, 7,
      5, 3,
      6, 8,
      7, 4
  );

  @Override
  protected Object part1(Loader2 loader) {
    List<Consumer<StringBuilder>> insts = loader.ml(this::parsePart1);
    StringBuilder state = new StringBuilder("abcdefgh");
    insts.forEach((inst) -> inst.accept(state));
    return state;
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Consumer<StringBuilder>> insts = loader.ml(this::parsePart2);
    StringBuilder state = new StringBuilder("fbgdceah");
    for (int i = insts.size() - 1; i >= 0; --i) {
      insts.get(i).accept(state);
    }
    return state;
  }

  private Consumer<StringBuilder> parsePart1(String spec) {
    if (spec.startsWith("swap position ")) {
      return swapPosition(spec);
    }
    else if (spec.startsWith("swap letter ")) {
      return swapLetter(spec);
    }
    else if (spec.startsWith("reverse positions ")) {
      return reverse(spec);
    }
    else if (spec.startsWith("rotate left ")) {
      return rotateLeft(spec);
    }
    else if (spec.startsWith("rotate right ")) {
      return rotateRight(spec);
    }
    else if (spec.startsWith("rotate based on position of letter ")) {
      return rotateBased(spec);
    }
    else if (spec.startsWith("move position ")) {
      return move(spec);
    }
    throw fail(spec);
  }

  private Consumer<StringBuilder> parsePart2(String spec) {
    if (spec.startsWith("swap position ")) {
      return swapPosition(spec);
    }
    else if (spec.startsWith("swap letter ")) {
      return swapLetter(spec);
    }
    else if (spec.startsWith("reverse positions ")) {
      return reverse(spec);
    }
    else if (spec.startsWith("rotate left ")) {
      return rotateRight(parseIndex(spec, "rotate left "));
    }
    else if (spec.startsWith("rotate right ")) {
      return rotateLeft(parseIndex(spec, "rotate right "));
    }
    else if (spec.startsWith("rotate based on position of letter ")) {
      String basedOn = spec.substring(spec.length() - 1);
      return (state) -> {
        int currentIndex = state.indexOf(basedOn);
        rotateLeft(finalIndexToLeftRotation.get(currentIndex)).accept(state);
      };
    }
    else if (spec.startsWith("move position ")) {
      int index1 = parseIndex(spec, "move position ");
      int index2 = Integer.parseInt(spec.substring(spec.length() - 1));
      return move(index2, index1);
    }
    throw fail(spec);
  }

  private Consumer<StringBuilder> swapPosition(String spec) {
    int index1 = parseIndex(spec, "swap position ");
    int index2 = Integer.parseInt(spec.substring(spec.length() - 1));
    return (state) -> {
      char ch1 = state.charAt(index1);
      char ch2 = state.charAt(index2);
      state.setCharAt(index1, ch2);
      state.setCharAt(index2, ch1);
    };
  }

  private Consumer<StringBuilder> swapLetter(String spec) {
    String letter1 = parseVal(spec, "swap letter ");
    String letter2 = spec.substring(spec.length() - 1);
    return (state) -> {
      int letter1Index = state.indexOf(letter1);
      int letter2Index = state.indexOf(letter2);
      state.setCharAt(letter1Index, letter2.charAt(0));
      state.setCharAt(letter2Index, letter1.charAt(0));
    };
  }

  private Consumer<StringBuilder> reverse(String spec) {
    int index1 = parseIndex(spec, "reverse positions ");
    int index2 = Integer.parseInt(spec.substring(spec.length() - 1));
    return (state) -> {
      StringBuilder reverser = new StringBuilder(state.substring(index1, index2 + 1));
      state.replace(index1, index2 + 1, reverser.reverse().toString());
    };
  }


  private Consumer<StringBuilder> rotateLeft(String spec) {
    return rotateLeft(parseIndex(spec, "rotate left "));
  }

  private Consumer<StringBuilder> rotateLeft(int unmodedNumSteps) {
    return (state) -> {
      int numSteps = unmodedNumSteps % state.length();
      String leftSide = state.substring(0, numSteps);
      state.replace(0, numSteps, "");
      state.append(leftSide);
    };
  }

  private Consumer<StringBuilder> rotateRight(String spec) {
    return rotateRight(parseIndex(spec, "rotate right "));
  }

  private Consumer<StringBuilder> rotateRight(int unmodedNumSteps) {
    return (state) -> {
      int numSteps = unmodedNumSteps % state.length();
      String rightSide = state.substring(state.length() - numSteps);
      state.replace(state.length() - numSteps, state.length(), "");
      state.insert(0, rightSide);
    };
  }

  private Consumer<StringBuilder> rotateBased(String spec) {
    String basedOn = spec.substring(spec.length() - 1);
    return (state) -> {
      int indexOf = state.indexOf(basedOn);
      rotateRight(1 + indexOf + ((indexOf >= 4) ? 1 : 0)).accept(state);
    };
  }

  private Consumer<StringBuilder> move(String spec) {
    int index1 = parseIndex(spec, "move position ");
    int index2 = Integer.parseInt(spec.substring(spec.length() - 1));
    return move(index1, index2);
  }

  private Consumer<StringBuilder> move(int index1, int index2) {
    return (state) -> {
      String moving = state.substring(index1, index1 + 1);
      state.replace(index1, index1 + 1, "");
      state.insert(index2, moving);
    };
  }

  private String parseVal(String spec, String prefix) {
    int pos = prefix.length();
    return spec.substring(pos, pos + 1);
  }

  private int parseIndex(String spec, String prefix) {
    return Integer.parseInt(parseVal(spec, prefix));
  }
}
