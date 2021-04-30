package com.cairns.rich.aoc.grid;

import com.cairns.rich.aoc.Base.HasId;
import com.cairns.rich.aoc.EnumUtils;
import com.cairns.rich.aoc.SafeAccessor;
import java.util.function.ToIntFunction;

/**
 * Base interface for direction enums.  A direction, at its root, is a description of deltas in each direction (x/y).
 * The {@link #ordinal()} method is included in this interface to ensure it is an enum that implements this (or
 * at least something that looks a lot like an enum).  Turning is also implemented in a general way at this level.
 * In order for this to work, the enum constants must be defined in a circular order (ie NESW, not NSEW).
 * 
 * @implNote The bizarre type param here allows the turn methods to return the proper type.
 */
public interface Dir<D extends Dir<D, I>, I> extends HasId<I> {
  /**
   * The amount the x-coord of a {@link Point} should change when moving in this direction.
   */
  int dx();
  
  /**
   * The amount the y-coord of a {@link Point} should change when moving in this direction.
   */
  int dy();
  
  /**
   * Should be implemented by the enum interface.  Returns the position in the values array for this type.
   */
  int ordinal();
  
  /**
   * Returns the direction found by turning one unit to the left.
   */
  default D turnLeft() {
    return turn(this, (ds) -> -1);
  }
  
  /**
   * Returns the direction found by turning one unit to the right.
   */
  default D turnRight() {
    return turn(this, (ds) -> 1);
  }
  
  /**
   * All directions in which "turning around" makes sense should implement this interface.  All directions
   * that have an even number of options should implement this interface.  An odd number of options will fail. 
   */
  public interface EvenDir<D extends EvenDir<D, I>, I> extends Dir<D, I> {
    /**
     * Returns the direction that is directly behind/opposite "this" direction.
     */
    default D turnAround() {
      return turn(this, (ds) -> ds.length / 2);
    }
  }
  
  /**
   * General turn function that can safely turn a number of units for a given direction value.
   * The toDelta function should return the number of units to turn for the given value array.
   */
  private static <D extends Dir<D, I>, I> D turn(Dir<D, I> value, ToIntFunction<D[]> toDelta) {
    @SuppressWarnings("unchecked")
    D[] values = (D[]) EnumUtils.enumValues(value.getClass());
    return SafeAccessor.safeGet(values, value.ordinal() + toDelta.applyAsInt(values));
  }
}
