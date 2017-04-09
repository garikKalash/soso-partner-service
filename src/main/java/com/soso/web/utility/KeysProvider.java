package com.soso.web.utility;

/**
 * Created by Garik.Kalashyan on 1/21/2017.
 */
public enum KeysProvider {
    USER_ID_KEY_FROM_SESSION("sessionUserId"),
    KEY_FOR_PROVIDER("provider"),
    KEY_FOR_CLIENT("client");

    KeysProvider(String key){
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }
}
