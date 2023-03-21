package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader.ConfigBinding;

public abstract class Base2020 extends Base {
  public static final Base day = new Day25();

  public Base2020(ConfigBinding... fullLoaderConfigBindings) {
    super(fullLoaderConfigBindings);
  }
}
