package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * A 3-bit computer needs to be simulated.  Direct simulation will work for part1, but we need
 * to understand what the program is doing to complete part2 in a reasonable amount of time.
 */
class Day17 extends Base2024 {
  private static final int A = 0;
  private static final int B = 1;
  private static final int C = 2;
  private static final Predicate<List<Integer>> UNTIL_DONE = (output) -> true;
  private static final Map<Integer, Operation> opsWithoutOutput = buildOperationsWithoutOutput();

  /**
   * Runs the input program {@link #UNTIL_DONE} and returns the output values joined by commas.
   */
  @Override
  protected Object part1(Loader loader) throws Throwable {
    List<String> lines = loader.ml();
    long startA = Long.parseLong(lines.get(0).split(" +")[2]);
    List<Integer> program = Arrays.stream(lines.get(lines.size() - 1).split(" ")[1].split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());
    return runWhile(program, startA, UNTIL_DONE).stream()
        .map((i) -> Integer.toString(i))
        .collect(Collectors.joining(","));
  }

  /**
   * The program in our input can be decompiled to effectively this pseudo-code:
   * <pre>
   *   A = aStart
   *   while (A != 0) {
   *     B = A & 0b111
   *     B = B ^ 0b011
   *     C = A >> B
   *     B = B ^ 0b101
   *     A = A >> 3
   *     B = B ^ C
   *     OUT(B & 0b111)
   *   }
   * </pre>
   * This program's behavior is entirely dependent on the value in register A.  As long as it has not reached
   * 0 it will continue looping where each loop "consumes" 3 bits of the value and right shifts it down.  For
   * each loop, it outputs some number based on a range of A's bits.  It outputs the 3 lowest bits of B (shown
   * by the fact that it is & with 0b111).  B is dependent upon C which is in turn dependent on B.  We need
   * to figure out which bits of A are relevant to each.  The initialization of B is limited to 3 bits (A & 0b111).
   * This means B has a value [0-7].  C's final value is only relevant in its 3 lowest bits (since B is eventually
   * & with 0b111).  C is simply A right shifted by B.  So C can be A's bits from [0-2] through [7-9].  This means
   * that A's value is relevant to B from [0-9] (the lowest 10 bits).
   * <p></p>
   * This is important to understand because the input program is 16 values long, meaning that to replicate itself,
   * it must output 16 values.  In order to do this, there must be 16, 3-bit values consumed.  We know what each
   * loop of the program must output as it is just one of these numbers.  We can loop through each position [0-16]
   * and figure out which 10-bit pattern results in that value.  There will be a number of them.  We can recursively
   * try each combination of these 10-bit values (ensuring the next 3-bit value is properly prefixed into the prev).
   * <p></p>
   * This will produce a number of "answers".  However, it is possible for programs to output MORE than 16 numbers.
   * We need to test each one to ensure it results in EXACTLY the 16 we are looking for.  Once we have THOSE answers
   * we can simply map those back to a long and return the minimum to get the answer.
   */
  @Override
  protected Object part2(Loader loader) throws Throwable {
    List<String> lines = loader.ml();
    String programLine = lines.get(lines.size() - 1);
    if (!md5(programLine).equals("d762469f475a95c80903f6b1341da253")) {
      throw fail("Part 2 only makes sense with the program in the full input.");
    }

    List<Integer> program = Arrays.stream(programLine.split(" ")[1].split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());
    Multimap<Long, String> needToTenBits = buildNeedToTenBits(program);
    List<String> answers = new ArrayList<>();
    for (String option : needToTenBits.get(program.get(0).longValue())) {
      tryAllOptions(answers, program, needToTenBits, 1, option);
    }
    return answers.stream()
        .mapToLong((binary) -> Long.parseLong(binary, 2))
        .filter((decimal) -> program.equals(runWhile(program, decimal, UNTIL_DONE)))
        .min().getAsLong();
  }

  /**
   * Computes a mapping from first output to all initial start A's thats result in that
   * output.  The input requires 10 bits of significance for each loop, so we need to know
   * all of the possible initial start A's that could possibly result in the desired output.
   */
  private Multimap<Long, String> buildNeedToTenBits(List<Integer> program) {
    Multimap<Long, String> needToTenBits = HashMultimap.create();
    for (long aStart = 0; aStart <= 0b1111111111; ++aStart) {
      needToTenBits.put(
          runWhile(program, aStart, (output) -> output.isEmpty()).get(0).longValue(),
          StringUtils.leftPad(Long.toBinaryString(aStart), 10, '0')
      );
    }
    return needToTenBits;
  }

  /**
   * Recursive method to find all binary string answers that result in the needed output (the original program).
   * This is done by recursing all valid options that could result in the next number.  An answer is considered
   * when we reach the end of the recursion chain without failing.  It is important to verify the answers returned
   * by this method as some "answers" might actually produce MORE numbers than necessary since this short circuits.
   */
  private void tryAllOptions(
      List<String> answers,
      List<Integer> program,
      Multimap<Long, String> needToTenBits,
      int index,
      String soFar
  ) {
    if (index == program.size()) {
      answers.add(soFar);
      return;
    }
    needToTenBits.get(program.get(index).longValue()).stream()
        .filter((option) -> soFar.startsWith(option.substring(3)))
        .forEach((option) -> tryAllOptions(answers, program, needToTenBits, index + 1, option.substring(0, 3) + soFar));
  }

  /**
   * Execution method that runs the given input program with given initial start A value until the keepGoing
   * predicate returns false (or the instruction pointer exits the program).  Will return the output list
   * produced by the program.  Note that the out {@link Operation} uses a local output list closure.
   */
  private List<Integer> runWhile(List<Integer> program, long startA, Predicate<List<Integer>> keepGoing) {
    long[] registers = { startA, 0, 0 };
    List<Integer> output = new ArrayList<>();
    Map<Integer, Operation> ops = new HashMap<>(opsWithoutOutput);
    ops.put(/*out*/ 5, Operation.incOp((regs, operand) -> output.add((int) (combo(regs, operand) & 0b111))));
    for (int instPtr = 0; (0 <= instPtr) && (instPtr < program.size()) && keepGoing.test(output); ) {
      int opCode = program.get(instPtr + 0);
      int operand = program.get(instPtr + 1);
      instPtr = ops.get(opCode).applyAndGetNewInstPtr(instPtr, registers, operand);
    }
    return output;
  }

  /**
   * Factory method to create standard operations for the program.
   * <pre>
   *   - 0 (adv) = a division -> register A is right shifted by the combo value of the operand
   *   - 1 (bxl) = b xor -> register B is set to the xor value of itself and the direct value of the operand
   *   - 2 (bst) = b to mod -> register B is set to the combo value of the operand mod 8 (3 LSBs)
   *   - 3 (jnz) = jump non-0 -> if register A is non-zero, jumps instruction pointer to direct value of the operand
   *   - 4 (bxc) = b xor c -> register B is set to the xor value of itself and register C
   *   - 6 (bdv) = b division -> register B is right shifted by the combo value of the operand
   *   - 7 (cdv) = c division -> register C is right shifted by the combo value of the operand
   * </pre>
   * NOTE: opcode 5 (output) is not included in this map as it needs access to an output array through closure.
   */
  private static Map<Integer, Operation> buildOperationsWithoutOutput() {
    return Map.of(
        /*adv*/ 0, Operation.incOp((registers, operand) -> registers[A] = registers[A] >> combo(registers, operand)),
        /*bxl*/ 1, Operation.incOp((registers, operand) -> registers[B] = registers[B] ^ operand),
        /*bst*/ 2, Operation.incOp((registers, operand) -> registers[B] = combo(registers, operand) & 0b111),
        /*jnz*/ 3, (instPtr, registers, operand) -> (registers[A] != 0) ? operand : instPtr + 2,
        /*bxc*/ 4, Operation.incOp((registers, operand) -> registers[B] = registers[B] ^ registers[C]),
        /*bdv*/ 6, Operation.incOp((registers, operand) -> registers[B] = registers[A] >> combo(registers, operand)),
        /*cdv*/ 7, Operation.incOp((registers, operand) -> registers[C] = registers[A] >> combo(registers, operand))
    );
  }

  /**
   * Returns the "combo" operand value that corresponds to the given operand and registers.
   *   - An operand value [0-3] is simply the operand value.
   *   - An operand value [4-6] is the value in registers[operand - 4].
   *   - Operand values (,0)-[7,) are invalid.
   */
  private static long combo(long[] registers, int operand) {
    if ((operand < 0) || (7 <= operand)) {
      throw fail("Invalid combo operand " + operand);
    }
    return (operand <= 3) ? operand : registers[operand - 4];
  }

  /**
   * Interface describing the action and instruction pointer side effect of a program operation.
   */
  private interface Operation {
    /**
     * Action corresponding to this operation based on the given registers and operand.
     * The first param is the instruction point of this operation and the return should be
     * the new value of the instruction pointer AFTER the operation has finished executing.
     */
    int applyAndGetNewInstPtr(int instPtr,  long[] registers, int operand);

    /**
     * Convenience factory method to create a simple {@link Operation} that executes the
     * given action and then increments the instruction pointer by 2 (opCode and operand).
     */
    private static Operation incOp(BiConsumer<long[], Integer> action) {
      return (instPtr, registers, operand) -> {
        action.accept(registers, operand);
        return instPtr + 2;
      };
    }
  }
}
