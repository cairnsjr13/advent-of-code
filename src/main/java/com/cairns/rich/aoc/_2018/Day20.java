package com.cairns.rich.aoc._2018;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

class Day20 extends Base2018 {
  private static final Map<Character, CardDir> dirLookup = EnumUtils.getLookup(CardDir.class);
  private static final Set<Character> atoms = Set.of('N', 'S', 'E', 'W');
  
  @Override
  protected void run() {
    String input = fullLoader.sl();
    input = input.substring(1, input.length() - 1);
    
    AndPiece topLevelPiece = parse(input);

    ImmutablePoint origin = new ImmutablePoint(0, 0);
    Multimap<ImmutablePoint, ImmutablePoint> doors = HashMultimap.create();
    addDoors(HashMultimap.create(), doors, origin, new LinkedList<>(topLevelPiece.subs));
    Map<ImmutablePoint, Integer> minDists = minDists(doors, origin);
    Pair<Integer, Integer> results = findResults(minDists);
    System.out.println(results.getLeft());
    System.out.println(results.getRight());
  }
  
  private Pair<Integer, Integer> findResults(Map<ImmutablePoint, Integer> minDists) {
    int largestDistance = 0;
    int numAtLeast1000 = 0;
    for (ImmutablePoint point : minDists.keySet()) {
      int dist = minDists.get(point);
      if (largestDistance < dist) {
        largestDistance = dist;
      }
      if (dist >= 1000) {
        ++numAtLeast1000;
      }
    }
    return Pair.of(largestDistance, numAtLeast1000);
  }
  
  private Map<ImmutablePoint, Integer> minDists(Multimap<ImmutablePoint, ImmutablePoint> doors, ImmutablePoint origin) {
    Map<ImmutablePoint, Integer> minDists = new HashMap<>();
    Queue<Candidate> candidates = new ArrayDeque<>();
    minDists.put(origin, 0);
    candidates.add(new Candidate(0, origin));
    while (!candidates.isEmpty()) {
      Candidate candidate = candidates.poll();
      for (ImmutablePoint nextStep : doors.get(candidate.location)) {
        if (!minDists.containsKey(nextStep)) {
          minDists.put(nextStep, candidate.numSteps + 1);
          candidates.offer(new Candidate(candidate.numSteps + 1, nextStep));
        }
      }
    }
    return minDists;
  }
  
  private void addDoors(
      Multimap<ImmutablePoint, Piece<?>> visited,
      Multimap<ImmutablePoint, ImmutablePoint> doors,
      ImmutablePoint from,
      Deque<Piece<?>> remaining
  ) {
    if (remaining.isEmpty() || visited.containsEntry(from, remaining.peek())) {
      return;
    }
    Piece<?> piece = remaining.poll();
    visited.put(from, piece);
    if (piece instanceof AtomPiece) {
      AtomPiece atom = (AtomPiece) piece;
      for (CardDir dir : atom.subs) {
        ImmutablePoint next = from.move(dir);
        doors.put(from, next);
        doors.put(next, from);
        from = next;
      }
      if (!remaining.isEmpty()) {
        addDoors(visited, doors, from, remaining);
      }
    }
    else if (piece instanceof AndPiece) {
      AndPiece andPiece = (AndPiece) piece;
      for (int i = andPiece.subs.size() - 1; i >= 0; --i) {
        remaining.addFirst(andPiece.subs.get(i));
      }
      addDoors(visited, doors, from, remaining);
    }
    else if (piece instanceof OrPiece) {
      OrPiece orGroup = (OrPiece) piece;
      List<Piece<?>> cacheRemaining = new ArrayList<>(remaining);
      for (Piece<?> orNext : orGroup.subs) {
        remaining.clear();
        remaining.add(orNext);
        remaining.addAll(cacheRemaining);
        addDoors(visited, doors, from, remaining);
      }
    }
    else {
      throw fail(piece.getClass());
    }
  }
  
  private AndPiece parse(String spec) {
    AndPiece topLevelAnd = new AndPiece();
    Stack<Piece<?>> stack = new Stack<>();
    stack.add(topLevelAnd);
    for (int i = 0; i < spec.length(); ++i) {
      char ch = spec.charAt(i);
      if (ch == '(') {
        GroupPiece holding = (GroupPiece) stack.peek();
        OrPiece newOr = new OrPiece();
        AndPiece newAnd = new AndPiece();
        
        holding.subs.add(newOr);
        newOr.subs.add(newAnd);
        
        stack.push(newOr);
        stack.push(newAnd);
      }
      else if (ch == ')') {
        stack.pop();    // pop the and
        stack.pop();    // pop the or
      }
      else if (ch == '|') {
        stack.pop();
        OrPiece holding = (OrPiece) stack.peek();
        stack.push(new AndPiece());
        holding.subs.add(stack.peek());
      }
      else {
        AtomPiece atomPiece = new AtomPiece();
        ((GroupPiece) stack.peek()).subs.add(atomPiece);
        for (; (i < spec.length()) && atoms.contains(spec.charAt(i)); ++i) {
          atomPiece.subs.add(dirLookup.get(spec.charAt(i)));
        }
        --i;
      }
    }
    return topLevelAnd;
  }
  
  private static abstract class Piece<T> {
    private final char marker;
    protected final List<T> subs = new ArrayList<>();
    
    protected Piece(char marker) {
      this.marker = marker;
    }
    
    @Override
    public String toString() {
      return "{" + marker + ":" + subs + "}";
    }
  }
  
  private static class AtomPiece extends Piece<CardDir> {
    private AtomPiece() {
      super('A');
    }
  }
  
  private static class GroupPiece extends Piece<Piece<?>> {
    private GroupPiece(char marker) {
      super(marker);
    }
  }
  
  private static class AndPiece extends GroupPiece {
    private AndPiece() {
      super('&');
    }
  }
  
  private static class OrPiece extends GroupPiece {
    private OrPiece() {
      super('|');
    }
  }
  
  private static class Candidate {
    private final int numSteps;
    private final ImmutablePoint location;
    
    private Candidate(int numSteps, ImmutablePoint location) {
      this.numSteps = numSteps;
      this.location = location;
    }
  }
}
