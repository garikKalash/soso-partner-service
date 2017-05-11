package com.soso.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.soso.models.ServiceInfo;

import java.util.Map;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */

public class JsonConverter {
    private final static Gson gson = new GsonBuilder().create();

    public static String toJson(Map<String,Object> object){
        return gson.toJson(object);
    }

    public static String toJson(Object object){
        return gson.toJson(object);
    }

    public static ServiceInfo getServiceInfoFromJSONString(String jsonString){
        JsonObject jsonObject = gson.fromJson( jsonString , JsonObject.class);
        return gson.fromJson(jsonObject.get("serviceDetail").toString(),ServiceInfo.class);
    }

    public static boolean isValidTokenStatusFromJSONString(String jsonString){
        return gson.fromJson(gson.fromJson(jsonString,JsonObject.class).get("isValidToken").toString(),Boolean.class);
    }


    public static String getCreatedTokenKeyFromJSONString(String jsonString){
        return gson.fromJson(gson.fromJson(jsonString,JsonObject.class).getAsJsonObject("createdToken").get("key").toString(),String.class);
    }



}
