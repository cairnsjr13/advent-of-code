package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.SafeAccessor;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.ToIntFunction;

/**
 * Centralization of the various tablet code programs used in various days of this year.
 */
class TabletCode extends SafeAccessor {
  /**
   * Parses an input {@link TabletCode} instruction into its action.
   */
  static ToIntFunction<State> parse(String spec) {
    String[] parts = spec.split(" ");
    ToIntFunction<State> action = parseAction(parts);
    return (state) -> {
      state.instCalls.add(parts[0]);
      return action.applyAsInt(state);
    };
  }

  /**
   * Executes the given program against the given state.  The program will terminate
   * if the instruction pointer ever jumps outside the bounds of the instruction list.
   */
  static void executeState(List<ToIntFunction<State>> insts, State state) {
    while ((0 <= state.instNum) && (state.instNum < insts.size())) {
      state.instNum += insts.get(state.instNum).applyAsInt(state);
    }
  }

  /**
   * Creates an appropriate state mutation action based on the parsed parts of the instruction:
   *   - snd: sends the value of [1] value/regRev
   *   - rcv: receives a sent value into the [1] regRef
   *   - set: sets the register at [1] to be the value/regRef at [2]
   *   - add: sets the register at [1] to be the result of adding the register at [1] to the value/regRef at [2]
   *   - sub: sets the register at [1] to be the result of subtracting the value/regRef at [2] from the register at [1]
   *   - mul: sets the register at [1] to be the result of multiplying the register at [1] to the value/regRef at [2]
   *   - mod: sets the register at [1] to be the result of %-ing the register at [1] with the value/regRef at [2]
   *   - jgz: if the value/regRef at [1] is >0, jumps [2] value/regRef (neg allowed) instructions.  1 otherwise
   *   - jnz: if the value/regRef at [1] is !0, jumps [2] value/regRef (neg allowed) instructions.  1 otherwise
   */
  private static ToIntFunction<State> parseAction(String[] parts) {
    switch (parts[0]) {
      case "snd" : return send(parts[1]);
      case "rcv" : return receive(parts[1]);
      case "set" : return simpleInc((state) -> state.set(parts[1].charAt(0), state.getValue(parts[2])));
      case "add" : return op(parts, Math::addExact);
      case "sub" : return op(parts, Math::subtractExact);
      case "mul" : return op(parts, Math::multiplyExact);
      case "mod" : return op(parts, (l, r) -> l % r);
      case "jgz" : return (state) -> (state.getValue(parts[1]) > 0) ? (int) state.getValue(parts[2]) : 1;
      case "jnz" : return (state) -> (state.getValue(parts[1]) != 0) ? (int) state.getValue(parts[2]) : 1;
      default : throw new RuntimeException(Arrays.toString(parts));
    }
  }

  /**
   * Creates an action to send the given value/regRef to the {@link State#otherState}'s recv queue and notifies it.
   */
  private static ToIntFunction<State> send(String register) {
    return simpleInc((state) -> {
      state.otherState.recv.offer(state.getValue(register));
      state.otherState.sndRcvSemaphore.release();
    });
  }

  /**
   * Creates an action to receive a value into the given regRef that was sent by a {@link State#otherState}.
   * If the given State is configures to stopOnRcv, the instruction pointer will be immediately jumped to an invalid position.
   * To synchronize communication between the states, we acquire a lock over our rcv system to check for values.  The state is
   * marked as isWaiting when it has no values to receive to allow the other state to detect when we are done.
   * TODO: the synchronization scheme could be cleaner.
   */
  private static ToIntFunction<State> receive(String register) {
    ToIntFunction<State> receiveAction = simpleInc((state) -> state.set(register.charAt(0), state.recv.remove()));
    return (state) -> {
      if (state.stopOnRcv) {
        return Integer.MIN_VALUE;
      }
      if (!state.recv.isEmpty()) {  // optimization to avoid lock (30-40%)
        return receiveAction.applyAsInt(state);
      }
      while (true) {
        state.rcvWaitingLock.lock();
        try {
          state.isWaiting = true;
          state.sndRcvSemaphore.drainPermits();
          if (!state.recv.isEmpty()) {
            state.isWaiting = false;
            return receiveAction.applyAsInt(state);
          }
          else if (state.otherState.isWaiting && state.otherState.recv.isEmpty()) {
            state.otherState.sndRcvSemaphore.release();
            return Integer.MIN_VALUE;
          }
        }
        finally {
          state.rcvWaitingLock.unlock();
        }
        state.sndRcvSemaphore.acquireUninterruptibly();
      }
    };
  }

  /**
   * Creates an action that increments the instruction pointer by one after the given state mutation.
   */
  private static ToIntFunction<State> simpleInc(Consumer<State> incAfter) {
    return (state) -> {
      incAfter.accept(state);
      return 1;
    };
  }

  /**
   * Creates an action that increments the instruction pointer by one after the given math operation.
   * The [1] register will be set to the new value after the math operation has been performed with
   * [1] register as its first arg and the value/regRef [2] as its second arg.
   * The part at [1] MUST be a register, while [2] can be a value or a register.
   */
  private static ToIntFunction<State> op(String[] parts, LongBinaryOperator op) {
    return simpleInc((state) -> state.set(
        parts[1].charAt(0),
        op.applyAsLong(state.getValue(parts[1]), state.getValue(parts[2]))
    ));
  }

  /**
   * State object that tracks the program's execution and an optional sister {@link #otherState}.
   */
  static class State {
    private final boolean stopOnRcv;
    private final Lock rcvWaitingLock;
    private final Semaphore sndRcvSemaphore = new Semaphore(0);
    private boolean isWaiting = false;
    private final long[] registers = new long[26];
    private int instNum = 0;
    State otherState;
    final Multiset<String> instCalls = HashMultiset.create();
    final BlockingDeque<Long> recv = new LinkedBlockingDeque<>();

    State(boolean stopOnRcv) {
      this.stopOnRcv = stopOnRcv;
      this.rcvWaitingLock = null;
    }

    State(Lock rcvWaitingLock, Map<Character, Integer> inits) {
      this.stopOnRcv = false;
      this.rcvWaitingLock = rcvWaitingLock;
      inits.forEach((register, init) -> registers[register - 'a'] = init);
    }

    /**
     * Returns the current value of the register denoted by the given character (a-z).
     */
    long registerValue(char register) {
      return registers[register - 'a'];
    }

    /**
     * Sets the given value to the register denoted by the given character (a-z).
     */
    private void set(char register, long value) {
      registers[register - 'a'] = value;
    }

    /**
     * Helper function to return the value of the given instruction part that is allowed to be a raw value or regRef.
     */
    private long getValue(String instPart) {
      return (Character.isAlphabetic(instPart.charAt(0)))
          ? registerValue(instPart.charAt(0))
          : Long.parseLong(instPart);
    }
  }
}
