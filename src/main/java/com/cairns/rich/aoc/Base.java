package com.cairns.rich.aoc;

import com.cairns.rich.aoc._2022.Base2022;
import com.google.common.base.Strings;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Base extends SafeAccessor {
  private static final ExecutorService daemonExec = Executors.newCachedThreadPool((r) -> {
    Thread thread = new Thread(r);
    thread.setDaemon(true);
    return thread;
  });
  private static final ThreadLocal<MessageDigest> md5Cache =
      ThreadLocal.withInitial(() -> quietly(() -> MessageDigest.getInstance("MD5")));

  public static void main(String[] args) throws Throwable {
    Base2022.day.run();
  }

  protected final Loader2 testLoader;
  protected final Loader2 fullLoader;

  protected Base() {
    String pkgPrefix = "/" + getClass().getPackageName().replace('.', '/') + "/";
    this.testLoader = new Loader2(pkgPrefix + "test.txt");
    this.fullLoader = new Loader2(pkgPrefix + getClass().getSimpleName().toLowerCase() + ".txt");
  }

  protected abstract void run() throws Throwable;

  protected static final RuntimeException fail() {
    return fail("Unexpected");
  }

  protected static final RuntimeException fail(Object msg) {
    return new RuntimeException(Objects.toString(msg));
  }

  protected static Matcher matcher(Pattern pattern, String spec) {
    Matcher matcher = pattern.matcher(spec);
    if (!matcher.matches()) {
      throw fail(spec);
    }
    return matcher;
  }

  protected static int num(Matcher matcher, int group) {
    return Integer.parseInt(matcher.group(group));
  }

  protected static String md5(String seed) {
    return quietly(() -> {
      MessageDigest md5 = md5Cache.get();
      md5.update(seed.getBytes());
      StringBuilder hex = new StringBuilder();
      for (byte b : md5.digest()) {
        hex.append(Strings.padStart(Integer.toHexString(((int) b) & 0xff), 2, '0'));
      }
      return hex.toString();
    });
  }

  protected static Future<?> startDaemon(Runnable action) {
    return daemonExec.submit(action);
  }

  protected static <R, V extends Comparable<V>> R getMax(Iterable<R> returns, Function<R, V> toValue) {
    return getExtreme(1, returns, toValue);
  }

  protected static <R, V extends Comparable<V>> R getMin(Iterable<R> returns, Function<R, V> toValue) {
    return getExtreme(-1, returns, toValue);
  }

  private static <R, V extends Comparable<V>> R getExtreme(
      int signForExtreme,
      Iterable<R> returns,
      Function<R, V> toValue
  ) {
    R toReturn = null;
    V extremeValue = null;
    for (R inspect : returns) {
      V value = toValue.apply(inspect);
      if ((extremeValue == null) || (value.compareTo(extremeValue) == signForExtreme)) {
        toReturn = inspect;
        extremeValue = value;
      }
    }
    return toReturn;
  }

  public static <I, T extends HasId<I>> Map<I, T> getLookup(Collection<T> elements) {
    return elements.stream().collect(Collectors.toMap(HasId::getId, Function.identity()));
  }

  public interface HasId<I> {
    I getId();
  }

  protected <S> Optional<SearchState<S>> bfs(
      S initial,
      Predicate<S> search,
      ToLongFunction<SearchState<S>> priorityComputer,
      BiConsumer<S, Consumer<S>> step
  ) {
    return bfs(initial, search, priorityComputer, (state, numSteps, registrar) -> step.accept(state, registrar));
  }

  protected <S> Optional<SearchState<S>> bfs(
      S initial,
      Predicate<S> search,
      ToLongFunction<SearchState<S>> priorityComputer,
      StepperWithStepsSoFar<S> step
  ) {
    Set<S> visited = new HashSet<>();
    visited.add(initial);
    PriorityQueue<SearchState<S>> pq = new PriorityQueue<>(Comparator.comparingLong(priorityComputer));
    pq.offer(new SearchState<>(initial, 0));
    while (!pq.isEmpty()) {
      SearchState<S> current = pq.poll();
      if (search.test(current.state)) {
        return Optional.of(current);
      }
      else {
        step.accept(current.state, current.numSteps, (next) -> {
          if (visited.add(next)) {
            pq.offer(new SearchState<>(next, current.numSteps + 1));
          }
        });
      }
    }
    return Optional.empty();
  }

  protected static interface StepperWithStepsSoFar<S> {
    void accept(S state, long numSteps, Consumer<S> registrar);
  }

  protected static final class SearchState<S> {
    public final S state;
    protected final long numSteps;

    protected SearchState(S state, long numSteps) {
      this.state = state;
      this.numSteps = numSteps;
    }

    public final long getNumSteps() {
      return numSteps;
    }
  }
}
