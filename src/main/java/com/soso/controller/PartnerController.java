package com.soso.controller;

import com.soso.models.Partner;
import com.soso.service.JsonConverter;
import com.soso.service.JsonMapBuilder;
import com.soso.service.PartnerService;
import com.soso.service.authentication.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Garik Kalashyan on 3/4/2017.
 */

@CrossOrigin("*")
@Controller
@RequestMapping("partner")
public class PartnerController {
    
    @Autowired
    private PartnerService partnerService;


    /*@ModelAttribute
    public void checkRequiredDataFromRequest(@RequestHeader("token") String token) throws NotFoundException {
        if (token != null && !token.isEmpty()) {
            if(partnerService.isValidToken(token)){
                System.out.println("Pre Requesting state is fully processed.");
            }else {
                throw new NotFoundException("The token: "+ token +" is not found in the stored tokens.");
            }
        }
    }*/


    @RequestMapping(value = "/connectToSystem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void registerPartner(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        Integer newPartnerId = partnerService.createPartner(partner);
        if (newPartnerId != null) {
            partner.setId(newPartnerId);
            partner.setPassword("If you read this in browser so UZBAGOYZYA!");

            partnerService.createToken(partner.getTelephone());

            response.getWriter().write(JsonConverter.toJson(new
                    JsonMapBuilder()
                    .add("provider", partner)
                    .add("token", AuthenticationTokenService.getCurrentAuthenticationToken())
                    .build()));
        } else {
            response.getWriter().write("something goes wrong");
        }
    }

    @RequestMapping(value = "/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void signin(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        Integer providerId = partnerService.signInPartner(partner);
        if (providerId != null) {
            partner.setId(providerId);
            partner.setPassword("UZBAGOYZYA");

            partnerService.createToken(partner.getTelephone());
            String providerToJsonString = JsonConverter.toJson(new JsonMapBuilder()
                    .add("provider", partner)
                    .add("token", AuthenticationTokenService.getCurrentAuthenticationToken())
                    .build());

            response.getWriter().write(providerToJsonString);
        } else {
            response.getWriter().write("Partner doesn't exists.");
        }

    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout(@RequestHeader("token") String token) {
        partnerService.removeToken(token);
    }

    @RequestMapping(value = "/partnerRoom", params = {"partnerId"}, method = RequestMethod.GET)
    public void getPartnerById(@RequestParam(value = "partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
        Partner partner = partnerService.getPartnerById(partnerId);
        String partnerToJsonString = JsonConverter.toJson(new JsonMapBuilder()
                .add("partner", partner)
                .build());
        response.getWriter().write(partnerToJsonString);
    }

    @RequestMapping(value = "/getAllPartners",method = RequestMethod.GET)
    public void getAllPartners(HttpServletResponse response) throws IOException {
        String partnerToJsonString = JsonConverter.toJson(new JsonMapBuilder()
                .add("partners",  partnerService.getAllPartners())
                .build());
        response.getWriter().write(partnerToJsonString);
    }



}
