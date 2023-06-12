package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.grid.Point;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Particles in 3d space need to be simulated by a gpu.  Luckily our {@link Point} class only has 2 dimensions....
 * We can use a new {@link Xyz} class to describe locations, velocities, and accelerations.
 */
class Day20 extends Base2017 {
  /**
   * Finds the index of the particle that stays the closest to the origin in the long run.
   * The simple way to find this is by finding the particle which has the smallest absolute acceleration,
   * because any gap will eventually be closed (and overtaken) by a difference in acceleration.
   */
  @Override
  protected Object part1(Loader loader) {
    List<Particle> particles = loader.ml(Particle::new);
    return getMin(
        IntStream.range(0, particles.size()).boxed().collect(Collectors.toList()),
        (i) -> particles.get(i).acceleration.manhattan()
    );
  }

  /**
   * Returns the number of particles remaining after all collisions are resolved (and removed).
   * The number of ticks is experimental and assumed to be large enough that no collisions are left.
   */
  @Override
  protected Object part2(Loader loader) {
    LinkedList<Particle> particles = new LinkedList<>(loader.ml(Particle::new));
    for (int i = 0; i < 100; ++i) {
      Multimap<Xyz, Particle> positions = HashMultimap.create();
      for (Particle particle : particles) {
        particle.tick();
        positions.put(particle.position, particle);
      }
      for (Xyz position : positions.keySet()) {
        Collection<Particle> here = positions.get(position);
        if (here.size() > 1) {
          particles.removeAll(here);
        }
      }
    }
    return particles.size();
  }

  /**
   * A particle that is described by 3 attributes: position, velocity, and acceleration.
   */
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

    /**
     * On each tick, a particle's velocity is changed by its acceleration, and its position is then changed by its velocity.
     */
    private void tick() {
      velocity.add(acceleration);
      position.add(velocity);
    }
  }

  /**
   * A 3d descriptor object that describes a value in each of the 3 standard dimensions (xyz).
   */
  private static class Xyz {
    private int x;
    private int y;
    private int z;

    private Xyz(Matcher matcher, int offset) {
      this.x = num(matcher, offset + 0);
      this.y = num(matcher, offset + 1);
      this.z = num(matcher, offset + 2);
    }

    /**
     * Increments each of the dimensions values by the given values.
     */
    private void add(Xyz other) {
      x += other.x;
      y += other.y;
      z += other.z;
    }

    /**
     * Returns the manhattan distance from the origin of this descriptor.
     */
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
