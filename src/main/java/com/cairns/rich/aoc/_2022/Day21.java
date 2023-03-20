package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader2;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

class Day21 extends Base2022 {
  @Override
  protected Object part1(Loader2 loader) {
    Map<String, Monkey> lookup = getLookup(loader.ml(Monkey::parse));
    return lookup.get("root").simplify(lookup).getValue(lookup);
  }

  @Override
  protected Object part2(Loader2 loader) {
    Map<String, Monkey> lookup = getLookup(loader.ml(Monkey::parse));
    lookup.get("root").simplify(lookup);
    OpMonkey root = (OpMonkey) lookup.get("root");
    BigInteger value;
    Monkey current;
    if (lookup.get(root.left) instanceof NumberMonkey) {
      value = lookup.get(root.left).getValue(lookup);
      current = lookup.get(root.right);
    }
    else {
      value = lookup.get(root.right).getValue(lookup);
      current = lookup.get(root.left);
    }

    while (current instanceof OpMonkey) {
      OpMonkey opMonkey = (OpMonkey) current;
      Monkey left = lookup.get(opMonkey.left);
      Monkey right = lookup.get(opMonkey.right);
      if (opMonkey.isCommutative()) {
        if (right instanceof NumberMonkey) {
          value = opMonkey.inverseOp().apply(value, right.getValue(lookup));
          current = left;
        }
        else {
          value = opMonkey.inverseOp().apply(value, left.getValue(lookup));
          current = right;
        }
      }
      else {
        if (right instanceof NumberMonkey) {
          value = opMonkey.inverseOp().apply(right.getValue(lookup), value);
          current = left;
        }
        else {
          value = opMonkey.op().apply(left.getValue(lookup), value);
          current = right;
        }
      }
    }
    return value;
  }

  private abstract static class Monkey implements HasId<String> {
    protected final String id;

    private Monkey(String idWithColon) {
      this.id = idWithColon.substring(0, 4);
    }

    @Override
    public String getId() {
      return id;
    }

    protected abstract BigInteger getValue(Map<String, Monkey> lookup);

    protected abstract Monkey simplify(Map<String, Monkey> lookup);

    private static Monkey parse(String line) {
      String[] parts = line.split(" ");
      return (parts.length == 2)
          ? new NumberMonkey(parts)
          : new OpMonkey(parts);
    }
  }

  private static class NumberMonkey extends Monkey {
    private final BigInteger number;

    private NumberMonkey(String[] parts) {
      super(parts[0]);
      this.number = new BigInteger(parts[1]);
    }

    private NumberMonkey(String id, BigInteger number) {
      super(id + ":");
      this.number = number;
    }

    @Override
    protected BigInteger getValue(Map<String, Monkey> lookup) {
      return number;
    }

    @Override
    protected Monkey simplify(Map<String, Monkey> lookup) {
      return this;
    }
  }

  private static class OpMonkey extends Monkey {
    private static final List<Character> opChs = List.of('+', '*', '-', '/' );
    private static final List<BinaryOperator<BigInteger>> ops =
        List.of(BigInteger::add, BigInteger::multiply, BigInteger::subtract, BigInteger::divide);

    private final int opIndex;
    private final String left;
    private final String right;

    private OpMonkey(String[] parts) {
      super(parts[0]);
      this.opIndex = opChs.indexOf(parts[2].charAt(0));
      this.left = parts[1];
      this.right = parts[3];
    }

    @Override
    protected BigInteger getValue(Map<String, Monkey> lookup) {
      return op().apply(
          lookup.get(left).getValue(lookup),
          lookup.get(right).getValue(lookup)
      );
    }

    @Override
    protected Monkey simplify(Map<String, Monkey> lookup) {
      Monkey leftSimplified = lookup.get(left).simplify(lookup);
      Monkey rightSimplified = lookup.get(right).simplify(lookup);
      if (isNonHumanNum(leftSimplified) && isNonHumanNum(rightSimplified)) {
        NumberMonkey simplified = new NumberMonkey(id, getValue(lookup));
        lookup.put(id, simplified);
        return simplified;
      }
      return this;
    }

    private boolean isCommutative() {
      return (opIndex == 0) || (opIndex == 1);    // + or *
    }

    private BinaryOperator<BigInteger> op() {
      return ops.get(opIndex);
    }

    private BinaryOperator<BigInteger> inverseOp() {
      return safeGet(ops, opIndex + ops.size() / 2);
    }

    private static boolean isNonHumanNum(Monkey monkey) {
      return (monkey instanceof NumberMonkey) && !"humn".equals(monkey.id);
    }
  }
}
