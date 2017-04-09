package com.soso.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class JsonMapBuilder {
    private static Map<String,Object> map = new HashMap<>();

    public JsonMapBuilder() {
    }

    public JsonMapBuilder add(String key,Object object){
        map.put(key,object);
        return this;
    }

    public Map<String, Object> build(){
        return map;
    }
}
