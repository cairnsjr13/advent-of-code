package com.cairns.rich.aoc;

import java.util.List;

/**
 * Abstraction layer that provides the ability to safely access arrays and lists.
 * Negative indexes read from the back, and all arrays/lists are treated as circular (mod access).
 */
public abstract class SafeAccessor extends QuietCapable {
  /**
   * Returns the element of the given int array at the appropriate circular indexed position.
   */
  public static int safeGet(int[] arr, long unmodedIndex) {
    return arr[safeIndex(arr.length, unmodedIndex)];
  }
  
  /**
   * Returns the element of the given long array at the appropriate circular indexed position.
   */
  public static long safeGet(long[] arr, long unmodedIndex) {
    return arr[safeIndex(arr.length, unmodedIndex)];
  }

  /**
   * Returns the element of the given boolean array at the appropriate circular indexed position.
   */
  public static boolean safeGet(boolean[] arr, long unmodedIndex) {
    return arr[safeIndex(arr.length, unmodedIndex)];
  }
  
  /**
   * Returns the element of the given array at the appropriate circular indexed position.
   */
  public static <T> T safeGet(T[] arr, long unmodedIndex) {
    return arr[safeIndex(arr.length, unmodedIndex)];
  }
  
  /**
   * Returns the element of the given list at the appropriate circular indexed position.
   */
  public static <T> T safeGet(List<T> list, long unmodedIndex) {
    return list.get(safeIndex(list.size(), unmodedIndex));
  }
  
  /**
   * Sets the given element at the appropriate circular index of the given int array.
   */
  public static void safeSet(int[] arr, long unmodedIndex, int elem) {
    arr[safeIndex(arr.length, unmodedIndex)] = elem;
  }
  
  /**
   * Sets the given element at the appropriate circular index of the given long array.
   */
  public static void safeSet(long[] arr, long unmodedIndex, long elem) {
    arr[safeIndex(arr.length, unmodedIndex)] = elem;
  }
  
  /**
   * Sets the given element at the appropriate circular index of the given boolean array.
   */
  public static void safeSet(boolean[] arr, long unmodedIndex, boolean elem) {
    arr[safeIndex(arr.length, unmodedIndex)] = elem;
  }
  
  /**
   * Sets the given element at the appropriate circular index of the given array.
   */
  public static <T> void safeSet(T[] arr, long unmodedIndex, T elem) {
    arr[safeIndex(arr.length, unmodedIndex)] = elem;
  }
  
  /**
   * Sets the given element at the appropriate circular index of the given list.
   */
  public static <T> void safeSet(List<T> list, long unmodedIndex, T elem) {
    list.set(safeIndex(list.size(), unmodedIndex), elem);
  }
  
  /**
   * Computes the appropriate circular index position of an array/list of given size.
   */
  private static int safeIndex(int lengthSize, long unmodedIndex) {
    if (unmodedIndex < 0) {
      unmodedIndex += lengthSize * (1 + Math.abs(unmodedIndex / lengthSize));
    }
    return (int) (unmodedIndex % lengthSize);
  }
}
