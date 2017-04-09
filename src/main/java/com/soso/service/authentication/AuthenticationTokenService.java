package com.soso.service.authentication;

import com.soso.service.BaseRestClient;
import com.soso.service.JsonConverter;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class AuthenticationTokenService extends BaseRestClient {

    private static String currentAuthenticationToken;

    public AuthenticationTokenService(Integer serviceId) {
        super(serviceId);
    }

    public String getAuthenticationToken() {
        return "";
    }

    public static String getCurrentAuthenticationToken() {
        return currentAuthenticationToken;
    }

    public static void setCurrentAuthenticationToken(String currentAuthenticationToken) {
        AuthenticationTokenService.currentAuthenticationToken = currentAuthenticationToken;
    }

    public boolean isValidToken(Integer selfId, String token) {

        return token.equals(currentAuthenticationToken)
                && JsonConverter.isValidTokenStatusFromJSONString(
                getRestTemplate().getForObject(getDestinationService().getUrl() + "authenticateService/isValidToken/" + selfId + "/" + token,
                        String.class)
        );
    }


    public void createToken(Integer selfId, String phoneNumber) {
        String result = getRestTemplate().postForObject(getDestinationService().getUrl() + "authenticateService/createToken/" + selfId + "/" + phoneNumber, null, String.class);
        currentAuthenticationToken = JsonConverter.getCreatedTokenKeyFromJSONString(result);
    }

    public void removeToken(Integer selfId, String token) {
        getRestTemplate().delete(getDestinationService().getUrl() + "authenticateService/deleteToken/" + selfId + "/" + token);
        currentAuthenticationToken = null;
    }

}
