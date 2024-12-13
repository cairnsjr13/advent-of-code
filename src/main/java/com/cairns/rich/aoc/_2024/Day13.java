package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * There are a number of claw machines that we can win prizes on.  Each of buttons
 * A and B cost a certain number of tokens to press.  If we can reach the prize on
 * a machine, we need to add the minimum number of tokens required to reach it.
 */
class Day13 extends Base2024 {
  private static final int BUTTON_A_COST = 3;
  private static final int BUTTON_B_COST = 1;

  /**
   * The puzzle input has the actual location of the prizes with no offset.
   */
  @Override
  protected Object part1(Loader loader) {
    return findMinimumTokensToWinPrizes(loader, 0);
  }

  /**
   * The puzzle input needs to be offset by 10_000_000_000_000L.
   */
  @Override
  protected Object part2(Loader loader) {
    return findMinimumTokensToWinPrizes(loader, 10_000_000_000_000L);
  }

  /**
   * For each machine, the minimum number of tokens required to get the prize can be computed with discrete algebra.
   * Because integer division is used, after the computation, we must check that the actual final location lands exactly
   * on the prize to determine if it is a winner.  A key assumption of the puzzle input is that there is only one actual
   * combination of A/B pushes that can reach the prize, so the "minimum" requirement is actually irrelevant.
   *
   * The system of equations with 2 equations and 2 unknowns has exactly one answer:
   * <pre>
   *    An = number of A pushes  (UNKNOWN)
   *    Bn = number of B pushes  (UNKNOWN)
   *    ButtonA = d(Ax, Ay)      (ALL CONSTANTS)
   *    ButtonB = d(Bx, By)      (ALL CONSTANTS)
   *    Prize = (Px, Py)         (ALL CONSTANTS)
   *
   *    Px = An * Ax + Bn * Bx
   *    Py = An * Ay + Bn * By
   * </pre>
   * Solving the first equation for Bn:
   * <pre>
   *         Px - An * Ax
   *    Bn = ------------
   *              Bx
   * </pre>
   * Substituting Bn into the second equation:
   * <pre>
   *         An * Ay + By * (Px - An * Ax)
   *    Py = -----------------------------
   *                      Bx
   *
   *         Bx * Py - By * Px
   *    An = -----------------    (In terms of only constants)
   *         Ay * Bx - Ax * By
   * </pre>
   *  Which can then be substituted back into the equation for Bn.
   */
  private long findMinimumTokensToWinPrizes(Loader loader, long prizeOffset) {
    long total = 0;
    List<Machine> machines = loader.gDelim("", Machine::new);
    for (Machine machine : machines) {
      long prizeX = machine.prize.x() + prizeOffset;
      long prizeY = machine.prize.y() + prizeOffset;

      long numAPushes = (machine.buttonB.dx * prizeY - machine.buttonB.dy * prizeX)
                      / (machine.buttonA.dy * machine.buttonB.dx - machine.buttonA.dx * machine.buttonB.dy);
      long numBPushes = (prizeX - numAPushes * machine.buttonA.dx)
                      / machine.buttonB.dx;

      long endX = machine.buttonA.dx * numAPushes
               + machine.buttonB.dx * numBPushes;
      long endY = machine.buttonA.dy * numAPushes
               + machine.buttonB.dy * numBPushes;

      if ((endX == prizeX) && (endY == prizeY)) {
        total += BUTTON_A_COST * numAPushes + BUTTON_B_COST * numBPushes;
      }
    }
    return total;
  }

  /**
   * Container object that describes a machine's buttons and prize location.
   */
  private static final class Machine {
    private static final Pattern prizePattern = Pattern.compile("^Prize: X=(\\d+), Y=(\\d+)$");

    private final Button buttonA;
    private final Button buttonB;
    private final ImmutablePoint prize;

    private Machine(List<String> lines) {
      this.buttonA = new Button(lines.get(0));
      this.buttonB = new Button(lines.get(1));
      Matcher matcher = matcher(prizePattern, lines.get(2));
      this.prize = new ImmutablePoint(num(matcher, 1), num(matcher, 2));
    }
  }

  /**
   * Container object that describes the (dx,dy) offsets that result in pressing this button.
   */
  private static final class Button {
    private static final Pattern pattern = Pattern.compile("^Button .: X\\+(\\d+), Y\\+(\\d+)$");

    private final long dx;
    private final long dy;

    private Button(String line) {
      Matcher matcher = matcher(pattern, line);
      this.dx = num(matcher, 1);
      this.dy = num(matcher, 2);
    }
  }
}
