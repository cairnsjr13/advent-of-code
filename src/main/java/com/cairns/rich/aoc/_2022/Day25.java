package com.cairns.rich.aoc._2022;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.List;

class Day25 extends Base2022 {
  private static final List<Character> digits = List.of('=', '-', '0', '1', '2');
  private static final Table<Character, Character, AddResult> lookup = HashBasedTable.create();

  static {
    digits.forEach((left) -> registerAddResult(left, '0', new AddResult('0', left)));
    registerAddResult('=', '=', new AddResult('-', '1'));
    registerAddResult('=', '-', new AddResult('-', '2'));
    registerAddResult('=', '1', new AddResult('0', '-'));
    registerAddResult('=', '2', new AddResult('0', '0'));
    registerAddResult('-', '-', new AddResult('0', '='));
    registerAddResult('-', '1', new AddResult('0', '0'));
    registerAddResult('-', '2', new AddResult('0', '1'));
    registerAddResult('1', '1', new AddResult('0', '2'));
    registerAddResult('1', '2', new AddResult('1', '='));
    registerAddResult('2', '2', new AddResult('1', '-'));
  }

  private static void registerAddResult(char left, char right, AddResult result) {
    lookup.put(left, right, result);
    lookup.put(right, left, result);
  }

  @Override
  protected void run() {
    List<String> inputs = fullLoader.ml();
    String total = "";
    for (String input : inputs) {
      total = add(total, input);
    }
    System.out.println(total);
  }

  private String add(String left, String right) {
    StringBuilder str = new StringBuilder();
    int maxLength = Math.max(left.length(), right.length());
    char carry = '0';
    for (int i = 0; i < maxLength; ++i) {
      AddResult result = lookup.get(at(left, i), at(right, i));
      AddResult withCarry = lookup.get(result.primary, carry);
      str.append(withCarry.primary);
      carry = lookup.get(result.carry, withCarry.carry).primary;
    }
    str.append(carry);
    while (str.charAt(str.length() - 1) == '0') {
      str.deleteCharAt(str.length() - 1);
    }
    return str.reverse().toString();
  }

  private char at(String var, int index) {
    return (index < var.length())
        ? var.charAt(var.length() - index - 1)
        : '0';
  }

  private static final class AddResult {
    private final char carry;
    private final char primary;

    private AddResult(char carry, char primary) {
      this.carry = carry;
      this.primary = primary;
    }
  }
}
