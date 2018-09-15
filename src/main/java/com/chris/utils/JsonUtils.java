package com.chris.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Chris Chen
 * 2018/9/15
 * Explain:Json工具
 */
public class JsonUtils {
    private static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
//            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Type clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }
}
