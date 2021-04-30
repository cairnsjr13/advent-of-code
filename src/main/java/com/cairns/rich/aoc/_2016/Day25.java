package com.cairns.rich.aoc._2016;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.cairns.rich.aoc._2016.AssemBunny.Inst;

class Day25 extends Base2016 {
  private static final List<Integer> expectedSeed = List.of(0, 1, 0, 1, 0, 1, 0, 1, 0, 1);
  
  @Override
  protected void run() {
    List<Inst> insts = fullLoader.ml(Inst::new);
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
            while (output.take() != Integer.MIN_VALUE) ;
          })
      );
      if (success.get()) {
        System.out.println(init);
        break;
      }
    }
  }
}
