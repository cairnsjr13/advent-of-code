package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day04 extends Base2016 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    List<Room> rooms = loader.ml(Room::new);
    result.part1(rooms.stream().filter(Room::isReal).mapToInt((r) -> r.sectorId).sum());
    result.part2(findNorthpoleRoomSectorId(rooms, "north"));
  }

  private int findNorthpoleRoomSectorId(List<Room> rooms, String search) {
    List<Integer> ids = rooms.stream()
        .filter(Room::isReal).filter((r) -> r.decryptName().contains(search))
        .map((r) -> r.sectorId).collect(Collectors.toList());
    if (ids.size() != 1) {
      throw fail(ids.size());
    }
    return ids.get(0);
  }

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
