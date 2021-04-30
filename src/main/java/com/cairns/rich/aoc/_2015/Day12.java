package com.cairns.rich.aoc._2015;

import java.util.function.ToLongFunction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Day12 extends Base2015 {
  @Override
  protected void run() {
    String input = fullLoader.ml().get(0);
    JSONParser parser = new JSONParser();
    Object root = quietly(() -> parser.parse(input));
    System.out.println(sumAll(root, this::allIncluded));
    System.out.println(sumAll(root, this::skipRed));
  }
  
  private long sumAll(Object obj, ToLongFunction<JSONObject> objHandler) {
    long sum = 0;
    if (obj instanceof JSONArray) {
      for (Object nextObj : ((JSONArray) obj)) {
        sum += sumAll(nextObj, objHandler);
      }
    }
    else if (obj instanceof JSONObject) {
      sum += objHandler.applyAsLong((JSONObject) obj);
    }
    else if (obj instanceof Long) {
      sum += (Long) obj;
    }
    return sum;
  }
  
  private long allIncluded(JSONObject obj) {
    long sum = 0;
    for (Object nextObj : obj.values()) {
      sum += sumAll(nextObj, this::allIncluded);
    }
    return sum;
  }
  
  private long skipRed(JSONObject obj) {
    long sum = 0;
    for (Object nextObj : obj.values()) {
      if ((nextObj instanceof String) && "red".equals((String) nextObj)) {
        return 0;
      }
      sum += sumAll(nextObj, this::skipRed);
    }
    return sum;
  }
}
