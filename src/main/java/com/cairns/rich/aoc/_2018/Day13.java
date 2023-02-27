package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.ReadDir;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

class Day13 extends Base2018 {
  private static final Table<ReadDir, Character, ReadDir> turns = HashBasedTable.create();

  static {
    turns.put(ReadDir.Down, '\\', ReadDir.Right); // turn left
    turns.put(ReadDir.Down, '/', ReadDir.Left);   // turn right
    turns.put(ReadDir.Up, '\\', ReadDir.Left);    // turn left
    turns.put(ReadDir.Up, '/', ReadDir.Right);    // turn right
    turns.put(ReadDir.Left, '\\', ReadDir.Up);    // turn right
    turns.put(ReadDir.Left, '/', ReadDir.Down);   // turn left
    turns.put(ReadDir.Right, '\\', ReadDir.Down); // turn right
    turns.put(ReadDir.Right, '/', ReadDir.Up);    // turn left
  }

  @Override
  protected void run() {
    char[][] tracks = loadTracks(fullLoader.ml());
    System.out.println(findFirstCrash(copy(tracks)));
    System.out.println(findLastCrash(copy(tracks)));
  }

  private MutablePoint findFirstCrash(char[][] tracks) {
    List<Cart> carts = findCarts(tracks);
    while (true) {
      Optional<Cart> collision = carts.stream().sorted(Cart.cmp).filter((cart) -> cart.move(tracks)).findFirst();
      if (collision.isPresent()) {
        return collision.get().location;
      }
    }
  }

  private MutablePoint findLastCrash(char[][] tracks) {
    List<Cart> carts = findCarts(tracks);
    Set<Cart> crashed = new HashSet<>();
    while (true) {
      carts.stream().sorted(Cart.cmp).filter((cart) -> !crashed.contains(cart) && cart.move(tracks)).forEach((cart) -> {
        Cart colliding = getCollidingCart(carts, cart);
        crashed.add(cart);
        crashed.add(colliding);
        tracks[colliding.location.y()][colliding.location.x()] = colliding.onTopOf;
      });
      if (carts.size() - crashed.size() == 1) {
        return carts.stream().filter((cart) -> !crashed.contains(cart)).findFirst().get().location;
      }
    }
  }

  private Cart getCollidingCart(List<Cart> carts, Cart mover) {
    return carts.stream().filter((cart) -> (cart != mover) && cart.location.equals(mover.location)).findFirst().get();
  }

  private char[][] loadTracks(List<String> lines) {
    return lines.stream().map(String::toCharArray).toArray(char[][]::new);
  }

  private char[][] copy(char[][] tracks) {
    return Arrays.stream(tracks).map((arr) -> Arrays.copyOf(arr, arr.length)).toArray((i) -> new char[i][]);
  }

  private List<Cart> findCarts(char[][] tracks) {
    List<Cart> carts = new ArrayList<>();
    for (int y = 0; y < tracks.length; ++y) {
      for (int x = 0; x < tracks[0].length; ++x) {
        char ch = tracks[y][x];
        if (Cart.cartChars.containsKey(ch)) {
          carts.add(new Cart(new MutablePoint(x, y), Cart.cartChars.get(ch)));
        }
      }
    }
    return carts;
  }

  private static class Cart {
    private static final BiMap<Character, ReadDir> cartChars =
        HashBiMap.create(Map.of('<', ReadDir.Left, '>', ReadDir.Right, '^', ReadDir.Up, 'v', ReadDir.Down));
    private static final EnumMap<ReadDir, Character> initOnTopOfs =
        new EnumMap<>(Map.of(ReadDir.Left, '-', ReadDir.Right, '-', ReadDir.Up, '|', ReadDir.Down, '|'));
    private static final Comparator<Cart> cmp =
        Comparator.<Cart>comparingInt((c) -> c.location.y()).thenComparingInt((c) -> c.location.x());
    private static final List<Function<ReadDir, ReadDir>> intersectionTurns =
        List.of(ReadDir::turnLeft, Function.identity(), ReadDir::turnRight);

    private final MutablePoint location;
    private ReadDir facing;
    private char onTopOf;
    private int intersectionCount = 0;

    private Cart(MutablePoint location, ReadDir facing) {
      this.location = location;
      this.facing = facing;
      this.onTopOf = initOnTopOfs.get(facing);
    }

    private boolean move(char[][] tracks) {
      tracks[location.y()][location.x()] = onTopOf;
      location.move(facing);
      onTopOf = tracks[location.y()][location.x()];
      if (Cart.cartChars.containsKey(onTopOf)) {
        return true;
      }
      else if ((onTopOf == '/') || (onTopOf == '\\')) {
        facing = turns.get(facing, onTopOf);
      }
      else if (onTopOf == '+') {
        facing = safeGet(intersectionTurns, intersectionCount).apply(facing);
        ++intersectionCount;
      }
      tracks[location.y()][location.x()] = Cart.cartChars.inverse().get(facing);
      return false;
    }
  }
}
