package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc._2019.IntCode.IO;
import com.cairns.rich.aoc._2019.IntCode.State;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

class Day07 extends Base2019 {
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    System.out.println(getMaxThrust(ConnType.Simple, program));
    System.out.println(getMaxThrust(ConnType.Feedback, program));
  }

  private long getMaxThrust(ConnType connType, List<Long> program) {
    return getMaxThrust(connType, program, new ArrayList<>(connType.phases), new ArrayList<>());
  }

  private long getMaxThrust(
      ConnType connType,
      List<Long> program,
      List<Long> left,
      List<Long> order
  ) {
    if (left.isEmpty()) {
      return connType.getThrust(program, order);
    }
    long maxThrust = 0;
    for (int i = 0; i < left.size(); ++i) {
      long next = left.remove(i);
      order.add(next);
      maxThrust = Math.max(maxThrust, getMaxThrust(connType, program, left, order));
      order.remove(order.size() - 1);
      left.add(i, next);
    }
    return maxThrust;
  }

  private enum ConnType {
    Simple(0, 4, 6, (size) -> size - 1),
    Feedback(5, 9, 5, (size) -> 0);

    private final List<Long> phases;
    private final int numIos;
    private final IntUnaryOperator sizeToResultIoIndex;

    private ConnType(long minPhase, long maxPhase, int numIos, IntUnaryOperator sizeToResultIoIndex) {
      this.phases = LongStream.rangeClosed(minPhase, maxPhase).boxed().collect(Collectors.toList());
      this.numIos = numIos;
      this.sizeToResultIoIndex = sizeToResultIoIndex;
    }

    private long getThrust(List<Long> program, List<Long> order) {
      List<IO> ios = IntStream.range(0, numIos).mapToObj((i) -> new IO()).collect(Collectors.toList());
      List<State> states = IntStream.range(0, 5)
          .mapToObj((i) -> IntCode.run(program, ios.get(i), ios.get((i + 1) % ios.size())))
          .collect(Collectors.toList());
      IntStream.range(0, order.size()).forEach((i) -> ios.get(i).put(order.get(i)));
      ios.get(0).put(0);
      states.forEach(State::blockUntilHalt);
      return ios.get(sizeToResultIoIndex.applyAsInt(ios.size())).take();
    }
  }
}
