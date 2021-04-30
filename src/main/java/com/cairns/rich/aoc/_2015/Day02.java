package com.cairns.rich.aoc._2015;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day02 extends Base2015 {
  @Override
  protected void run() {
    List<Pkg> pkgs = fullLoader.ml(Pkg::new);
    System.out.println(getAnswer(pkgs, Pkg::getPaperRequired));
    System.out.println(getAnswer(pkgs, Pkg::getRibbonRequired));
  }
  
  private int getAnswer(List<Pkg> pkgs, ToIntFunction<Pkg> toAnswer) {
    return pkgs.stream().mapToInt(toAnswer).sum();
  }
  
  private static class Pkg {
    private static final Pattern pattern = Pattern.compile("^(\\d+)x(\\d+)x(\\d+)$");
    
    private final int[] dims;
    
    private Pkg(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.dims = new int[] {
          Integer.parseInt(matcher.group(1)),
          Integer.parseInt(matcher.group(2)),
          Integer.parseInt(matcher.group(3))
      };
      Arrays.sort(dims);
    }
    
    private int getPaperRequired() {
      return 2 * (dims[0] * dims[1] + dims[0] * dims[2] + dims[1] * dims[2])
           + dims[0] * dims[1];
    }
    
    private int getRibbonRequired() {
      return (dims[0] * dims[1] * dims[2])
           + (2 * (dims[0] + dims[1]));
    }
  }
}
