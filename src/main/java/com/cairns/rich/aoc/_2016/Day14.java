package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.LinkedList;
import java.util.Optional;

/**
 * We need to generate a one-time pad using md5 hashing.  Secure communication with Santa!
 */
class Day14 extends Base2016 {
  private static final ConfigToken<Integer> targetKeyNumToken = ConfigToken.of("targetKeyNum", Integer::parseInt);

  /**
   * Computes the index of the configured target key number with no key stretching.
   */
  @Override
  protected Object part1(Loader loader) {
    return findIndexOfTargetKeyNum(loader, 0);
  }

  /**
   * Computes the index of the configured target key number with 2016 extra key stretchings.
   */
  @Override
  protected Object part2(Loader loader) {
    return findIndexOfTargetKeyNum(loader, 2016);
  }

  /**
   * Finds the overall index of the target key number configured with the input salt.
   * A key is considered valid if it has a triple AND at least one of the following 1000
   * hashes has that same character repeated 5 times in a row.
   */
  private int findIndexOfTargetKeyNum(Loader loader, int numExtraHashings) {
    String salt = loader.sl();
    int targetKeyNum = loader.getConfig(targetKeyNumToken);
    State state = new State(salt, numExtraHashings);
    int hashIndex = -1;
    for (int i = 0; i < targetKeyNum; ++i) {
      while (true) {
        state.ensureEnoughHashes();
        String hash = state.hashes.removeFirst();
        ++hashIndex;
        Optional<Character> firstTriple = findFirstTriple(hash);
        if (firstTriple.isPresent() && windowHasRepeat5(state, firstTriple.get())) {
          break;
        }
      }
    }
    return hashIndex;
  }

  /**
   * Returns the character representing the first triple repeated character in the given hash.
   * Returns {@link Optional#empty()} if none are found.
   */
  private Optional<Character> findFirstTriple(String hash) {
    for (int i = 0; i < hash.length() - 2; ++i) {
      char ch = hash.charAt(i);
      if ((ch == hash.charAt(i + 1)) && (ch == hash.charAt(i + 2))) {
        return Optional.of(ch);
      }
    }
    return Optional.empty();
  }

  /**
   * Returns true if the window of hashes contains any hashes that have 5 of the given character in a row.
   */
  private boolean windowHasRepeat5(State state, char ch) {
    return state.hashes.stream().anyMatch((hash) -> hasRepeat5(hash, ch));
  }

  /**
   * Returns true if the given hash has 5 of the given character in a row.
   */
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

  /**
   * Container object that is responsible for ensuring we have a properly sized window of hashes to consider
   * based upon the configured salt and current hash key.  We use md5 hashing to compute the hash keys.
   */
  private static class State {
    private final String salt;
    private final int numExtraHashings;
    private int hashIndexToGen = 0;
    private final LinkedList<String> hashes = new LinkedList<>();

    private State(String salt, int numExtraHashings) {
      this.salt = salt;
      this.numExtraHashings = numExtraHashings;
    }

    /**
     * Ensures there are 1001 md5 hashes in the window so we can verify the requirements.
     */
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
