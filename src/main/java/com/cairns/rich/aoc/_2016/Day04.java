package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * We found a kiosk with room information.  We need to discard decoy data and decrypt the information.
 */
class Day04 extends Base2016 {
  private static final ConfigToken<String> northPoleSearchTerm = ConfigToken.of("search", Function.identity());

  /**
   * Computes the sum of sector ids from all of the real rooms.
   */
  @Override
  protected Object part1(Loader loader) {
    return loader.ml(Room::new).stream().filter(Room::isReal).mapToInt((r) -> r.sectorId).sum();
  }

  /**
   * Computes the sectorId of the only real room whose decrypted name contains {@link #northPoleSearchTerm}.
   */
  @Override
  protected Object part2(Loader loader) {
    List<Integer> ids = loader.ml(Room::new).stream()
        .filter(Room::isReal)
        .filter((r) -> r.decryptName().contains(loader.getConfig(northPoleSearchTerm)))
        .map((r) -> r.sectorId).collect(Collectors.toList());
    if (ids.size() != 1) {
      throw fail(ids.size());
    }
    return ids.get(0);
  }

  /**
   * Container class to describe a room.
   */
  private static class Room {
    private static final Pattern pattern = Pattern.compile("^(.+)-(\\d{3})\\[(.{5})\\]$");

    private final int sectorId;
    private final String name;
    private final Multiset<Character> nameChars = HashMultiset.create();
    private final String checksum;

    private Room(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.sectorId = Integer.parseInt(matcher.group(2));
      this.name = matcher.group(1);
      name.chars().forEach((ch) -> nameChars.add((char) ch));
      nameChars.setCount('-', 0);
      this.checksum = matcher.group(3);
    }

    /**
     * Returns true if this room is valid and not a decoy.  A room is considered valid if the five most common characters
     * are in the order of the checksum when sorted based on most frequent.  Ties are broken by alphabetic ordering.
     */
    private boolean isReal() {
      List<Character> chs = new ArrayList<>(nameChars.elementSet());
      chs.sort(Comparator.<Character>comparingInt(nameChars::count).reversed().thenComparing(Comparator.naturalOrder()));
      for (int i = 0; i < checksum.length(); ++i) {
        if (chs.get(i) != checksum.charAt(i)) {
          return false;
        }
      }
      return true;
    }

    /**
     * Computes the decrypted name of this room by converting dashes to spaces
     * and shifting each letter by its sector id (wrapping as necessary)
     */
    private String decryptName() {
      StringBuilder str = new StringBuilder();
      for (char ch : name.toCharArray()) {
        if (ch == '-') {
          str.append(' ');
        }
        else {
          int chId = ch - 'a';
          str.append((char) ('a' + ((chId + sectorId) % 26)));
        }
      }
      return str.toString();
    }
  }
}
