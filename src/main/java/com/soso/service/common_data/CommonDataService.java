package com.soso.service.common_data;

import com.soso.models.MessageDto;
import com.soso.service.BaseRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
@Service
public class CommonDataService extends BaseRestClient{
    @Autowired
    public CommonDataService(@Value("${commondataservice.id}")Integer serviceId) {
        super(serviceId);
    }

    public String getAllSosoServicesAsJSONString(){
        return getRestTemplate().getForObject(getDestinationService().getUrl()+"commonData/getSosoServices",String.class);
    }

    public MessageDto getMessageByGlobKey(String globkey){
        return getRestTemplate().getForObject(getDestinationService().getUrl() + "commonData/systemmessagebykey/" + globkey, MessageDto.class);
    }




}
