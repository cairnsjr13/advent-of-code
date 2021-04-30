package com.cairns.rich.aoc._2016;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day09 extends Base2016 {
  private static final Pattern pattern = Pattern.compile("^(\\((\\d+)x(\\d+)\\)).*$");
  
  @Override
  protected void run() {
    String input = fullLoader.ml().get(0);
    System.out.println(getDecompressedLength(input, false));
    System.out.println(getDecompressedLength(input, true));
  }
  
  private long getDecompressedLength(String input, boolean recursive) {
    long length = 0;
    for (int i = 0; i < input.length(); ++i) {
      char ch = input.charAt(i);
      if (ch == '(') {
        Matcher matcher = pattern.matcher(input.substring(i));
        if (matcher.matches()) {
          int strLength = matcher.group(1).length();
          int numChars = Integer.parseInt(matcher.group(2));
          int numCopies = Integer.parseInt(matcher.group(3));
          length += numCopies * ((recursive)
              ? getDecompressedLength(input.substring(i + strLength, i + strLength + numChars), true)
              : numChars);
          i += strLength + numChars - 1;
        }
        else {
          ++length;
        }
      }
      else {
        ++length;
      }
    }
    return length;
  }
}
