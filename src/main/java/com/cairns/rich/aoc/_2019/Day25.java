package com.cairns.rich.aoc._2019;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc._2019.IntCode.State;
import com.cairns.rich.aoc.grid.CardDir;
import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day25 extends Base2019 {
  private static final Map<Character, CardDir> dirs = EnumUtils.getLookup(CardDir.class);
  private static final Map<CardDir, String> dirToCmd =
      dirs.values().stream().collect(Collectors.toMap(Function.identity(), (dir) -> dir.name().toLowerCase()));
  private static final Set<String> dangerous =
      Set.of("molten lava", "infinite loop", "giant electromagnet", "escape pod", "photons");

  @Override
  protected Object part1(Loader2 loader) {
    List<Long> program = IntCode.parseProgram(loader);
    State state = IntCode.run(program);
    List<CardDir> pathToSecurityCheckpoint = takeAllSafe(state);
    List<String> items = goToCheckpointAndDropAll(state, pathToSecurityCheckpoint);

    Pattern answerPattern = Pattern.compile("^\\D*(\\d+)\\D*$");
    return matcher(answerPattern, findPassword(state, items, 0)).group(1);
  }

  protected final void interactive(State state) throws IOException {
    state.blockUntilHaltOrWaitForInput();
    readAll(state).forEach(System.out::println);
    try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
      while (!state.hasHalted()) {
        cmd(state, in.readLine()).forEach(System.out::println);
      }
    }
  }

  private List<CardDir> takeAllSafe(State state) {
    state.blockUntilHaltOrWaitForInput();
    List<CardDir> pathToTest = new ArrayList<>();
    findAndTakeAll(state, new ArrayDeque<>(), readAll(state), pathToTest);
    return pathToTest;
  }

  private void findAndTakeAll(
      State state,
      Deque<CardDir> path,
      List<String> previousOutput,
      List<CardDir> pathToSecurityCheckpoint
  ) {
    itemsHere(previousOutput, "Items here:").stream()
        .filter(Predicate.not(dangerous::contains))
        .forEach((item) -> cmd(state, "take " + item));
    for (CardDir dirToMove : dirsOpen(previousOutput)) {
      if (path.isEmpty() || (dirToMove != path.peekLast().turnAround())) {
        if (previousOutput.stream().anyMatch((line) -> -1 != line.indexOf("Security Checkpoint"))) {
          pathToSecurityCheckpoint.addAll(path);
        }
        else {
          List<String> output = cmd(state, dirToMove.name().toLowerCase());
          path.offerLast(dirToMove);
          findAndTakeAll(state, path, output, pathToSecurityCheckpoint);
          Preconditions.checkState(path.pollLast() == dirToMove);
          cmd(state, dirToMove.turnAround().name().toLowerCase());
        }
      }
    }
  }

  private List<String> goToCheckpointAndDropAll(State state, List<CardDir> pathToSecurityCheckpoint) {
    pathToSecurityCheckpoint.stream().map(dirToCmd::get).forEach((cmd) -> cmd(state, cmd));
    List<String> items = itemsHere(cmd(state, "inv"), "Items in your inventory:");
    items.forEach((item) -> cmd(state, "drop " + item));
    return items;
  }

  private String findPassword(State state, List<String> items, int index) {
    if (index == items.size()) {
      List<String> output = cmd(state, "east");
      return (output.stream().allMatch((line) -> -1 == line.indexOf("ejected")))
          ? output.get(output.size() - 1)
          : null;
    }
    String item = items.get(index);
    String password = findPassword(state, items, index + 1);
    if (password == null) {
      cmd(state, "take " + item);
      password = findPassword(state, items, index + 1);
      cmd(state, "drop " + item);
    }
    return password;
  }

  private List<CardDir> dirsOpen(List<String> lines) {
    return findElements(lines, "Doors here lead:", (ln) -> dirs.get(Character.toUpperCase(ln.charAt("- ".length()))));
  }

  private List<String> itemsHere(List<String> lines, String header) {
    return findElements(lines, header, (line) -> line.substring("- ".length()));
  }

  private <T> List<T> findElements(List<String> lines, String header, Function<String, T> transform) {
    List<T> elements = new ArrayList<>();
    int indexOfHeader = lines.indexOf(header);
    for (int i = indexOfHeader + 1; !lines.get(i).isEmpty(); ++i) {
      elements.add(transform.apply(lines.get(i)));
    }
    return elements;
  }

  private List<String> cmd(State state, String command) {
    command.chars().forEach(state.programInput::put);
    state.programInput.put('\n');
    for (int i = 0; i < command.length() + 1; ++i) {
      state.blockUntilHaltOrWaitForInput();
    }
    return readAll(state);
  }

  private List<String> readAll(State state) {
    StringBuilder str = new StringBuilder();
    while (state.programOutput.hasMoreToTake()) {
      str.append((char) state.programOutput.take());
    }
    return Arrays.asList(str.toString().split("\\n"));
  }
}
