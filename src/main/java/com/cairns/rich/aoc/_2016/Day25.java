package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc._2016.AssemBunny.Inst;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * We need to use the antenna on top of the building to transmit a signal.  We need to find the
 * appropriate initial value for register a such that the signal is correct.  This involves
 * adding an "out" command to {@link AssemBunny} that transmits a signal.
 */
class Day25 extends Base2016 {
  private static final List<Integer> expectedSeed = List.of(0, 1, 0, 1, 0, 1, 0, 1, 0, 1);

  /**
   * Returns the minimum initial value of register a that will result in 010101 being repeated forever.
   * We assume that {@link #expectedSeed} holds enough values to be considered "forever".
   */
  @Override
  protected Object part1(Loader loader) {
    List<Inst> insts = loader.ml(Inst::new);
    for (int init = 0; true; ++init) {
      AtomicBoolean killer = new AtomicBoolean();
      Queue<Integer> expected = new LinkedList<>(expectedSeed);
      AtomicBoolean success = new AtomicBoolean(true);
      AssemBunny.execute(
          insts.stream().map(Inst::new).collect(Collectors.toList()),
          Map.of('a', init),
          killer::get,
          (output) -> quietly(() -> {
            while (!expected.isEmpty()) {
              if (output.take() != expected.poll()) {
                success.set(false);
                break;
              }
            }
            killer.set(true);
            AssemBunny.ignore.accept(output);
          })
      );
      if (success.get()) {
        return init;
      }
    }
  }
}
