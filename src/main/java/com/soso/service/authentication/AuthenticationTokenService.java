package com.soso.service.authentication;

import com.soso.service.BaseRestClient;
import com.soso.service.JsonConverter;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class AuthenticationTokenService extends BaseRestClient {


    public AuthenticationTokenService(Integer serviceId) {
        super(serviceId);
    }

    public boolean isValidToken(Integer selfId, Integer itemId, String token) {

        return  JsonConverter.isValidTokenStatusFromJSONString(
                getRestTemplate().getForObject(getDestinationService().getUrl() + "authenticateService/isValidToken/" + selfId + "/" +itemId+"/"+ token,
                        String.class)
        );
    }


    public void createToken(Integer selfId, String phoneNumber) {
        String result = getRestTemplate().postForObject(getDestinationService().getUrl() + "authenticateService/createToken/" + selfId + "/" + phoneNumber, null, String.class);
    }

    public void removeToken(Integer selfId, String token) {
        getRestTemplate().delete(getDestinationService().getUrl() + "authenticateService/deleteToken/" + selfId + "/" + token);
    }

}
