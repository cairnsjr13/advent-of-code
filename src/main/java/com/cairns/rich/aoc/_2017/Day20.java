package com.cairns.rich.aoc._2017;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

class Day20 extends Base2017 {
  @Override
  protected void run() {
    List<Particle> particles = fullLoader.ml(Particle::new);
    System.out.println(getSluggishParticle(particles));
    System.out.println(getNumParticlesAfterCollision(particles));
  }
  
  private int getSluggishParticle(List<Particle> particles) {
    int minI = 0;
    int minAccel = particles.get(0).acceleration.manhattan();
    for (int i = 1; i < particles.size(); ++i) {
      int accel = particles.get(i).acceleration.manhattan();
      if (accel < minAccel) {
        minI = i;
        minAccel = accel;
      }
    }
    return minI;
  }

  private int getNumParticlesAfterCollision(List<Particle> arrayParticles) {
    LinkedList<Particle> particles = new LinkedList<Day20.Particle>(arrayParticles);
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
    
    @Override
    public String toString() {
      return "p=" + position + ", v=" + velocity + ", a=" + acceleration;
    }
  }
  
  private static class Xyz {
    private int x;
    private int y;
    private int z;
    
    private Xyz(Matcher matcher, int offset) {
      this.x = Integer.parseInt(matcher.group(offset + 0));
      this.y = Integer.parseInt(matcher.group(offset + 1));
      this.z = Integer.parseInt(matcher.group(offset + 2));
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
    
    @Override
    public String toString() {
      return "(" + x + "," + y + "," + z + ")";
    }
  }
}
