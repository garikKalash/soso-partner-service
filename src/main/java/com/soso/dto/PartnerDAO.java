package com.soso.dto;

import com.soso.models.Feedback;
import com.soso.models.Partner;
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
    private final String GET_FEEDBACKS_PARTNER_BY_ID_QUERY = "SELECT * FROM public.f_partnerfeedbacks WHERE partnerid = :partnerid";
    private final String GET_IMAGES_PARTNER_BY_ID_QUERY = "SELECT image_path FROM public.f_partnerimages WHERE partner_id = :partnerid";
    private final String GET_FOLLOWERS_PARTNER_BY_ID_QUERY = "SELECT * FROM public.f_partnerimages WHERE partner_id = :partnerid";
    private final String SIGN_IN_PARTNER_QUERY = "SELECT * FROM public.Partner WHERE password = :password AND telephone = :phonenumber";
    private final String UPDATE_LOGO_SRC_PATH_OF_PARTNER_QUERY = "UPDATE public.Partner SET imgpath = :imgpath WHERE id = :id";
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

    public String addPhotoToPartnier(Integer partnerId, String imgPath) {
        String addPhotoQuery = "SELECT addphototopartnier ( :partnerId, :imgPath)";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("partnerId", partnerId);
        paramMap.put("imgPath", imgPath);


        return getNamedParameterJdbcOperations().queryForObject(addPhotoQuery, paramMap, String.class);

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

    public void updateLogosrcPathOfPartner(Integer partnerId, String path) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", partnerId);
        paramMap.put("imgpath", path);
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
        return getNamedParameterJdbcOperations().queryForList(GET_FEEDBACKS_PARTNER_BY_ID_QUERY, paramMap, Feedback.class);
    }

    public List<String> loadPhotosByPartnerId(Integer partnerId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("partnerid", partnerId);
        return getNamedParameterJdbcOperations().queryForList(GET_IMAGES_PARTNER_BY_ID_QUERY, paramMap, String.class);
    }


}
