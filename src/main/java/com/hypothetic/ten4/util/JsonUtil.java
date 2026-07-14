package com.hypothetic.ten4.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class JsonUtil {
  public static @Nullable JsonObject getJsonObject(JsonObject json, String key) {
    if (json.has(key)) {
      return json.getAsJsonObject(key);
    }
    return null;
  }

  public static String getStringOr(JsonObject json, String key, String fallback) {
    JsonElement e = json.get(key);
    return (e != null && !e.isJsonNull()) ? e.getAsString() : fallback;
  }

  public static String getString(JsonObject json, String key) {
    return getStringOr(json, key, "");
  }

  public static int getIntOr(JsonObject json, String key, int fallback) {
    JsonElement e = json.get(key);
    return (e != null && !e.isJsonNull()) ? e.getAsInt() : fallback;
  }

  public static int getInt(JsonObject json, String key) {
    return getIntOr(json, key, 1);
  }

  public static float getFloatOr(JsonObject json, String key, float fallback) {
    JsonElement e = json.get(key);
    return (e != null && !e.isJsonNull()) ? e.getAsFloat() : fallback;
  }

  public static float getFloat(JsonObject json, String key) {
    return getFloatOr(json, key, 1f);
  }

  public static boolean getBooleanOr(JsonObject json, String key, boolean fallback) {
    JsonElement e = json.get(key);
    return (e != null && !e.isJsonNull()) ? e.getAsBoolean() : fallback;
  }
}
