package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader2.ConfigBinding;

public abstract class Base2018 extends Base {
  public static final Base day = new Day25();

  public Base2018(ConfigBinding... fullLoaderConfigBindings) {
    super(fullLoaderConfigBindings);
  }
}
