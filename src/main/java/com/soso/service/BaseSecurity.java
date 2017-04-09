package com.soso.service;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class BaseSecurity {
    public static String getMd5Version(String data){
        return DigestUtils.md5Hex(data);
    }
}
