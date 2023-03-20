package com.cairns.rich.aoc._2021;

import com.cairns.rich.aoc.Loader2;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day19 extends Base2021 {
  private static final ToIntFunction<Beacon> toX = (b) -> b.x;
  private static final ToIntFunction<Beacon> toY = (b) -> b.y;
  private static final ToIntFunction<Beacon> toZ = (b) -> b.z;
  private static final ToIntFunction<Beacon> toNegX = (b) -> -b.x;
  private static final ToIntFunction<Beacon> toNegY = (b) -> -b.y;

  private static final Transformation rotateAroundX = new Transformation(toX, toZ, toNegY);
  private static final Transformation rotateAroundY = new Transformation(toZ, toY, toNegX);
  private static final Transformation rotateAroundZ = new Transformation(toY, toNegX, toZ);

  @Override
  protected Object part1(Loader2 loader) {
    List<Scanner> scanners = loader.gDelim("", Scanner::new);
    baseScanners(scanners);
    return scanners.stream().map((s) -> s.beacons).flatMap(Set::stream).collect(Collectors.toSet()).size();
  }

  @Override
  protected Object part2(Loader2 loader) {
    List<Scanner> scanners = loader.gDelim("", Scanner::new);
    baseScanners(scanners);
    int maxDist = 0;
    for (int i = 0; i < scanners.size(); ++i) {
      for (int j = i + 1; j < scanners.size(); ++j) {
        maxDist = Math.max(maxDist, Scanner.manhattanDist(scanners.get(i), scanners.get(j)));
      }
    }
    return maxDist;
  }

  private void baseScanners(List<Scanner> scanners) {
    List<Scanner> basedScanners = new ArrayList<>();
    basedScanners.add(scanners.get(0));
    List<Scanner> unbasedScanners = scanners.subList(1, scanners.size()).stream().collect(Collectors.toList());
    while (!unbasedScanners.isEmpty()) {
      tryToMatch(basedScanners, unbasedScanners);
    }
  }

  private void tryToMatch(List<Scanner> basedScanners, List<Scanner> unbasedScanners) {
    for (Scanner basedScanner : basedScanners) {
      for (Scanner unbasedScanner : unbasedScanners) {
        boolean matched = forEachRotation(unbasedScanner, () -> {
          for (Beacon basedBeacon : basedScanner.beacons) {
            for (Beacon unbasedBeacon : unbasedScanner.beacons) {
              int dx = basedBeacon.x - unbasedBeacon.x;
              int dy = basedBeacon.y - unbasedBeacon.y;
              int dz = basedBeacon.z - unbasedBeacon.z;
              int numMatches = 0;
              int numMismatches = 0;
              for (Beacon testBeacon : unbasedScanner.beacons) {
                testBeacon.shift(dx, dy, dz);
                if (basedScanner.beacons.contains(testBeacon)) {
                  ++numMatches;
                }
                else {
                  ++numMismatches;
                }
                testBeacon.shift(-dx, -dy, -dz);
                if (12 - numMatches > unbasedScanner.beacons.size() - (numMatches + numMismatches)) {
                  break;
                }
              }
              if (numMatches >= 12) {
                unbasedScanner.shift(dx, dy, dz);
                return true;
              }
            }
          }
          return false;
        });
        if (matched) {
          unbasedScanners.remove(unbasedScanner);
          basedScanners.add(0, unbasedScanner);
          return;
        }
      }
    }
  }

  private boolean forEachRotation(Scanner scanner, BooleanSupplier action) {
    BooleanSupplier aroundAllY = () -> {
      for (int i = 0; i < 4; ++i) {
        scanner.beacons.forEach((b) -> b.apply(rotateAroundY));
        if (action.getAsBoolean()) {
          return true;
        }
      }
      return false;
    };
    for (int i = 0; i < 4; ++i) {
      scanner.beacons.forEach((b) -> b.apply(rotateAroundX));
      if (aroundAllY.getAsBoolean()) {
        return true;
      }
    }
    for (int i = 0; i < 2; ++i) {
      scanner.beacons.forEach((b) -> b.apply(rotateAroundZ));
      if (aroundAllY.getAsBoolean()) {
        return true;
      }
      scanner.beacons.forEach((b) -> b.apply(rotateAroundZ));
    }
    return false;
  }

  private static class Scanner {
    private final Set<Beacon> beacons;
    private int dx;
    private int dy;
    private int dz;

    private Scanner(List<String> lines) {
      this.beacons = lines.subList(1, lines.size()).stream().map(Beacon::new).collect(Collectors.toSet());
    }

    private void shift(int dx, int dy, int dz) {
      this.dx = dx;
      this.dy = dy;
      this.dz = dz;
      List<Beacon> beaconsToRehash = new ArrayList<>(beacons);
      beaconsToRehash.forEach((b) -> b.shift(dx, dy, dz));
      beacons.clear();
      beaconsToRehash.forEach(beacons::add);
    }

    private static int manhattanDist(Scanner first, Scanner second) {
      return Math.abs(first.dx - second.dx)
           + Math.abs(first.dy - second.dy)
           + Math.abs(first.dz - second.dz);
    }
  }

  private static class Beacon {
    private static final Pattern pattern = Pattern.compile("^(-?\\d+),(-?\\d+),(-?\\d+)$");

    private int x;
    private int y;
    private int z;

    private Beacon(String input) {
      Matcher matcher = matcher(pattern, input);
      this.x = num(matcher, 1);
      this.y = num(matcher, 2);
      this.z = num(matcher, 3);
    }

    private void apply(Transformation transformation) {
      int newX = transformation.xChange.applyAsInt(this);
      int newY = transformation.yChange.applyAsInt(this);
      int newZ = transformation.zChange.applyAsInt(this);

      this.x = newX;
      this.y = newY;
      this.z = newZ;
    }

    private void shift(int dx, int dy, int dz) {
      x += dx;
      y += dy;
      z += dz;
    }

    @Override
    public boolean equals(Object other) {
      return (other instanceof Beacon)
          && (x == ((Beacon) other).x)
          && (y == ((Beacon) other).y)
          && (z == ((Beacon) other).z);
    }

    @Override
    public int hashCode() {
      return Integer.hashCode(x + y + z);
    }
  }

  private static class Transformation {
    private final ToIntFunction<Beacon> xChange;
    private final ToIntFunction<Beacon> yChange;
    private final ToIntFunction<Beacon> zChange;

    private Transformation(ToIntFunction<Beacon> xChange, ToIntFunction<Beacon> yChange, ToIntFunction<Beacon> zChange) {
      this.xChange = xChange;
      this.yChange = yChange;
      this.zChange = zChange;
    }
  }
}
