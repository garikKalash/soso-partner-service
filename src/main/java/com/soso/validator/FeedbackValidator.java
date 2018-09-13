package com.soso.validator;

import com.soso.models.Feedback;
import com.soso.models.MessageDto;
import com.soso.service.common_data.CommonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class FeedbackValidator {

    @Autowired
    private CommonDataService commonDataService;

    public void validate(Feedback newFeedback,String language ,Errors errors){
        if(isInvalidFeedback(newFeedback)){
            MessageDto responseEntity =  commonDataService.getMessageByGlobKey("invalidfeedback");
            if(language.compareToIgnoreCase("hay") == 0){
                errors.reject("invalidfeedback", responseEntity.getHay());
            }else if(language.compareToIgnoreCase("eng") == 0){
                errors.reject("invalidfeedback", responseEntity.getEng());
            }
        }
    }

    private boolean isInvalidFeedback(Feedback newFeedback){
        return newFeedback.getClientId() == null ||
                newFeedback.getRate() == null ||
                newFeedback.getRequestId() == null ||
                newFeedback.getPartnerId() == null;
    }

}
