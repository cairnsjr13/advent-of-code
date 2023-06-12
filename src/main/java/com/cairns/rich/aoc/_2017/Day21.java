package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * We need to run an image enhancement program for an art program.  Depending on the size
 * and pixels in an image we need to flip/rotate sections of it to generate the next version.
 */
public class Day21 extends Base2017 {
  private static final ConfigToken<Integer> numIterations = ConfigToken.of("numIterations", Integer::parseInt);

  /**
   * Computes the number of pixels set after the configured number of iterations.
   */
  @Override
  protected Object part1(Loader loader) {
    return getNumSetAfter(loader);
  }

  /**
   * Computes the number of pixels set after the configured number of iterations.
   */
  @Override
  protected Object part2(Loader loader) {
    return getNumSetAfter(loader);
  }

  /**
   * Returns the number of pixels set after the configured number of iterations with the given input transformations.
   */
  private int getNumSetAfter(Loader loader) {
    Map<State, State> rules = rulesLookup(loader.ml(Rule::new));
    int iterations = loader.getConfig(numIterations);
    State state = new State(".#./..#/###");
    for (int i = 0; i < iterations; ++i) {
      state = iterate(rules, state);
    }
    return state.grid.cardinality();
  }

  /**
   * Performs one translation on the given input state according to the given rules.
   * This is done by chunking the grid appropriately (based on odd/even size) and looking
   * up each chunk's corresponding output.  These are pieced together in the returned state.
   */
  private State iterate(Map<State, State> rules, State state) {
    int chunkSize = (state.size % 2 == 0) ? 2 : 3;
    int numChunks = state.size / chunkSize;
    State next = new State(numChunks * (chunkSize + 1), new BitSet());
    for (int chunkRow = 0; chunkRow < numChunks; ++chunkRow) {
      for (int chunkCol = 0; chunkCol < numChunks; ++chunkCol) {
        State chunk = chunk(state, chunkSize, chunkRow, chunkCol);
        State replace = rules.get(chunk);
        put(next, chunkRow, chunkCol, replace);
      }
    }
    return next;
  }

  /**
   * Fetches the corresponding subchunk from the given large picture state.
   */
  private State chunk(State state, int chunkSize, int chunkRow, int chunkCol) {
    State subState = new State(chunkSize, new BitSet());
    for (int relRow = 0; relRow < chunkSize; ++relRow) {
      for (int relCol = 0; relCol < chunkSize; ++relCol) {
        if (state.grid.get(toI(state.size, chunkRow * chunkSize + relRow, chunkCol * chunkSize + relCol))) {
          subState.grid.set(toI(chunkSize, relRow, relCol));
        }
      }
    }
    return subState;
  }

  /**
   * Places the given subchunk into the given large picture state.
   */
  private void put(State output, int chunkRow, int chunkCol, State chunk) {
    for (int relRow = 0; relRow < chunk.size; ++relRow) {
      for (int relCol = 0; relCol < chunk.size; ++relCol) {
        if (chunk.grid.get(toI(chunk.size, relRow, relCol))) {
          output.grid.set(toI(output.size, chunkRow * chunk.size + relRow, chunkCol * chunk.size + relCol));
        }
      }
    }
  }

  /**
   * Converts the row/col information into a {@link BitSet} index for a grid of the given size.
   */
  private static int toI(int size, int row, int col) {
    return row * size + col;
  }

  /**
   * Consolidates all of the given {@link Rule}s into a common lookup map where ALL
   * input states (required to be distinct) are mapped to their output states.
   */
  private static Map<State, State> rulesLookup(List<Rule> rules) {
    Map<State, State> lookup = new HashMap<>();
    rules.forEach((rule) -> rule.inputs.forEach((input) -> lookup.put(input, rule.output)));
    return lookup;
  }

  /**
   * A container class describing a given input or output state.  This is specified
   * by an edge size (squares) and the state of each pixel in the grid.
   */
  private static class State {
    private final int size;
    private final BitSet grid;

    private State(int size, BitSet grid) {
      this.size = size;
      this.grid = grid;
    }

    private State(String spec) {
      String[] lines = spec.split("/");
      this.size = lines.length;
      this.grid = new BitSet();
      for (int row = 0; row < size; ++row) {
        for (int col = 0; col < size; ++col) {
          if (lines[row].charAt(col) == '#') {
            grid.set(toI(size, row, col));
          }
        }
      }
    }

    @Override
    public boolean equals(Object other) {
      return (size == ((State) other).size)
          && grid.equals(((State) other).grid);
    }

    @Override
    public int hashCode() {
      return (size * 31) + grid.hashCode();
    }
  }

  /**
   * A container class describing all of the possible input states that map to a given output state.
   */
  private static class Rule {
    private final Set<State> inputs = new HashSet<>();
    private final State output;

    private Rule(String spec) {
      int indexOfArrow = spec.indexOf(" => ");
      addAllInputs(spec.substring(0, indexOfArrow));
      this.output = new State(spec.substring(indexOfArrow + " => ".length()));
    }

    /**
     * Precomputes and adds all potential inputs for the given spec.  This is done
     * by adding the described input state and then rotating it 3 times, flipping it,
     * and rotating it three more times to find all possible inputs.
     */
    private void addAllInputs(String spec) {
      State state = new State(spec);
      inputs.add(state);
      state = transformAddAndGet(state, this::rotate);
      state = transformAddAndGet(state, this::rotate);
      state = transformAddAndGet(state, this::rotate);
      state = transformAddAndGet(state, this::flip);
      state = transformAddAndGet(state, this::rotate);
      state = transformAddAndGet(state, this::rotate);
      state = transformAddAndGet(state, this::rotate);
    }

    /**
     * Applies the given transformation to the current state, adds it to the
     * inputs, and returns the newly created state for further transformation.
     */
    private State transformAddAndGet(State cur, Function<State, State> transform) {
      cur = transform.apply(cur);
      inputs.add(cur);
      return cur;
    }

    /**
     * Returns a new state that is the given input state rotated by 90 degrees.
     */
    private State rotate(State input) {
      State output = new State(input.size, new BitSet());
      for (int row = 0; row < input.size; ++row) {
        for (int col = 0; col < input.size; ++col) {
          if (input.grid.get(toI(input.size, row, col))) {
            output.grid.set(toI(output.size, col, output.size - row - 1));
          }
        }
      }
      return output;
    }

    /**
     * Returns a new state that is the given input state flipped horizontally.
     */
    private State flip(State input) {
      State output = new State(input.size, new BitSet());
      for (int row = 0; row < input.size; ++row) {
        int leftI = toI(input.size, row, 0);
        int midI = toI(input.size, row, 1);
        int rightI = toI(input.size, row, input.size - 1);
        output.grid.set(leftI, input.grid.get(rightI));
        output.grid.set(rightI, input.grid.get(leftI));
        if (output.size == 3) {
          output.grid.set(midI, input.grid.get(midI));
        }
      }
      return output;
    }
  }
}
