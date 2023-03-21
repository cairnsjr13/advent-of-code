package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader2.ConfigBinding;

public abstract class Base2015 extends Base {
  public static final Base day = new Day25();

  public Base2015(ConfigBinding... fullLoaderConfigBindings) {
    super(fullLoaderConfigBindings);
  }
}
