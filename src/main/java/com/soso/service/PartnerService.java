package com.soso.service;

import com.soso.dto.PartnerDAO;
import com.soso.models.Partner;
import com.soso.service.authentication.AuthenticationTokenService;
import com.soso.service.common_data.CommonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */

@Repository
public class PartnerService {
    private final Integer selfId = 2;

    @Autowired
    private PartnerDAO partnerDAO;

    private final CommonDataService commonDataService = new CommonDataService(4);

    private final AuthenticationTokenService authenticationTokenService = new AuthenticationTokenService(3);

    public boolean isValidToken(String token) {
        return authenticationTokenService.isValidToken(selfId, token);
    }

    public void createToken(String token) {
        authenticationTokenService.createToken(selfId, token);
    }

    public void removeToken(String token) {
        authenticationTokenService.removeToken(selfId, token);
    }

    public Integer createPartner(Partner partner) {
        return partnerDAO.create(partner);
    }

    public Partner getPartnerById(Integer partnerId) {
        return partnerDAO.getPartnerById(partnerId);
    }

    public Integer signInPartner(Partner partner) {
        return partnerDAO.signin(partner);
    }

    public List<Partner> getAllPartners() {
        return partnerDAO.getAllPartners();
    }

    public String getAllSosoServicesAsJsonString() {
        return commonDataService.getAllSosoServicesAsJSONString();
    }

    public String getAllCountryCodesAsJsonString() {
        return commonDataService.getAllCountryCodesAsJSONString();
    }


}
