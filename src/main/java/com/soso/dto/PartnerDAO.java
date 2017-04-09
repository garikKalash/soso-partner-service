package com.soso.dto;

import com.soso.models.Partner;
import com.soso.service.BaseSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */

@Repository
public class PartnerDAO {
    @Autowired
    private NamedParameterJdbcOperations namedParameterJdbcOperations;

    public NamedParameterJdbcOperations getNamedParameterJdbcOperations() {
        return namedParameterJdbcOperations;
    }

    private final String GET_PARTNER_BY_ID_QUERY = "SELECT * FROM public.Partner WHERE id= :id";
    private final String SIGN_IN_PARTNER_QUERY = "SELECT * FROM public.Partner WHERE partner_password = :password AND telephone = :phonenumber";
    private final String LOAD_ALL_PARTNERS_QUERY = "SELECT * FROM public.Partner";


    public Integer create(Partner item) {
        String createUserQuery = "SELECT addPartner ( :description, :birthday, :firstname, :password, :phonenumber)";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("telephone", item.getTelephone());
        paramMap.put("partnerName", item.getPartner_name());
        paramMap.put("password", BaseSecurity.getMd5Version(item.getPassword()));
        paramMap.put("username", item.getPartner_username());
        
        if(item.getServiceId() != null){
           paramMap.put("serviceId", item.getServiceId());
        }
        
        paramMap.put("isAdmin", item.isAdmin());

        return getNamedParameterJdbcOperations().queryForObject(createUserQuery, paramMap, Integer.class);

    }

    public List<Partner> getAllPartners(){
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

    public Partner getPartnerById(Integer PartnerId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", PartnerId);
        return getNamedParameterJdbcOperations().queryForObject(GET_PARTNER_BY_ID_QUERY, paramMap, new BeanPropertyRowMapper<>(Partner.class));

    }


}
