package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Day14 extends Base2020 {
  @Override
  protected Object part1(Loader loader) {
    return runSimulation(loader.ml(Inst::parse), 1);
  }

  @Override
  protected Object part2(Loader loader) {
    return runSimulation(loader.ml(Inst::parse), 2);
  }

  private long runSimulation(List<Inst> insts, int version) {
    State state = new State(version);
    insts.stream().forEach((inst) -> inst.apply(state));
    return state.memory.values().stream().mapToLong(Long::longValue).sum();
  }

  private static class State {
    private final int version;
    private final List<String> masks = new ArrayList<>();
    private final Map<Long, Long> memory = new HashMap<>();

    private State(int version) {
      this.version = version;
      this.masks.add("");
    }
  }

  private interface Inst {
    void apply(State state);

    private static Inst parse(String spec) {
      return (spec.startsWith("mask")) ? new MaskInst(spec) : new SetInst(spec);
    }
  }

  private static class MaskInst implements Inst {
    private final String maskReset;

    private MaskInst(String spec) {
      this.maskReset = spec.substring("mask = ".length());
    }

    @Override
    public void apply(State state) {
      if (state.version == 1) {
        state.masks.set(0, maskReset);
      }
      else {
        state.masks.clear();
        addMasksRec(state.masks, new StringBuilder(), 0);
      }
    }

    private void addMasksRec(List<String> masks, StringBuilder built, int index) {
      if (index == 36) {
        masks.add(built.toString());
      }
      else {
        char ch = maskReset.charAt(index);
        if (ch == '0') {
          recurse(masks, built, index, 'X');
        }
        else {
          if (ch == 'X') {
            recurse(masks, built, index, '0');
          }
          recurse(masks, built, index, '1');
        }
      }
    }

    private void recurse(List<String> masks, StringBuilder built, int index, char ch) {
      built.append(ch);
      addMasksRec(masks, built, index + 1);
      built.deleteCharAt(index);
    }
  }

  private static class SetInst implements Inst {
    private final long addr;
    private final long value;

    private SetInst(String spec) {
      String[] parts = spec.split(" = ");
      this.addr = Long.parseLong(parts[0].substring("mem[".length(), parts[0].length() - 1));
      this.value = Long.parseLong(parts[1]);
    }

    @Override
    public void apply(State state) {
      if (state.version == 1) {
        state.memory.put(addr, convert(state.masks.get(0), value));
      }
      else {
        state.masks.forEach((mask) -> state.memory.put(convert(mask, addr), value));
      }
    }

    private long convert(String mask, long value) {
      long newValue = 0;
      for (int i = 0; i < 36; ++i) {
        int index = 35 - i;
        if (mask.charAt(index) == 'X') {
          if (0 != (value & (1L << i))) {
            newValue = newValue | (1L << i);
          }
        }
        else if (mask.charAt(index) == '1') {
          newValue = newValue | (1L << i);
        }
      }
      return newValue;
    }
  }
}
