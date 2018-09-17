package com.soso.web;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Garik Kalashyan on 5/14/2017.
 */
public class AppModeResolver {
    //@Todo: read it from properties
    private final String mode = "DEVELOPMENT";

    public boolean isLocalMode(){
        return mode.equals("DEVELOPMENT");
    }

}
