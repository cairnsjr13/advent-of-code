package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Base;
import java.util.function.Supplier;

public abstract class Base2022 extends Base {
  public static final Supplier<Base> day = () -> new Day25();
}
