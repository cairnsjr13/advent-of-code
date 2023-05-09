package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.function.ToLongFunction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * The accounting elves need some help summing up numbers.  They are all in a nested json object.
 */
class Day12 extends Base2015 {
  /**
   * Sums up all number nodes in the nested json object.
   */
  @Override
  protected Object part1(Loader loader) throws Throwable {
    Object root = (new JSONParser()).parse(loader.sl());
    return sumAll(root, this::allIncluded);
  }

  /**
   * Sums up all number nodes in the nested json object, excluding the ones on or descendants of objects containing "red".
   */
  @Override
  protected Object part2(Loader loader) throws Throwable {
    Object root = (new JSONParser()).parse(loader.sl());
    return sumAll(root, this::skipRed);
  }

  /**
   * Computes the sum for this object.
   *   - {@link Long}s are verbatim
   *   - {@link JSONObject}s are recursively summed with the given filter
   *   - {@link JSONArray}s are recursively summed
   *   - all others are 0
   */
  private long sumAll(Object obj, ToLongFunction<JSONObject> objHandler) {
    if (obj instanceof Long) {
      return (Long) obj;
    }
    if (obj instanceof JSONObject) {
      return objHandler.applyAsLong((JSONObject) obj);
    }
    if (obj instanceof JSONArray) {
      long sum = 0;
      for (Object nextObj : ((JSONArray) obj)) {
        sum += sumAll(nextObj, objHandler);
      }
      return sum;
    }
    return 0;
  }

  /**
   * Sums all of the children (recursively) with no filtering.
   */
  private long allIncluded(JSONObject obj) {
    long sum = 0;
    for (Object nextObj : obj.values()) {
      sum += sumAll(nextObj, this::allIncluded);
    }
    return sum;
  }

  /**
   * Sums all of the children (recursively) short circuiting to 0 if any are "red".
   */
  private long skipRed(JSONObject obj) {
    long sum = 0;
    for (Object nextObj : obj.values()) {
      if ("red".equals(nextObj)) {
        return 0;
      }
      sum += sumAll(nextObj, this::skipRed);
    }
    return sum;
  }
}
