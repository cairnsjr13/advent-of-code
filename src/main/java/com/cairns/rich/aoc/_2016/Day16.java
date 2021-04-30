package com.cairns.rich.aoc._2016;

class Day16 extends Base2016 {
  @Override
  protected void run() {
    System.out.println(getChecksum("10000", 20));
    System.out.println(getChecksum("11100010111110100", 272));
    System.out.println(getChecksum("11100010111110100", 35651584));
  }
  
  private String getChecksum(String initialState, int toFill) {
    String state = initialState;
    while (state.length() < toFill) {
      StringBuilder newState = new StringBuilder(state);
      newState.append('0');
      for (int i = state.length() - 1; i >= 0; --i) {
        newState.append((state.charAt(i) == '0') ? '1' : '0');
      }
      state = newState.toString();
    }
    String data = state.substring(0, toFill);
    while (true) {
      StringBuilder checksum = new StringBuilder();
      for (int i = 0; i < data.length(); i += 2) {
        checksum.append((data.charAt(i) == data.charAt(i + 1)) ? '1' : '0');
      }
      data = checksum.toString();
      if (data.length() % 2 == 1) {
        return data;
      }
    }
  }
}
