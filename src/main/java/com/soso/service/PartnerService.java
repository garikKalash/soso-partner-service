package com.soso.service;

import com.soso.models.*;
import com.soso.persistance.PartnerDAO;
import com.soso.service.authentication.AuthenticationTokenService;
import com.soso.service.common_data.CommonDataService;
import com.soso.service.eventListener.EventListenerClient;
import com.soso.utility.GeoCalculator;
import com.soso.utility.UnitType;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */

@Repository
public class PartnerService extends BaseRestClient{
    private final Integer selfId;
    private final CommonDataService commonDataService = new CommonDataService(4);
    private final AuthenticationTokenService authenticationTokenService = new AuthenticationTokenService(3);
    private final EventListenerClient eventListenerClient = new EventListenerClient(6);

    @Autowired
    private PartnerDAO partnerDAO;

    @Autowired
    public PartnerService(@Value("${partnerservice.id}") Integer defaultId) {
        super(defaultId);
        selfId = defaultId;
    }

    public boolean isValidToken(Integer itemId, String token) {
        return authenticationTokenService.isValidToken(selfId, itemId, token);
    }

    public Integer addPartner(Partner partner) {
        return partnerDAO.addPartner(partner);
    }

    public Partner getPartnerById(Integer partnerId) {
        return partnerDAO.getPartnerById(partnerId);
    }

    public List<Feedback> getFeedbacks(Integer partnerId) {
        return partnerDAO.loadFeedbacksByPartnerId(partnerId);
    }


    public Integer signInPartner(String telephone, String password) {
        return partnerDAO.signin(telephone, password);
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


    public Partner getPartnerByTelephone(String telephone) {
        return partnerDAO.getPartnerByTelephone(telephone);
    }

    public Partner getPartnerByUsername(String username) {
        return partnerDAO.getPartnerByUsername(username);
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

    public List<PhotoDto> getPhotosByParentId(Integer partnerId) {
        return partnerDAO.loadPhotosByPartnerId(partnerId);
    }

    public String getPhotoById(Integer photoId) {
        return partnerDAO.getPhotoById(photoId);
    }

    public void deletePhotoById(Integer photoId) {
        partnerDAO.deletePhotoById(photoId);
    }

    public void deleteReservationById(Integer reserveId) {
        partnerDAO.deleteReservationById(reserveId);
    }

    public List<Request> getReservationsByClientId(Integer clientId, Integer status) {
        return partnerDAO.getReservationsByClientId(clientId, status);
    }

    public List<Request> getReservationsByPartnerId(Integer partnierId, Integer status) {
        return partnerDAO.getReservationsByPartnerId(partnierId, status);
    }

    public void updateReserve(Request request) {

        request.getStartTime().setTime(request.getStartTime().getTime() + 4*60 * 1000);
        partnerDAO.updateReservation(request);
    }


    public Integer addReservation(Request request) {
        request.getStartTime().setTime(request.getStartTime().getTime() + 4*60*1000);
        return partnerDAO.addReservation(request);
    }

    public void deleteFollowerByClientPartnerId(Integer clientId, Integer partnerId) {
        partnerDAO.deleteFollowerByClientPartnerId(clientId, partnerId);
    }

    public Integer addFollowerToPartnier(Integer partnerId, Integer clientId) {
        return partnerDAO.addFollowerToPartnier(partnerId, clientId);
    }

    public List<Follower> getFollowersByPartnerId(Integer partnerId) {
        return partnerDAO.getFollowersByPartnerId(partnerId);
    }

    public List<Follower> getFollowersByClientId(Integer clientId) {
        return partnerDAO.getFollowersByClientId(clientId);
    }

    public Integer addServiceDetailToPartner(Integer partnerid, Integer serviceId, Integer defaulttime, Integer price) {
        return partnerDAO.addServiceToPartnier(partnerid, serviceId, defaulttime, price);
    }

    public Integer updatePartnerServiceDetail(PartnerServiceDetail partnerServiceDetail) {
        return partnerDAO.updateServiceDetailForPartner(partnerServiceDetail);
    }

    public void deletePartnerServiceDetail(Integer itemId) {
        partnerDAO.deletePartnerService(itemId);
    }

    public List<PartnerServiceDetail> getPartnerServiceDetailsByPartner(Integer itemId) {
        return partnerDAO.getAllServicesByPartner(itemId);
    }

    public Follower getFollowerById(Integer id) {
        return partnerDAO.getFollowerById(id);
    }


    public List<EventDto> getAutoCompletedRequests() {
        List<Request> requests = partnerDAO.getAllRequests();
        List<EventDto> result = new ArrayList<>();
        for (Request request : requests) {
            if (isNotCompleted(request) && needAutoCompleteRequest(request)) {
                request.setStatus(3);
                partnerDAO.updateReservation(request);
                EventDto eventDto = new EventDto(null, request.getPartnerId(), request.getClientId(), request.getId(), false, null);
                result.add(eventDto);
            }
        }
        return result;
    }

    public List<Partner> getPartnersByServiceTermId(Integer serviceId, String term) {
        return partnerDAO.getPartnersByServiceTermId(serviceId, term);
    }

    private boolean isNotCompleted(Request request) {
        return request.getStatus() != 3;
    }

    private boolean needAutoCompleteRequest(Request request) {
        long startTimeInMs = request.getStartTime().getTime();
        Date afterAddingMins = new Date(startTimeInMs + (request.getDuration()  * 1000));
        return afterAddingMins.getTime() <= new Date().getTime();
    }

    public Request getCrossedRequestFromDuration(Request request){
        long startTimeInMs = request.getStartTime().getTime();
        Date endTime = new Date(startTimeInMs + (request.getDuration()  * 1000 * 60));

            for(Request _request:partnerDAO.getReservationsByPartnerId(request.getPartnerId(),1)){
                long _startTimeInMs = _request.getStartTime().getTime();
                Date _afterAddingMins = new Date(startTimeInMs + (request.getDuration() *  1000 * 60));

                if((endTime.getTime() > _startTimeInMs && startTimeInMs < _startTimeInMs)
                        || (endTime.getTime() > _afterAddingMins.getTime() && startTimeInMs < _startTimeInMs )){
                    return _request;
                }
            }
            return null;
        }

    public Request getCrossedRequestFromStartTime(Request request){
        long startTimeInMs = request.getStartTime().getTime();
        for(Request _request:partnerDAO.getReservationsByPartnerId(request.getPartnerId(),1)){
            Date _endTime = new Date(_request.getStartTime().getTime() + (_request.getDuration() * 60 * 1000));
            if(_endTime.getTime() > startTimeInMs && startTimeInMs > _request.getStartTime().getTime()){
                return _request;
            }
        }
        return null;
    }


    public EventListenerClient eventListenerClient() {
        return this.eventListenerClient;
    }

    public Integer addFeedbackToPartner(Feedback feedback) {
        return partnerDAO.addFeedbackToPartner(feedback);
    }

    public Integer addIsRatedFlagToRequest(Integer requestId) {
        return partnerDAO.setRatedFlagTrue(requestId);
    }

    public Partner getPartnerMainDetailsById(Integer partnerId) {
        return partnerDAO.getPartnerMainDetailsById(partnerId);
    }

    public boolean isNewRequestInValidRange(Request request) {
        List<Request> requests = partnerDAO.getReservationsByPartnerId(request.getPartnerId(), 1);
        long startTimeInMs = request.getStartTime().getTime();
        Date afterAddingMins = new Date(startTimeInMs + (request.getDuration()  * 1000 * 60));
        for (Request _request : requests) {
            if (_request.getStartTime().getTime() < afterAddingMins.getTime()) {
                return false;
            }
        }
        return true;
    }

    public List<Map> getPartnersInGivenRange(Pair<BigDecimal,BigDecimal> myLocation, BigDecimal range, Integer partnerServiceId){
          List<Partner> partnersByServiceId = partnerDAO.getPartnersByServiceId(partnerServiceId);
          List<Map> list = new ArrayList<>();

          partnersByServiceId.forEach(partner -> {
              double distanceForPartner = GeoCalculator.distance(partner.getLatitude().doubleValue(),
                      partner.getLongitude().doubleValue(),
                      myLocation.getKey().doubleValue(),
                      myLocation.getValue().doubleValue(),
                      UnitType.KILOMETER);
               if(distanceForPartner <= range.doubleValue()){
                   list.add(new JsonMapBuilder().add("partner",partner).add("distance",distanceForPartner).build());
               }
          });
          return list;
    }



}
