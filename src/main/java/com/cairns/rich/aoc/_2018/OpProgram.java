package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Base.HasId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.LongBinaryOperator;

final class OpProgram {
  static final Map<String, Op> ops;

  static {
    LongBinaryOperator band = (l, r) -> l & r;
    LongBinaryOperator bor = (l, r) -> l | r;
    LongBinaryOperator gt = (l, r) -> (l > r) ? 1 : 0;
    LongBinaryOperator eq = (l, r) -> (l == r) ? 1 : 0;
    ops = Base.getLookup(Arrays.asList(
        Op.rr("addr", Math::addExact),
        Op.ri("addi", Math::addExact),
        Op.rr("mulr", Math::multiplyExact),
        Op.ri("muli", Math::multiplyExact),
        Op.rr("banr", band),
        Op.ri("bani", band),
        Op.rr("borr", bor),
        Op.ri("bori", bor),
        new Op("setr", (regs, inst) -> regs[inst[3]] = regs[inst[1]]),
        new Op("seti", (regs, inst) -> regs[inst[3]] = inst[1]),
        Op.rr("gtrr", gt),
        Op.ri("gtri", gt),
        Op.ir("gtir", gt),
        Op.rr("eqrr", eq),
        Op.ri("eqri", eq),
        Op.ir("eqir", eq)
    ));
  }

  private OpProgram() { }

  static Consumer<long[]> parse(String spec) {
    String[] parts = spec.split(" ");
    Op op = OpProgram.ops.get(parts[0]);
    int[] inst = {
      0,
      Integer.parseInt(parts[1]),
      Integer.parseInt(parts[2]),
      Integer.parseInt(parts[3])
    };
    return (regs) -> op.action.accept(regs, inst);
  }

  static long[] run(int instructionRegister, List<Consumer<long[]>> instructions) {
    return run(instructionRegister, instructions, (instructionPtr, registers) -> false);
  }

  static long[] run(
      int instructionRegister,
      List<Consumer<long[]>> instructions,
      BiPredicate<Integer, long[]> shouldHalt
  ) {
    long[] registers = new long[6];
    IntPredicate shouldContinue =
        (instPtr) -> (0 <= instPtr) && (instPtr < instructions.size()) && !shouldHalt.test(instPtr, registers);
    for (int instPtr = 0; shouldContinue.test(instPtr); ++instPtr) {
      registers[instructionRegister] = instPtr;
      instructions.get(instPtr).accept(registers);
      instPtr = (int) registers[instructionRegister];
    }
    return registers;
  }

  static class Op implements HasId<String> {
    final String name;
    final BiConsumer<long[], int[]> action;

    private Op(String name, BiConsumer<long[], int[]> action) {
      this.name = name;
      this.action = action;
    }

    @Override
    public String getId() {
      return name;
    }

    private static Op rr(String name, LongBinaryOperator operation) {
      return new Op(name, (regs, inst) -> regs[inst[3]] = operation.applyAsLong(regs[inst[1]], regs[inst[2]]));
    }

    /**
     * Note: Cannot be used for setr because the second access is never used and may be out of bounds.
     */
    private static Op ri(String name, LongBinaryOperator operation) {
      return new Op(name, (regs, inst) -> regs[inst[3]] = operation.applyAsLong(regs[inst[1]], inst[2]));
    }

    /**
     * Note: Cannot be used for seti because the second access is never used and may be out of bounds.
     */
    private static Op ir(String name, LongBinaryOperator operation) {
      return new Op(name, (regs, inst) -> regs[inst[3]] = operation.applyAsLong(inst[1], regs[inst[2]]));
    }
  }
}
