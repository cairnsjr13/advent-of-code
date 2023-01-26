package com.cairns.rich.aoc._2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

public class Day08 extends Base2021 {
  private static final Set<Integer> uniqueConfigs = Set.of(1, 4, 7, 8);
  
  @Override
  protected void run() {
    List<Signal> signals = fullLoader.ml(Signal::new);
    
    long partOneCount = 0;
    long partTwoTotal = 0;
    for (Signal signal : signals) {
      BiMap<Integer, Set<Character>> configs = deduceConfigs(signal.inputs);
      partOneCount += numUniqueLengthOutputs(configs, signal);
      partTwoTotal += parseOutput(configs, signal);
    }
    
    System.out.println(partOneCount);
    System.out.println(partTwoTotal);
  }
  
  private long numUniqueLengthOutputs(BiMap<Integer, Set<Character>> configs, Signal signal) {
    return signal.outputs.stream().filter((output) -> uniqueConfigs.contains(configs.inverse().get(output))).count();
  }
  
  private int parseOutput(BiMap<Integer, Set<Character>> configs, Signal signal) {
    return 1000 * configs.inverse().get(signal.outputs.get(0))
         +  100 * configs.inverse().get(signal.outputs.get(1))
         +   10 * configs.inverse().get(signal.outputs.get(2))
         +    1 * configs.inverse().get(signal.outputs.get(3));
  }
  
  private BiMap<Integer, Set<Character>> deduceConfigs(Set<Set<Character>> inputs) {
    BiMap<Integer, Set<Character>> configs = HashBiMap.create();
    
    deduceConfig(1, configs, inputs, 2, Set.of());
    deduceConfig(4, configs, inputs, 4, Set.of());
    deduceConfig(7, configs, inputs, 3, Set.of());
    deduceConfig(8, configs, inputs, 7, Set.of());
    
    deduceConfig(9, configs, inputs, 6, configs.get(4));
    deduceConfig(0, configs, inputs, 6, configs.get(7));
    deduceConfig(6, configs, inputs, 6, Set.of());
    
    deduceConfig(3, configs, inputs, 5, configs.get(1));
    deduce2(configs, inputs);
    deduceConfig(5, configs, inputs, 5, Set.of());
    
    return configs;
  }
  
  private void deduceConfig(
      int config,
      BiMap<Integer, Set<Character>> configs,
      Set<Set<Character>> inputs,
      int length,
      Set<Character> includesAll
  ) {
    List<Set<Character>> candidates = new ArrayList<>();
    for (Set<Character> input : inputs) {
      if ((input.size() == length) && (input.containsAll(includesAll))) {
        candidates.add(input);
      }
    }
    if (candidates.size() != 1) {
      throw fail(config + ", " + candidates);
    }
    inputs.remove(candidates.get(0));
    configs.put(config, candidates.get(0));
  }
  
  private void deduce2(BiMap<Integer, Set<Character>> configs, Set<Set<Character>> inputs) {
    for (Set<Character> input : inputs) {
      if ((input.size() == 5) && (Sets.intersection(input, configs.get(4)).size() == 2)) {
        configs.put(2, input);
      }
    }
    inputs.remove(configs.get(2));
  }
  
  private static class Signal {
    private static final Pattern pattern = Pattern.compile("^(.+) \\| (.+)$");
    
    private final Set<Set<Character>> inputs;
    private final List<Set<Character>> outputs;
    
    private Signal(String line) {
      Matcher matcher = matcher(pattern, line);
      this.inputs = Arrays.stream(matcher.group(1).split(" ")).map(this::charsFromString).collect(Collectors.toSet());
      this.outputs = Arrays.stream(matcher.group(2).split(" ")).map(this::charsFromString).collect(Collectors.toList());
    }
    
    private Set<Character> charsFromString(String str) {
      Set<Character> chars = new HashSet<>();
      for (int i = 0; i < str.length(); ++i) {
        chars.add(str.charAt(i));
      }
      return chars;
    }
  }
}
