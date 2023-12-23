package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Electrical pulses are to be sent through a module network and need to be simulated.
 * Various modules handle low/high signals differently and result in downstream pulses.
 */
class Day20 extends Base2023 {
  private static final ConfigToken<String> finalOutputName = ConfigToken.of("finalOutputName", Function.identity());
  private static final Pattern pattern = Pattern.compile("^(.+) -> (.+)$");
  private static final int LOW = 0;
  private static final int HIGH = 1;

  /**
   * Computes the product of the total number of {@link #LOW} pulses and {@link #HIGH} pulses after 1000 button pushes.
   */
  @Override
  protected Object part1(Loader loader) throws Throwable {
    Map<String, Module> modules = runPulsing(loader, (buttonPushes, output) -> buttonPushes <= 1000);
    long numLows = modules.values().stream().mapToLong((m) -> m.numPulsesReceived[LOW]).sum();
    long numHighs = modules.values().stream().mapToLong((m) -> m.numPulsesReceived[HIGH]).sum();
    return numLows * numHighs;
  }

  /**
   * Computes the lowest number of button pushes required for the output module to receive its first low signal.
   * By inspecting the full input, we can notice that the output module has a number of conjunction inputs that
   * are completely independent from each other.  By using this fact, we can quickly compute the number of button
   * pushes required for each of them to receive a low pulse.  At that point we can use {@link #lcm(java.util.stream.Stream)}
   * to compute the first time all of their cycles will line up and provide us an answer.
   */
  @Override
  protected Object part2(Loader loader) throws Throwable {
    Map<String, Module> modules = runPulsing(loader, (buttonPushes, output) -> !output.hasAllInputLowPoints());
    return lcm(((OutputModule) modules.get("rx")).inputLowPoints.values().stream());
  }

  /**
   * Helper method to execute all of the pulse rules for the input module network.  The simulation will continue
   * as long as the given keepGoing test continues to pass.  The simulation involves pressing a button (sending
   * a {@link Day20#LOW} pulse to the {@link BroadcasterModule} and waiting for all pulses to be sent.  Pulses
   * are processed in the overall order that they are sent in a queue like manner, not a stack like manner.
   */
  private Map<String, Module> runPulsing(Loader loader, BiPredicate<Integer, OutputModule> keepGoing) {
    Deque<Runnable> pulseQ = new ArrayDeque<>();
    Map<String, Module> modules = getLookup(loader.ml((line) -> parseModule(pulseQ, line)));
    OutputModule finalOutput = new OutputModule(loader.getConfig(finalOutputName));
    modules.put(finalOutput.name, finalOutput);
    modules.forEach((name, input) -> input.outputs.forEach((output) -> modules.get(output).registerInput(input)));
    for (int buttonPushes = 1; keepGoing.test(buttonPushes, finalOutput); ++buttonPushes) {
      modules.get(BroadcasterModule.NAME).pulseTo("button", LOW, modules);
      while (!pulseQ.isEmpty()) {
        pulseQ.poll().run();
      }
      finalOutput.checkForInputHighPoints(modules, buttonPushes);
    }
    return modules;
  }

  /**
   * Parsing function to return the proper type of {@link Module} for the given input line.
   */
  private Module parseModule(Deque<Runnable> pulseQ, String line) {
    Matcher matcher = matcher(pattern, line);
    String fullName = matcher.group(1);
    String outputsSpec = matcher.group(2);
    if (BroadcasterModule.NAME.equals(fullName)) {
      return new BroadcasterModule(pulseQ, outputsSpec);
    }
    if (fullName.charAt(0) == '%') {
      return new FlipFlopModule(pulseQ, fullName.substring(1), outputsSpec);
    }
    if (fullName.charAt(0) == '&') {
      return new ConjunctionModule(pulseQ, fullName.substring(1), outputsSpec);
    }
    throw fail(line);
  }

  /**
   * Base class for modules in the network.  Each module is responsible for accepting inputs
   * from other modules, processing the signal, and sending another signal downstream.  This
   * level of the inheritance graph will keep track of the pulse queue, the output modules it
   * is responsible for, as well as the total number of low and high pulses it has received.
   */
  private static abstract class Module implements HasId<String> {
    protected final Deque<Runnable> pulseQ;
    protected final String name;
    protected final List<String> outputs;
    protected final long[] numPulsesReceived = new long[2];

    protected Module(Deque<Runnable> pulseQ, String name, String outputsSpec) {
      this.pulseQ = pulseQ;
      this.name = name;
      this.outputs = (!"".equals(outputsSpec)) ? List.of(outputsSpec.split(", ")) : List.of();
    }

    /**
     * After all modules are created, this method should be called to notify downstream modules of their input modules.
     */
    protected void registerInput(Module from) { }

    /**
     * Helper method to facilitate sending a pulse from this module to downstream output modules.
     */
    protected final void pulseFrom(Map<String, Module> modules, int value) {
      outputs.forEach((output) -> pulseQ.offer(() -> modules.get(output).pulseTo(name, value, modules)));
    }

    /**
     * Sending a pulse to a module should be done with this method to handle pulse tracking.
     */
    protected final void pulseTo(String from, int value, Map<String, Module> modules) {
      ++numPulsesReceived[value];
      handlePulseTo(from, value, modules);
    }

    /**
     * Module specific logic to handle a received pulse should be implemented here.
     */
    protected abstract void handlePulseTo(String from, int value, Map<String, Module> modules);

    @Override
    public String getId() {
      return name;
    }
  }

  /**
   * A broadcast module receives the input of the button and kicks pulses off to outputs.
   */
  private static class BroadcasterModule extends Module {
    private static final String NAME = "broadcaster";

    public BroadcasterModule(Deque<Runnable> pulseQ, String outputsSpec) {
      super(pulseQ, NAME, outputsSpec);
    }

    @Override
    public void handlePulseTo(String from, int value, Map<String, Module> modules) {
      if (value == HIGH) {
        throw fail();
      }
      pulseFrom(modules, LOW);
    }
  }

  /**
   * A flip flop module maintains a state (initial {@link Day20#LOW}).
   * When receiving a low signal, the state is flipped and passed on to the output modules.
   * {@link Day20#HIGH} signals are ignored.
   */
  private static class FlipFlopModule extends Module {
    private int state;

    private FlipFlopModule(Deque<Runnable> pulseQ, String name, String outputsSpec) {
      super(pulseQ, name, outputsSpec);
    }

    @Override
    public void handlePulseTo(String from, int value, Map<String, Module> modules) {
      if (value == LOW) {
        state = (1 - state);
        pulseFrom(modules, state);
      }
    }
  }

  /**
   * A conjunction module keeps track of all of it's input modules' most recent pulses.
   * On a specific input pulse, if all of it's input modules' most recent pulses are {@link Day20#HIGH},
   * a {@link Day20#LOW} signal will be sent to the output, {@link Day20#HIGH} otherwise.
   */
  private static class ConjunctionModule extends Module {
    private final Map<String, Integer> lastPulsesFromInputs = new HashMap<>();

    private ConjunctionModule(Deque<Runnable> pulseQ, String name, String outputsSpec) {
      super(pulseQ, name, outputsSpec);
    }

    @Override
    protected void registerInput(Module from) {
      lastPulsesFromInputs.put(from.name, LOW);
    }

    @Override
    public void handlePulseTo(String from, int value, Map<String, Module> modules) {
      lastPulsesFromInputs.put(from, value);
      pulseFrom(modules, lastPulsesFromInputs.values().stream().allMatch(Predicate.isEqual(HIGH)) ? LOW : HIGH);
    }
  }

  /**
   * The output module is responsible for tracking the first time we see a {@link Day20#LOW} signal from each
   * of the input modules.  They can be run at the same time because the dependency graphs for each upstream
   * module is completely independent (only connecting at the broadcast and output modules).
   */
  private static class OutputModule extends Module {
    private final Map<String, Long> inputLowPoints = new HashMap<>();
    private ConjunctionModule finalConjunction;

    private OutputModule(String name) {
      super(null, name, "");
    }

    @Override
    protected void registerInput(Module from) {
      this.finalConjunction = (ConjunctionModule) from; // Input is corrupt if this isn't true
    }

    @Override
    protected void handlePulseTo(String from, int value, Map<String, Module> modules) { }

    /**
     * Should be called at the end of each button push to determine if one of the upstream modules received a
     * {@link Day20#LOW} signal.  This can be used to extrapolate the number of button pushes which they sync.
     * Only the first low signal will be tracked for each upstream module.
     */
    private void checkForInputHighPoints(Map<String, Module> modules, long numButtonPushes) {
      for (String inputName : finalConjunction.lastPulsesFromInputs.keySet()) {
        if (modules.get(inputName).numPulsesReceived[LOW] == 1) {
          inputLowPoints.putIfAbsent(inputName, numButtonPushes);
        }
      }
    }

    /**
     * Returns true if all of the upstream modules have registered a first {@link Day20#LOW} signal.
     */
    private boolean hasAllInputLowPoints() {
      return inputLowPoints.size() == finalConjunction.lastPulsesFromInputs.size();
    }
  }
}
