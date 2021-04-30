package com.cairns.rich.aoc._2018;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.cairns.rich.aoc.grid.MutablePoint;
import com.cairns.rich.aoc.grid.RelDir;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;

class Day13 extends Base2018 {
  private static final Table<RelDir, Character, RelDir> turns = HashBasedTable.create();
  
  static {
    turns.put(RelDir.Down, '\\', RelDir.Right); // turn left
    turns.put(RelDir.Down, '/', RelDir.Left);   // turn right
    turns.put(RelDir.Up, '\\', RelDir.Left);    // turn left
    turns.put(RelDir.Up, '/', RelDir.Right);    // turn right
    turns.put(RelDir.Left, '\\', RelDir.Up);    // turn right
    turns.put(RelDir.Left, '/', RelDir.Down);   // turn left
    turns.put(RelDir.Right, '\\', RelDir.Down); // turn right
    turns.put(RelDir.Right, '/', RelDir.Up);    // turn left
  }
  
  @Override
  protected void run() {
    char[][] tracks = loadTracks(fullLoader.ml());
    MutablePoint firstCrash = findFirstCrash(tracks);
    System.out.println(firstCrash.x() + "," + -firstCrash.y());
    MutablePoint lastCrash = findLastCrash(tracks);
    System.out.println(lastCrash.x() + "," + -lastCrash.y());
  }
  
  private MutablePoint findFirstCrash(char[][] tracks) {
    tracks = copy(tracks);
    List<Cart> carts = findCarts(tracks);
    while (true) {
      carts.sort(Cart.cmp);
      for (Cart cart : carts) {
        if (cart.move(tracks)) {
          return cart.location;
        }
      }
    }
  }
  
  private MutablePoint findLastCrash(char[][] tracks) {
    tracks = copy(tracks);
    List<Cart> carts = findCarts(tracks);
    Set<Cart> crashed = new HashSet<>();
    while (true) {
      carts.sort(Cart.cmp);
      for (Cart cart : carts) {
        if (!crashed.contains(cart)) {
          if (cart.move(tracks)) {
            Cart colliding = getCollidingCart(carts, cart);
            crashed.add(cart);
            crashed.add(colliding);
            tracks[-colliding.location.y()][colliding.location.x()] = colliding.onTopOf;
          }
        }
      }
      if (crashed.size() + 1 == carts.size()) {
        return carts.stream().filter((c) -> !crashed.contains(c)).findFirst().get().location;
      }
    }
  }
  
  private Cart getCollidingCart(List<Cart> carts, Cart mover) {
    for (Cart cart : carts) {
      if ((cart != mover) && cart.location.equals(mover.location)) {
        return cart;
      }
    }
    throw fail();
  }
  
  private char[][] loadTracks(List<String> lines) {
    char[][] tracks = new char[lines.size()][];
    for (int i = 0; i < lines.size(); ++i) {
      tracks[i] = lines.get(i).toCharArray();
    }
    return tracks;
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
          carts.add(new Cart(new MutablePoint(x, -y), Cart.cartChars.get(ch)));
        }
      }
    }
    return carts;
  }
  
  private static class Cart {
    private static final BiMap<Character, RelDir> cartChars =
        HashBiMap.create(Map.of('<', RelDir.Left, '>', RelDir.Right, '^', RelDir.Up, 'v', RelDir.Down));
    private static final EnumMap<RelDir, Character> initOnTopOfs =
        new EnumMap<>(Map.of(RelDir.Left, '-', RelDir.Right, '-', RelDir.Up, '|', RelDir.Down, '|'));
    private static final Comparator<Cart> cmp =
        Comparator.<Cart, Integer>comparing((c) -> -c.location.y()).thenComparing((c) -> c.location.x());
    private static final List<Function<RelDir, RelDir>> intersectionTurns =
        List.of(RelDir::turnLeft, Function.identity(), RelDir::turnRight);
    
    private final MutablePoint location;
    private RelDir facing;
    private char onTopOf;
    private int intersectionCount = 0;
    
    private Cart(MutablePoint location, RelDir facing) {
      this.location = location;
      this.facing = facing;
      this.onTopOf = initOnTopOfs.get(facing);
    }
    
    private boolean move(char[][] tracks) {
      tracks[-location.y()][location.x()] = onTopOf;
      location.move(facing);
      onTopOf = tracks[-location.y()][location.x()];
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
      tracks[-location.y()][location.x()] = Cart.cartChars.inverse().get(facing);
      return false;
    }
    
    @Override
    public String toString() {
      return location.toString();
    }
  }
}
