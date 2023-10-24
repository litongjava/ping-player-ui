package com.litongjava.ping.player.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wcy on 2021/2/24.
 */
public class GsonUtils {

  private static final Gson gson = new Gson();
  private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

  public static String toJson(Object obj) {
    try {
      return GsonUtils.toJson(obj);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> T fromJson(String json, Class<T> type) {
    try {
      return GsonUtils.fromJson(json, type);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
    try {
      JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
      return fromJsonList(jsonArray, clazz);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> List<T> fromJsonList(JsonArray jsonArray, Class<T> clazz) {
    try {
      List<T> list = new ArrayList<>();
      for (JsonElement item : jsonArray) {
        T bean = gson.fromJson(item, clazz);
        list.add(bean);
      }
      return list;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String toPrettyJson(Object any) {
    return prettyGson.toJson(any);
  }

  public static void removeNullValues(JsonElement element) {
    if (element instanceof JsonObject) {
      removeObjectNullValues((JsonObject) element);
    } else if (element instanceof JsonArray) {
      removeArrayNullValues((JsonArray) element);
    }
  }

  private static void removeObjectNullValues(JsonObject jsonObject) {
    Iterator<String> iterator = jsonObject.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      JsonElement json = jsonObject.get(key);
      if (json instanceof JsonNull) {
        iterator.remove();
      } else {
        removeNullValues(json);
      }
    }
  }

  private static void removeArrayNullValues(JsonArray jsonArray) {
    Iterator<JsonElement> iterator = jsonArray.iterator();
    while (iterator.hasNext()) {
      JsonElement element = iterator.next();
      if (element instanceof JsonNull) {
        iterator.remove();
      } else {
        removeNullValues(element);
      }
    }
  }
}
