package com.cairns.rich.aoc._2017;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Day21 extends Base2017 {
  @Override
  protected void run() {
    Map<State, State> rules = Rule.lookup(fullLoader.ml(Rule::new));
    System.out.println(getNumSetAfter(rules, 5));
    System.out.println(getNumSetAfter(rules, 18));
  }
  
  private int getNumSetAfter(Map<State, State> rules, int iterations) {
    State state = new State(".#./..#/###");
    for (int i = 0; i < iterations; ++i) {
      state = iterate(rules, state);
    }
    return state.grid.cardinality();
  }
  
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
  
  private void put(State output, int chunkRow, int chunkCol, State chunk) {
    for (int relRow = 0; relRow < chunk.size; ++relRow) {
      for (int relCol = 0; relCol < chunk.size; ++relCol) {
        if (chunk.grid.get(toI(chunk.size, relRow, relCol))) {
          output.grid.set(toI(output.size, chunkRow * chunk.size + relRow, chunkCol * chunk.size + relCol));
        }
      }
    }
  }
  
  private static int toI(int size, int row, int col) {
    return row * size + col;
  }
  
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
  
  private static class Rule {
    private final Set<State> inputs = new HashSet<>();
    private final State output;
    
    private static Map<State, State> lookup(List<Rule> rules) {
      Map<State, State> lookup = new HashMap<>();
      rules.forEach((rule) -> rule.inputs.forEach((input) -> lookup.put(input, rule.output)));
      return lookup;
    }
    
    private Rule(String spec) {
      int indexOfArrow = spec.indexOf(" => ");
      addAllInputs(spec.substring(0, indexOfArrow));
      this.output = new State(spec.substring(indexOfArrow + " => ".length()));
    }
    
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
    
    private State transformAddAndGet(State cur, Function<State, State> transform) {
      cur = transform.apply(cur);
      inputs.add(cur);
      return cur;
    }
    
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
