package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.Loader2;
import com.cairns.rich.aoc.grid.CardDir;
import com.cairns.rich.aoc.grid.ImmutablePoint;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

class Day20 extends Base2018 {
  private static final Map<Character, CardDir> dirLookup = EnumUtils.getLookup(CardDir.class);

  @Override
  protected Object part1(Loader2 loader) {
    return findAnswerFromMinDists(loader, (minDists) -> getMax(minDists.values(), Function.identity()));
  }

  @Override
  protected Object part2(Loader2 loader) {
    return findAnswerFromMinDists(loader, (minDists) -> minDists.values().stream().filter((dist) -> dist >= 1000).count());
  }

  private long findAnswerFromMinDists(Loader2 loader, Function<Map<ImmutablePoint, Long>, Long> toAnswer) {
    AndPiece topLevelPiece = parse(fullLoader.sl());
    ImmutablePoint origin = new ImmutablePoint(0, 0);
    Multimap<ImmutablePoint, ImmutablePoint> doors = HashMultimap.create();
    addDoors(HashMultimap.create(), doors, origin, new LinkedList<>(topLevelPiece.subs));
    return toAnswer.apply(minDists(doors, origin));
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

  private Map<ImmutablePoint, Long> minDists(Multimap<ImmutablePoint, ImmutablePoint> doors, ImmutablePoint origin) {
    Map<ImmutablePoint, Long> minDists = new HashMap<>();
    bfs(
        origin,
        (s) -> false,
        SearchState::getNumSteps,
        (current, steps, registrar) -> {
          if (!minDists.containsKey(current)) {
            minDists.put(current, steps);
            doors.get(current).forEach(registrar::accept);
          }
        }
    );
    return minDists;
  }

  private AndPiece parse(String spec) {
    AndPiece topLevelAnd = new AndPiece();
    Stack<Piece<?>> stack = new Stack<>();
    stack.add(topLevelAnd);
    for (int i = 1; i < spec.length() - 1; ++i) {   // trim the ^$
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
        for (; (i < spec.length()) && AtomPiece.atoms.contains(spec.charAt(i)); ++i) {
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
    private static final Set<Character> atoms = Set.of('N', 'S', 'E', 'W');

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
}
