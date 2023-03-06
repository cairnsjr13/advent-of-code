package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.Base.HasId;
import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.QuietCapable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

final class IntCode extends QuietCapable {
  private static final ExecutorService exec = Executors.newCachedThreadPool((r) -> {
    Thread thread = new Thread(r);
    thread.setDaemon(true);
    return thread;
  });
  private static final Map<Long, ParamMode> paramModeLookup = EnumUtils.getLookup(ParamMode.class);

  private IntCode() { }

  static List<Long> parseProgram(Loader2 loader) {
    return loader.sl(",", Long::parseLong);
  }

  static State run(List<Long> program) {
    return run(program, new IO(), new IO());
  }

  static State run(List<Long> program, IO programInput, IO programOutput) {
    return new State(program, programInput, programOutput, (state) -> {
      while (true) {
        long opCode = state.getMem(state.instPtr) % 100;
        if (opCode == 99) {
          break;
        }
        else if (opCode == 1) {
          long left = ParamMode.deref(DerefType.Read, state, 1);
          long right = ParamMode.deref(DerefType.Read, state, 2);
          long dest = ParamMode.deref(DerefType.Write, state, 3);
          state.setMem(dest, left + right);
          state.instPtr += 4;
        }
        else if (opCode == 2) {
          long left = ParamMode.deref(DerefType.Read, state, 1);
          long right = ParamMode.deref(DerefType.Read, state, 2);
          long dest = ParamMode.deref(DerefType.Write, state, 3);
          state.setMem(dest, left * right);
          state.instPtr += 4;
        }
        else if (opCode == 3) {
          state.waitingOnInput.release();
          long value = programInput.take();
          long dest = ParamMode.deref(DerefType.Write, state, 1);
          state.setMem(dest, value);
          state.instPtr += 2;
        }
        else if (opCode == 4) {
          long value = ParamMode.deref(DerefType.Read, state, 1);
          programOutput.put(value);
          state.instPtr += 2;
        }
        else if (opCode == 5) {
          long value = ParamMode.deref(DerefType.Read, state, 1);
          state.instPtr = (value != 0)
              ? ParamMode.deref(DerefType.Read, state, 2)
              : state.instPtr + 3;
        }
        else if (opCode == 6) {
          long value = ParamMode.deref(DerefType.Read, state, 1);
          state.instPtr = (value == 0)
              ? ParamMode.deref(DerefType.Read, state, 2)
              : state.instPtr + 3;
        }
        else if (opCode == 7) {
          long left = ParamMode.deref(DerefType.Read, state, 1);
          long right = ParamMode.deref(DerefType.Read, state, 2);
          long dest = ParamMode.deref(DerefType.Write, state, 3);
          state.setMem(dest, (left < right) ? 1 : 0);
          state.instPtr += 4;
        }
        else if (opCode == 8) {
          long left = ParamMode.deref(DerefType.Read, state, 1);
          long right = ParamMode.deref(DerefType.Read, state, 2);
          long dest = ParamMode.deref(DerefType.Write, state, 3);
          state.setMem(dest, (left == right) ? 1 : 0);
          state.instPtr += 4;
        }
        else if (opCode == 9) {
          long adjust = ParamMode.deref(DerefType.Read, state, 1);
          state.relativeBase += adjust;
          state.instPtr += 2;
        }
        else {
          throw new RuntimeException(state.instPtr + " - " + opCode);
        }
      }
    });
  }

  static final class IO {
    final LinkedBlockingQueue<Long> queue = new LinkedBlockingQueue<>();

    void put(long value) {
      quietly(() -> queue.put(value));
    }

    long take() {
      return quietly(queue::take);
    }

    boolean hasMoreToTake() {
      return !queue.isEmpty();
    }

    @Override
    public String toString() {
      return queue.toString();
    }
  }

  static class State {
    private long instPtr = 0;
    private final Map<Long, Long> mem = new HashMap<>();
    private long relativeBase = 0;
    private final Future<?> future;
    private final Semaphore waitingOnInput = new Semaphore(0);
    final IO programInput;
    final IO programOutput;

    private State(List<Long> program, IO programInput, IO programOutput, Consumer<State> runner) {
      for (int i = 0; i < program.size(); ++i) {
        mem.put((long) i, program.get(i));
      }
      this.programInput = programInput;
      this.programOutput = programOutput;
      this.future = exec.submit(() -> runner.accept(this));
    }

    boolean hasHalted() {
      return future.isDone();
    }

    // TODO: This is wrong, we need to make sure the input is EMPTY and still have permits
    boolean isWaitingForInput() {
      return waitingOnInput.tryAcquire();
    }

    void blockUntilHalt() {
      while (!hasHalted()) {
        Thread.yield();
      }
    }

    void blockUntilWaitForInput() {
      while (!isWaitingForInput()) {
        Thread.yield();
      }
    }

    void blockUntilHaltOrWaitForInput() {
      while (!hasHalted() && !isWaitingForInput()) {
        Thread.yield();
      }
    }

    void blockUntilHaltOrOutput() {
      while (!hasHalted() && !programOutput.hasMoreToTake()) {
        Thread.yield();
      }
    }

    long getMem(long pos) {
      return mem.getOrDefault(pos, 0L);
    }

    private void setMem(long pos, long value) {
      mem.put(pos, value);
    }
  }

  private enum ParamMode implements HasId<Long> {
    Position(0) {
      @Override
      protected long getValue(State state, long param) {
        return state.getMem(param);
      }

      @Override
      protected long getWriteLocation(State state, long param) {
        return param;
      }
    },
    Immediate(1) {
      @Override
      protected long getValue(State state, long param) {
        return param;
      }

      @Override
      protected long getWriteLocation(State state, long param) {
        throw new RuntimeException("Unsupported: " + param);
      }
    },
    Relative(2) {
      @Override
      protected long getValue(State state, long param) {
        return state.getMem(state.relativeBase + param);
      }

      @Override
      protected long getWriteLocation(State state, long param) {
        return state.relativeBase + param;
      }
    };

    private final long id;

    private ParamMode(long id) {
      this.id = id;
    }

    @Override
    public Long getId() {
      return id;
    }

    protected abstract long getValue(State state, long param);

    protected abstract long getWriteLocation(State state, long param);

    private static long deref(DerefType derefType, State state, int paramIndex) {
      long param = state.getMem(state.instPtr + paramIndex);
      ParamMode paramMode =
          paramModeLookup.get((state.getMem(state.instPtr) / (int) Math.pow(10, paramIndex + 1)) % 10);
      return (derefType == DerefType.Read)
          ? paramMode.getValue(state, param)
          : paramMode.getWriteLocation(state, param);
    }
  }

  private enum DerefType {
    Read,
    Write;
  }
}
