package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day10 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    return findMessage(loader, State::print);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return findMessage(loader, (state) -> state.numTicksToMessage);
  }

  private <T> T findMessage(Loader2 loader, Function<State, T> toAnswer) {
    State state = new State(loader);
    int previousDistance = state.getVertDiff();
    while (true) {
      state.lights.forEach(Light::tick);
      int currentDistance = state.getVertDiff();
      if (currentDistance > previousDistance) {
        state.lights.forEach(Light::unTick);
        return toAnswer.apply(state);
      }
      previousDistance = currentDistance;
      ++state.numTicksToMessage;
    }
  }

  private static class Light {
    private static final String subPattern = "< *(-?\\d+), *(-?\\d+)>";
    private static final Pattern pattern = Pattern.compile("^position=" + subPattern + " velocity=" + subPattern + "$");

    private final MutablePoint position;
    private final ReadDir horizDir;
    private final int horizVelocity;
    private final ReadDir vertDir;
    private final int vertVelocity;

    private Light(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.position = new MutablePoint(num(matcher, 1), num(matcher, 2));
      int dh = num(matcher, 3);
      int dv = num(matcher, 4);
      this.horizDir = (dh < 0) ? ReadDir.Left : ReadDir.Right;
      this.horizVelocity = Math.abs(dh);
      this.vertDir = (dv < 0) ? ReadDir.Up : ReadDir.Down;
      this.vertVelocity = Math.abs(dv);
    }

    private void tick() {
      position.move(horizDir, horizVelocity);
      position.move(vertDir, vertVelocity);
    }

    private void unTick() {
      position.move(horizDir.turnAround(), horizVelocity);
      position.move(vertDir.turnAround(), vertVelocity);
    }
  }

  private static class State {
    private final List<Light> lights;
    private int numTicksToMessage;

    private State(Loader2 loader) {
      this.lights = loader.ml(Light::new);
    }

    private int getVertDiff() {
      return getMax(lights, (light) -> light.position.y()).position.y()
           - getMin(lights, (light) -> light.position.y()).position.y();
    }

    private StringBuilder print() {
      StringBuilder out = new StringBuilder("\n");
      Set<MutablePoint> points = lights.stream().map((light) -> light.position).collect(Collectors.toSet());
      int minX = getMin(points, (p) -> p.x()).x();
      int maxX = getMax(points, (p) -> p.x()).x();
      int minY = getMin(points, (p) -> p.y()).y();
      int maxY = getMax(points, (p) -> p.y()).y();
      for (int y = minY; y <= maxY; ++y) {
        for (MutablePoint point = new MutablePoint(minX, y); point.x() <= maxX; point.move(ReadDir.Right)) {
          out.append((points.contains(point)) ? 0x2588 : ' ');  // TODO: centralize dark pixel
        }
        out.append("\n");
      }
      return out;
    }
  }
}
