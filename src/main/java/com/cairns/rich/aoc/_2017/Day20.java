package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day20 extends Base2017 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Particle> particles = loader.ml(Particle::new);
    return getMin(
        IntStream.range(0, particles.size()).boxed().collect(Collectors.toList()),
        (i) -> particles.get(i).acceleration.manhattan()
    );
  }

  @Override
  protected Object part2(Loader2 loader) {
    LinkedList<Particle> particles = new LinkedList<>(loader.ml(Particle::new));
    for (int i = 0; i < 100; ++i) {
      Multiset<Xyz> seenPositions = HashMultiset.create();
      for (Particle particle : particles) {
        particle.tick();
        seenPositions.add(particle.position);
      }
      Iterator<Particle> itr = particles.iterator();
      while (itr.hasNext()) {
        if (seenPositions.count(itr.next().position) > 1) {
          itr.remove();
        }
      }
    }
    return particles.size();
  }

  private static class Particle {
    private static final String xyzPattern = "<(-?\\d+),(-?\\d+),(-?\\d+)>";
    private static final Pattern pattern =
        Pattern.compile("^p=" + xyzPattern + ", v=" + xyzPattern + ", a=" + xyzPattern + "$");

    private final Xyz position;
    private final Xyz velocity;
    private final Xyz acceleration;

    private Particle(String spec) {
      Matcher matcher = matcher(pattern, spec);
      this.position = new Xyz(matcher, 1);
      this.velocity = new Xyz(matcher, 4);
      this.acceleration = new Xyz(matcher, 7);
    }

    private void tick() {
      velocity.add(acceleration);
      position.add(velocity);
    }
  }

  private static class Xyz {
    private int x;
    private int y;
    private int z;

    private Xyz(Matcher matcher, int offset) {
      this.x = num(matcher, offset + 0);
      this.y = num(matcher, offset + 1);
      this.z = num(matcher, offset + 2);
    }

    private void add(Xyz other) {
      x += other.x;
      y += other.y;
      z += other.z;
    }

    private int manhattan() {
      return Math.abs(x) + Math.abs(y) + Math.abs(z);
    }

    @Override
    public boolean equals(Object obj) {
      Xyz other = (Xyz) obj;
      return (x == other.x)
          && (y == other.y)
          && (z == other.z);
    }

    @Override
    public int hashCode() {
      return (31 * ((31 * x) + y)) + z;
    }
  }
}
