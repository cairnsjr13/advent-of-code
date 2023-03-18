package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import java.util.LinkedList;

class Day14 extends Base2016 {
  @Override
  protected Object part1(Loader2 loader) {
    return getIndexOf64th(loader.sl(), 0);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getIndexOf64th(loader.sl(), 2016);
  }

  private int getIndexOf64th(String salt, int numExtraHashings) {
    State state = new State(salt, numExtraHashings);
    int hashIndex = -1;
    for (int i = 0; i < 64; ++i) {
      while (true) {
        state.ensureEnoughHashes();
        String hash = state.hashes.removeFirst();
        ++hashIndex;
        if (state.hashes.size() != 1000) throw new RuntimeException("" + state.hashes.size());
        Character firstTriple = findFirstTriple(hash);
        if ((firstTriple != null) && (windowHasRepeat5(state, firstTriple))) {
          break;
        }
      }
    }
    return hashIndex;
  }

  private Character findFirstTriple(String hash) {
    for (int i = 0; i < hash.length() - 2; ++i) {
      char ch = hash.charAt(i);
      if ((ch == hash.charAt(i + 1)) && (ch == hash.charAt(i + 2))) {
        return ch;
      }
    }
    return null;
  }

  private boolean windowHasRepeat5(State state, char ch) {
    for (String hash : state.hashes) {
      if (hasRepeat5(hash, ch)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasRepeat5(String hash, char ch) {
    for (int i = 0; i < hash.length() - 4; ++i) {
      if ((ch == hash.charAt(i + 0)) && (ch == hash.charAt(i + 1)) && (ch == hash.charAt(i + 2)) &&
          (ch == hash.charAt(i + 3)) && (ch == hash.charAt(i + 4)))
      {
        return true;
      }
    }
    return false;
  }

  private static class State {
    private final String salt;
    private final int numExtraHashings;
    private int hashIndexToGen = 0;
    private final LinkedList<String> hashes = new LinkedList<>();

    private State(String salt, int numExtraHashings) {
      this.salt = salt;
      this.numExtraHashings = numExtraHashings;
    }

    private void ensureEnoughHashes() {
      while (hashes.size() < 1001) {
        String hash = md5(salt + hashIndexToGen);
        for (int i = 0; i < numExtraHashings; ++i) {
          hash = md5(hash);
        }
        hashes.add(hash);
        ++hashIndexToGen;
      }
    }
  }
}
