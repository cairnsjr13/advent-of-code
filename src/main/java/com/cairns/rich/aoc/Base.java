package com.cairns.rich.aoc;

import com.cairns.rich.aoc._2015.Base2015;
import com.google.common.base.Strings;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
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
    Base day = Base2015.day;
    day.run();
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
}
