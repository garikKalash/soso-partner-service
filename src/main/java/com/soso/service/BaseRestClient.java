package com.soso.service;

import com.soso.models.ServiceInfo;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

/**
 * Created by Garik Kalashyan on 3/12/2017.
 */
public class BaseRestClient {
    private RestTemplate restTemplate;
    private ServiceInfo destinationService;


    protected BaseRestClient(@NotNull Integer serviceId){
        restTemplate = new RestTemplate();
        initializeBaseUrl(serviceId);
       }

    @PostConstruct
    protected void initializeBaseUrl(Integer serviceId){
        String serviceDetailByIdJSONString =  new ServicesDetailService().getInfoByServiceId(serviceId);
        destinationService =  JsonConverter.getServiceInfoFromJSONString(serviceDetailByIdJSONString);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public ServiceInfo getDestinationService() {
        return destinationService;
    }





    private class ServicesDetailService {
        private String baseServicesDetailServiceUrl = "https://pure-badlands-72083.herokuapp.com/";

        private String getInfoByServiceId(Integer serviceId){
            return restTemplate.getForObject(baseServicesDetailServiceUrl + "serviceDetails/" + serviceId,String.class);
        }


    }

}
