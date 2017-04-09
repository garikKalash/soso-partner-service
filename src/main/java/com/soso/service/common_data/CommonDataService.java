package com.soso.service.common_data;

import com.soso.service.BaseRestClient;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class CommonDataService extends BaseRestClient{
    public CommonDataService(Integer serviceId) {
        super(serviceId);
    }

    public String getAllSosoServicesAsJSONString(){
        return getRestTemplate().getForObject(getDestinationService().getUrl()+"commonData/getSosoServices",String.class);
    }

    public String getAllCountryCodesAsJSONString(){
        return getRestTemplate().getForObject(getDestinationService().getUrl()+"commonData/countryCodes",String.class);
    }

}
