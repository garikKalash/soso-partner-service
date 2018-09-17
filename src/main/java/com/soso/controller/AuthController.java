package com.soso.controller;

import com.soso.models.Partner;
import com.soso.service.PartnerService;
import com.soso.validator.PartnerValidator;
import com.soso.web.PartnerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Garik.Kalashyan on 12/31/2016.
 */
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/authenticate")
public class AuthController {

    @Autowired
    private PartnerValidator partnerValidator;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public void datas() {
        System.out.println(" SosoPartner Server is running. Request type GET.");
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(){
        return "logout";
    }

    @RequestMapping(value = "/signinpartner", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signin(@RequestBody Partner partner,
                                 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language,
                                 Errors errors) throws IOException {
        partnerValidator.validateForSignin(partner, language, errors);

        if (!errors.hasErrors()) {

            Partner loadedPartner = partnerService.getPartnerByTelephone(partner.getTelephone());
            if (loadedPartner != null) {
               if(passwordEncoder.matches(partner.getPassword(), loadedPartner.getPassword())){
                  return new ResponseEntity(loadedPartner, HttpStatus.OK);
               }else{
                  return new ResponseEntity("Incorrect details", HttpStatus.UNAUTHORIZED);
               }

            } else {
                return new ResponseEntity(-1, HttpStatus.NO_CONTENT);
            }
        } else {
            return new ResponseEntity(constructMapFromErrors(errors), HttpStatus.BAD_REQUEST);
        }
        }

    private Map<String, String> constructMapFromErrors(Errors errors){
        Map<String, String> errorsMap =  new HashMap<>();
        errors.getAllErrors().forEach(objectError -> {
            errorsMap.put(objectError.getCode(), objectError.getDefaultMessage());
        });
        return errorsMap;
    }

}
