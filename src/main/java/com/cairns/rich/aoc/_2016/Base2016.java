package com.cairns.rich.aoc._2016;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader2.ConfigBinding;

public abstract class Base2016 extends Base {
  public static final Base2016 day = new Day25();

  public Base2016(ConfigBinding... fullLoaderConfigBindings) {
    super(fullLoaderConfigBindings);
  }
}
