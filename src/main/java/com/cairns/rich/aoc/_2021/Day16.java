package com.cairns.rich.aoc._2021;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.LongBinaryOperator;

class Day16 extends Base2021 {
  @Override
  protected void run() {
    BytePositioner input = new BytePositioner(fullLoader.sl());
    Packet rootPacket = parsePacket(input);
    System.out.println(rootPacket.sumOfAllVersions());
    System.out.println(rootPacket.getValue());
  }

  private static Packet parsePacket(BytePositioner input) {
    int version = input.readValue(3);
    int typeId = input.readValue(3);
    return (typeId == 4)
        ? new LiteralPacket(version, input)
        : new OpPacket(version, typeId, input);
  }

  private static class BytePositioner {
    private final byte[] bytes;
    private int position = 0;

    private BytePositioner(String input) {
      this.bytes = new byte[input.length() / 2];
      for (int i = 0; i < input.length(); i += 2) {
        byte msb = Byte.parseByte(input.substring(i, i + 1), 16);
        byte lsb = Byte.parseByte(input.substring(i + 1, i + 2), 16);
        bytes[i / 2] = (byte) ((msb << 4) | (lsb << 0));
      }
    }

    private int readValue(int numBits) {
      int value = 0;
      for (int i = 0; i < numBits; ++i, ++position) {
        int byteIndex = position / 8;
        int bitIndex = position % 8;
        boolean isSet = (bytes[byteIndex] & (1 << (7 - bitIndex))) != 0;
        value = (value << 1) + ((isSet) ? 1 : 0);
      }
      return value;
    }
  }

  private static abstract class Packet {
    protected final int version;

    protected Packet(int version) {
      this.version = version;
    }

    protected abstract int sumOfAllVersions();

    protected abstract long getValue();
  }

  private static class OpPacket extends Packet {
    private static final Map<Integer, LongBinaryOperator> ops = Map.of(
        0, (l, r) -> l + r,
        1, (l, r) -> l * r,
        2, (l, r) -> (l < r) ? l : r,
        3, (l, r) -> (l > r) ? l : r,
        5, (l, r) -> (l > r) ? 1 : 0,
        6, (l, r) -> (l < r) ? 1 : 0,
        7, (l ,r) -> (l == r) ? 1 : 0
    );

    private final LongBinaryOperator op;
    private final List<Packet> packets = new ArrayList<>();

    private OpPacket(int version, int typeId, BytePositioner input) {
      super(version);
      if (!ops.containsKey(typeId)) {
        throw fail("Unknown typeId op: " + typeId);
      }
      this.op = ops.get(typeId);

      int lengthTypeId = input.readValue(1);
      if (lengthTypeId == 0) {
        int numBitsInSubPackets = input.readValue(15);
        int stopAt = input.position + numBitsInSubPackets;
        while (input.position != stopAt) {
          packets.add(parsePacket(input));
        }
      }
      else {
        int numPackets = input.readValue(11);
        for (int i = 0; i < numPackets; ++i) {
          packets.add(parsePacket(input));
        }
      }
    }

    @Override
    protected int sumOfAllVersions() {
      return version + packets.stream().mapToInt(Packet::sumOfAllVersions).sum();
    }

    @Override
    protected long getValue() {
      long value = packets.get(0).getValue();
      for (int i = 1; i < packets.size(); ++i) {
        value = op.applyAsLong(value, packets.get(i).getValue());
      }
      return value;
    }
  }

  private static class LiteralPacket extends Packet {
    private final long value;

    private LiteralPacket(int version, BytePositioner input) {
      super(version);
      long value = 0;
      for (boolean morePieces = true; morePieces; ) {
        morePieces = input.readValue(1) == 1;
        value = (value << 4) + input.readValue(4);
      }
      this.value = value;
    }

    @Override
    protected int sumOfAllVersions() {
      return version;
    }

    @Override
    protected long getValue() {
      return value;
    }
  }
}
