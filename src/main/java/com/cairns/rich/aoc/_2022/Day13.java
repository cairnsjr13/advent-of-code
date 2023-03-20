package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day13 extends Base2022 {
  @Override
  protected Object part1(Loader2 loader) {
    List<List<ListPacket>> packetPairs =
        loader.gDelim("", (l) -> l.stream().map(ListPacket::parse).collect(Collectors.toList()));
    return IntStream.range(0, packetPairs.size())
        .filter((i) -> {
          List<ListPacket> packetPair = packetPairs.get(i);
          return packetPair.get(0).compareTo(packetPair.get(1)) <= 0;
        }).map((i) -> i + 1)
        .sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<ListPacket> packets = loader.gDelim("", (l) -> l.stream().map(ListPacket::parse).collect(Collectors.toList()))
        .stream().flatMap(List::stream).collect(Collectors.toList());
    Set<ListPacket> dividers = Set.of(ListPacket.parse("[[2]]"), ListPacket.parse("[[6]]"));
    packets.addAll(dividers);
    Collections.sort(packets);
    return IntStream.range(0, packets.size())
        .filter((i) -> dividers.contains(packets.get(i)))
        .map((i) -> i + 1)
        .reduce(1, Math::multiplyExact);
  }

  private static interface Packet extends Comparable<Packet> { }

  private static final class NumPacket implements Packet {
    private final int num;

    private NumPacket(String str) {
      this.num = Integer.parseInt(str);
    }

    @Override
    public int compareTo(Packet other) {
      return (other instanceof ListPacket)
          ? ListPacket.wrap(this).compareTo(other)
          : Integer.compare(num, ((NumPacket) other).num);
    }
  }

  private static final class ListPacket implements Packet {
    private final List<Packet> subPackets = new ArrayList<>();

    @Override
    public int compareTo(Packet other) {
      if (other instanceof NumPacket) {
        return compareTo(ListPacket.wrap((NumPacket) other));
      }
      ListPacket otherList = (ListPacket) other;
      int minSize = Math.min(subPackets.size(), otherList.subPackets.size());
      for (int i = 0; i < minSize; ++i) {
        int cmp = subPackets.get(i).compareTo(otherList.subPackets.get(i));
        if (cmp != 0) {
          return cmp;
        }
      }
      return Integer.compare(subPackets.size(), otherList.subPackets.size());
    }

    private static ListPacket wrap(NumPacket numPacket) {
      ListPacket wrapped = new ListPacket();
      wrapped.subPackets.add(numPacket);
      return wrapped;
    }

    private static ListPacket parse(String line) {
      Stack<ListPacket> stack = new Stack<>();
      stack.add(new ListPacket());
      for (int i = 0; i < line.length(); ++i) {
        char ch = line.charAt(i);
        if (ch == '[') {
          ListPacket next = new ListPacket();
          stack.peek().subPackets.add(next);
          stack.push(next);
        }
        else if (ch == ']') {
          stack.pop();
        }
        else if (ch != ',') {
          int j = i;
          while ((j < line.length()) && Character.isDigit(line.charAt(j))) {
            ++j;
          }
          stack.peek().subPackets.add(new NumPacket(line.substring(i, j)));
          i = j - 1;
        }
      }
      return (ListPacket) stack.pop().subPackets.get(0);
    }
  }
}
