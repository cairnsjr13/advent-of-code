package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader.ConfigBinding;

public abstract class Base2021 extends Base {
	public static final Base day = new Day25();

	public Base2021(ConfigBinding... fullLoaderConfigBindings) {
    super(fullLoaderConfigBindings);
  }
}
