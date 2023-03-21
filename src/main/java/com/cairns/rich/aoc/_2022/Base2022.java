package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Base;
import com.cairns.rich.aoc.Loader2.ConfigBinding;

public abstract class Base2022 extends Base {
	public static final Base day = new Day25();

	public Base2022(ConfigBinding... fullLoaderConfigBindings) {
	  super(fullLoaderConfigBindings);
  }
}
