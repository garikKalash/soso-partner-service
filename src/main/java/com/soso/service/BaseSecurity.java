package com.soso.service;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class BaseSecurity {
    public static String getEncodedVersion(String data){
        return BCrypt.hashpw(data, BCrypt.gensalt(4));
    }
}
