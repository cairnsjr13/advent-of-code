package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.function.ToLongFunction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Day12 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    String input = loader.sl();
    JSONParser parser = new JSONParser();
    Object root = quietly(() -> parser.parse(input));
    result.part1(sumAll(root, this::allIncluded));
    result.part2(sumAll(root, this::skipRed));
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
      if ("red".equals(nextObj)) {
        return 0;
      }
      sum += sumAll(nextObj, this::skipRed);
    }
    return sum;
  }
}
