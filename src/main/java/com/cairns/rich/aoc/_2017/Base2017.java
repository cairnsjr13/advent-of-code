package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader.ConfigBinding;

public abstract class Base2017 extends Base {
  public static final Base day = new Day25();

  public Base2017(ConfigBinding... fullLoaderConfigBindings) {
    super(fullLoaderConfigBindings);
  }
}
