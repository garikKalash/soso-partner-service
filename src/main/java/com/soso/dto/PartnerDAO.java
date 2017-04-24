package com.soso.dto;

import com.soso.models.Feedback;
import com.soso.models.Partner;
import com.soso.models.Request;
import com.soso.service.BaseSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */

@Repository
public class PartnerDAO {
    private final String GET_PARTNER_BY_ID_QUERY = "SELECT * FROM public.Partner WHERE id= :id";
    private final String GET_PHOTO_BY_ID_QUERY = "SELECT image_path FROM public.f_partnerimages WHERE id= :id";
    private final String DELETE_PHOTO_BY_ID_QUERY = "DELETE FROM public.f_partnerimages WHERE id= :id";
    private final String DELETE_RESERVATION_BY_ID_QUERY = "DELETE FROM public.f_partnerrequests WHERE id= :id";
    private final String GET_PARTNERS_BY_SERVICE_ID_QUERY = "SELECT * FROM public.Partner WHERE serviceid= :serviceid";
    private final String GET_FEEDBACKS_PARTNER_BY_ID_QUERY = "SELECT * FROM public.f_partnerfeedbacks WHERE partnerid = :partnerid";
    private final String GET_RESERVATIONS_BY_PARTNER_ID_QUERY = "SELECT * FROM public.f_partnerrequests WHERE partnerid = :partnerid";
    private final String GET_RESERVATIONS_BY_CLIENT_ID_QUERY = "SELECT * FROM public.f_partnerrequests WHERE clientid = :clientid";
    private final String GET_IMAGES_PARTNER_BY_ID_QUERY = "SELECT id FROM public.f_partnerimages WHERE partner_id = :partnerid";
    private final String GET_FOLLOWERS_PARTNER_BY_ID_QUERY = "SELECT * FROM public.f_partnerimages WHERE partner_id = :partnerid";
    private final String SIGN_IN_PARTNER_QUERY = "SELECT * FROM public.Partner WHERE password = :password AND telephone = :phonenumber";
    private final String UPDATE_LOGO_SRC_PATH_OF_PARTNER_QUERY = "UPDATE public.Partner SET imgid = :imgid WHERE id = :id";
    private final String UPDATE_TELEPHONE_OF_PARTNER_QUERY = "UPDATE public.Partner SET telephone = :telephone  WHERE id = :id";
    private final String UPDATE_LONG_ADDRESS_OF_PARTNER_QUERY = "UPDATE public.Partner SET address = :address,longitude = :longitude, latitude = :latitude  WHERE id = :id";
    private final String UPDATE_NOTICE_OF_PARTNER_QUERY = "UPDATE public.Partner SET notices = :notice  WHERE id = :id";
    private final String LOAD_ALL_PARTNERS_QUERY = "SELECT * FROM public.Partner";
    @Autowired
    private NamedParameterJdbcOperations namedParameterJdbcOperations;

    public NamedParameterJdbcOperations getNamedParameterJdbcOperations() {
        return namedParameterJdbcOperations;
    }

    public Integer create(Partner item) {
        String createUserQuery = "SELECT addPartner ( :description, :birthday, :firstname, :password, :phonenumber)";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("telephone", item.getTelephone());
        paramMap.put("partnerName", item.getName());
        paramMap.put("password", BaseSecurity.getMd5Version(item.getPassword()));
        paramMap.put("username", item.getUsername());

        if (item.getServiceId() != null) {
            paramMap.put("serviceId", item.getServiceId());
        }

        paramMap.put("isAdmin", item.isAdmin());

        return getNamedParameterJdbcOperations().queryForObject(createUserQuery, paramMap, Integer.class);

    }

    public Integer addPhotoToPartnier(Integer partnerId, String imgPath) {
        String addPhotoQuery = "SELECT addphototopartnier ( :partnerId, :imgPath)";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("partnerId", partnerId);
        paramMap.put("imgPath", imgPath);


        return getNamedParameterJdbcOperations().queryForObject(addPhotoQuery, paramMap, Integer.class);

    }

    public List<Partner> getAllPartners() {
        return getNamedParameterJdbcOperations().query(LOAD_ALL_PARTNERS_QUERY, new BeanPropertyRowMapper<>(Partner.class));
    }


    public Integer update(Partner item) {
        return null;
    }


    public Integer delete(Partner item) {
        return null;
    }

    public Integer signin(Partner partner) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("telephone", partner.getTelephone());
        paramMap.put("password", BaseSecurity.getMd5Version(partner.getPassword()));
        Partner responsePartner = getNamedParameterJdbcOperations().queryForObject(SIGN_IN_PARTNER_QUERY, paramMap, new BeanPropertyRowMapper<Partner>(Partner.class));

        return responsePartner == null ? null : responsePartner.getId();
    }

    public void updateLogosrcPathOfPartner(Integer partnerId, Integer imgid) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", partnerId);
        paramMap.put("imgid", imgid);
        getNamedParameterJdbcOperations().update(UPDATE_LOGO_SRC_PATH_OF_PARTNER_QUERY, paramMap);
    }

    public void saveEditedMainInfoOfPartner(Integer partnerId, String editedAddress, String editedTelephone) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", partnerId);
        if (editedTelephone != null && !editedTelephone.isEmpty()) {
            paramMap.put("telephone", editedTelephone);
            getNamedParameterJdbcOperations().update(UPDATE_TELEPHONE_OF_PARTNER_QUERY, paramMap);
        }
    }

    public void saveEditedAddress(Integer partnerId, BigDecimal latitude, BigDecimal lotitude, String address) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", partnerId);
        paramMap.put("address", address);
        paramMap.put("latitude", latitude);
        paramMap.put("longitude", lotitude);
        getNamedParameterJdbcOperations().update(UPDATE_LONG_ADDRESS_OF_PARTNER_QUERY, paramMap);

    }

    public void saveEditedNotice(Integer partnerId, String notice) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", partnerId);
        paramMap.put("notice", notice);
        getNamedParameterJdbcOperations().update(UPDATE_NOTICE_OF_PARTNER_QUERY, paramMap);

    }

    public Partner getPartnerById(Integer partnerId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", partnerId);
        return getNamedParameterJdbcOperations().queryForObject(GET_PARTNER_BY_ID_QUERY, paramMap, new BeanPropertyRowMapper<>(Partner.class));

    }

    public List<Feedback> loadFeedbacksByPartnerId(Integer partnerId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("partnerid", partnerId);
        return getNamedParameterJdbcOperations().query(GET_FEEDBACKS_PARTNER_BY_ID_QUERY, paramMap, new BeanPropertyRowMapper<>(Feedback.class));
    }

    public List<Integer> loadPhotosByPartnerId(Integer partnerId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("partnerid", partnerId);
        return getNamedParameterJdbcOperations().queryForList(GET_IMAGES_PARTNER_BY_ID_QUERY, paramMap, Integer.class);
    }

    public List<Partner> getPartnersByServiceId(Integer serviceId){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("serviceid", serviceId);
        return getNamedParameterJdbcOperations().query(GET_PARTNERS_BY_SERVICE_ID_QUERY,paramMap, new BeanPropertyRowMapper<>(Partner.class));
    }

    public String getPhotoById(Integer photoId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", photoId);
        return getNamedParameterJdbcOperations().queryForObject(GET_PHOTO_BY_ID_QUERY, paramMap, String.class);

    }

    public void deletePhotoById(Integer photoId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", photoId);
       getNamedParameterJdbcOperations().update(DELETE_PHOTO_BY_ID_QUERY, paramMap);

    }

    public Integer addReservation(Request request){
        String addReservationQuery = "SELECT addreservetopartnier ( :clientid, :partnerid, :starttime, :description, :status, :responsetext, :duration)";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientid", request.getClientId());
        paramMap.put("partnerid", request.getPartnerId());
        paramMap.put("starttime", request.getStartTime());
        paramMap.put("description", request.getDescription());
        paramMap.put("status", request.getStatus());
        paramMap.put("responsetext", request.getResponseText());
        paramMap.put("duration", request.getDuration());

        return getNamedParameterJdbcOperations().queryForObject(addReservationQuery, paramMap, Integer.class);

    }

    public List<Request> getReservationsByPartnerId(Integer partnerId){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("partnerid", partnerId);
        return getNamedParameterJdbcOperations().query(GET_RESERVATIONS_BY_PARTNER_ID_QUERY,paramMap, new BeanPropertyRowMapper<>(Request.class));

    }

    public List<Request> getReservationsByClientId(Integer clientId){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientid", clientId);
        return getNamedParameterJdbcOperations().query(GET_RESERVATIONS_BY_CLIENT_ID_QUERY,paramMap, new BeanPropertyRowMapper<>(Request.class));

    }

    public void deleteReservationById(Integer reserveId){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", reserveId);
        getNamedParameterJdbcOperations().update(DELETE_RESERVATION_BY_ID_QUERY, paramMap);
    }


}
