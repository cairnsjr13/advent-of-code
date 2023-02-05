package com.cairns.rich.aoc._2015;

class Day25 extends Base2015 {
  @Override
  protected void run() {
    int targetRow = 2978;
    int targetCol = 3083;

    int sequenceNumber = computeSequenceNumber(targetRow, targetCol);
    System.out.println(computeCode(sequenceNumber));
  }

  private int computeSequenceNumber(int targetRow, int targetCol) {
    int sequenceNumber = 1;
    for (int col = 1; col < targetCol; ++col) {
      sequenceNumber += (col + 1);
    }
    for (int row = 1; row < targetRow; ++row) {
      sequenceNumber += (targetCol + (row - 1));
    }
    return sequenceNumber;
  }

  private long computeCode(int sequenceNumber) {
    long code = 20_151_125;
    for (int i = 1; i < sequenceNumber; ++i) {
      code = (code * 252_533) % 33_554_393;
    }
    return code;
  }
}
