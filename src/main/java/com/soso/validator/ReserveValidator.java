package com.soso.validator;

import com.soso.models.MessageDto;
import com.soso.models.Request;
import com.soso.service.PartnerService;
import com.soso.service.common_data.CommonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class ReserveValidator {

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private PartnerService partnerService;

    public void validateReserve(Request request, String lang ,Errors errors){
         if (request.getStatus() != 2 && (request.getDuration() == null || request.getDuration() <= 0)) {
             MessageDto messageDto = commonDataService.getMessageByGlobKey("invalidduration");
             if(lang.compareToIgnoreCase("hay") == 0){
                 errors.reject("isWrongDuration", messageDto.getHay());
             }else{
                 errors.reject("isWrongDuration", messageDto.getEng());
             }
             return;
         }
         if (request.getDuration() != null && partnerService.getCrossedRequest(request) != null) {
             MessageDto messageDto = commonDataService.getMessageByGlobKey("crossedrequestduration");
             if(lang.compareToIgnoreCase("hay") == 0){
                 errors.reject("crossedrequestduration", messageDto.getHay());
             }else{
                 errors.reject("crossedrequestduration", messageDto.getEng());
             }
         }
     }
}
