package com.soso.service;

import com.soso.dto.PartnerDAO;
import com.soso.models.Feedback;
import com.soso.models.Partner;
import com.soso.models.Request;
import com.soso.service.authentication.AuthenticationTokenService;
import com.soso.service.common_data.CommonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */

@Repository
public class PartnerService {
    private final Integer selfId = 2;
    private final CommonDataService commonDataService = new CommonDataService(4);
    private final AuthenticationTokenService authenticationTokenService = new AuthenticationTokenService(3);
    @Autowired
    private PartnerDAO partnerDAO;

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

    public List<Feedback> getFeedbacks(Integer partnerId) {
        return partnerDAO.loadFeedbacksByPartnerId(partnerId);
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


    public boolean deletePartnerOldLogoFromFiles(String oldLogoPath) {
        return new File(oldLogoPath).delete();
    }

    public void updatePartnerLogo(Integer newlogoId, Integer partnerId) {
        partnerDAO.updateLogosrcPathOfPartner(partnerId, newlogoId);
    }

    public void saveEditedMainInfo(Integer partnerId, String editedTelephone, String editedAddress) {
        partnerDAO.saveEditedMainInfoOfPartner(partnerId, editedAddress, editedTelephone);
    }

    public List<Partner> getPartnersByServiceId(Integer serviceId) {
        return partnerDAO.getPartnersByServiceId(serviceId);
    }

    public void saveEditedAddress(Integer partnerId, BigDecimal latitude, BigDecimal lotitude, String address) {
        partnerDAO.saveEditedAddress(partnerId, latitude, lotitude, address);
    }

    public void saveEditedNotice(Integer partnerId, String notice) {
        partnerDAO.saveEditedNotice(partnerId, notice);
    }

    public Integer savePhotoToPartnier(Integer partnerId, String imgPath) {
        return partnerDAO.addPhotoToPartnier(partnerId, imgPath);
    }

    public List<Integer> getPhotosByParentId(Integer partnerId) {
        return partnerDAO.loadPhotosByPartnerId(partnerId);
    }

    public String getPhotoById(Integer photoId){
        return partnerDAO.getPhotoById(photoId);
    }

    public  void deletePhotoById(Integer photoId){
        partnerDAO.deletePhotoById(photoId);
    }

    public void deleteReservationById(Integer reserveId){
        partnerDAO.deleteReservationById(reserveId);
    }

    public List<Request> getReservationsByClientId(Integer clientId){
        return partnerDAO.getReservationsByClientId(clientId);
    }

    public List<Request> getReservationsByPartnerId(Integer partnierId){
        return partnerDAO.getReservationsByPartnerId(partnierId);
    }


    public Integer addReservation(Request request){
        return partnerDAO.addReservation(request);
    }

}
