package com.soso.controller;

import com.soso.models.*;
import com.soso.service.JsonConverter;
import com.soso.service.JsonMapBuilder;
import com.soso.service.PartnerService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Garik Kalashyan on 3/4/2017.
 */

@CrossOrigin("*")
@Controller
@RequestMapping("partner")
public class PartnerController {

    private static final String RELATIVE_PATH_FOR_UPLOADS = "\\work\\soso-partner-uploads\\";

    @Autowired
    private PartnerService partnerService;


    /*   @ModelAttribute
       public void checkRequiredDataFromRequest(@RequestHeader("token") String token,@RequestHeader("partnerId") Integer itemId, HttpServletRequest request) {
           if (!isUnauthorizedRequestType(request)) {
               if(token.isEmpty() && itemId != null && partnerService.isValidToken(itemId,token)){
                   System.out.println("Pre Requesting state is fully processed.");
               }else {
                   try {
                       throw new Exception("The token: "+ token +" is not found in the stored tokens.");
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
           }
       }

   */
    @RequestMapping(value = "/saveEditedMainInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveEditedMainInfo(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        partnerService.saveEditedMainInfo(partner.getId(), partner.getTelephone(), partner.getAddress());
    }

    @RequestMapping(value = "/saveEditedAddress", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveEditedAddress(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        partnerService.saveEditedAddress(partner.getId(), partner.getLatitude(), partner.getLongitude(), partner.getAddress());
    }

    @RequestMapping(value = "/saveEditedNotice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveEditedNotice(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        partnerService.saveEditedNotice(partner.getId(), partner.getNotices());
    }

    @RequestMapping(value = "/partnerRoom/{partnerId}", method = RequestMethod.GET)
    public void getPartnerById(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setCharacterEncoding("UTF-8");
        Partner partner = partnerService.getPartnerById(partnerId);
        partner.setFeedbacks(partnerService.getFeedbacks(partnerId));
        partner.setServices(partnerService.getPartnerServiceDetailsByPartner(partnerId));

        partner.setPhotoDtos(new ArrayList<>());
        for (PhotoDto photoDto : partnerService.getPhotosByParentId(partnerId)) {
            photoDto.setImage_path(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoDto.getId());
            partner.getPhotoDtos().add(photoDto);
        }

        if (partner.getImgId() != null) {
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + partner.getImgId());
        } else {
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + 39);

        }

        String partnerToJsonString = JsonConverter.toJson(new JsonMapBuilder()
                .add("partner", partner)
                .build());
        response.getWriter().write(partnerToJsonString);
    }


    @RequestMapping(value = "/partnermaindetails/{partnerId}", method = RequestMethod.GET)
    public void getPartnerMainDetailsById(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setCharacterEncoding("UTF-8");
        Partner partner = partnerService.getPartnerMainDetailsById(partnerId);
        if (partner.getImgId() != null) {
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + partner.getImgId());
        } else {
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + 39);

        }

        String partnerToJsonString = JsonConverter.toJson(new JsonMapBuilder()
                .add("partner", partner)
                .build());
        response.getWriter().write(partnerToJsonString);
    }

    @RequestMapping(value = "/getAllPartners", method = RequestMethod.GET)
    public void getAllPartners(HttpServletResponse response) throws IOException {
        String partnerToJsonString = JsonConverter.toJson(new JsonMapBuilder()
                .add("partners", partnerService.getAllPartners())
                .build());
        response.getWriter().write(partnerToJsonString);
    }

    @RequestMapping(value = "/accountImage/{partnerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void getPartnerAccountPage(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
        Partner partner = partnerService.getPartnerById(partnerId);
        response.getWriter().flush();

        String accountImgIdJson = JsonConverter.toJson(new JsonMapBuilder()
                .add("imageId", partner.getImgId() != null ? partner.getImgId() : 39)
                .build()); // 39 is the id of default account image path
        response.getWriter().write(accountImgIdJson);
    }

    @RequestMapping(value = "/addImageToPartnier", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public void addImageToPartnier(@RequestParam("file") CommonsMultipartFile file, @RequestParam("id") Integer partnerId, RedirectAttributes redirectAttributes) {
        File directory = new File(getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS);
        String photoPath = null;

        if (directory.exists() && directory.isDirectory()) {
            photoPath = getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS + file.getOriginalFilename();
        } else if (directory.mkdirs()) {
            photoPath = directory.getPath() + "\\" + file.getOriginalFilename();
        }
        try {
            file.transferTo(new File(photoPath));
            partnerService.savePhotoToPartnier(partnerId, photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @RequestMapping(value = "/uploadAccountImage", method = RequestMethod.POST, consumes = {"multipart/mixed", "multipart/form-data"})
    public String uploadAccountImage(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer partnerId,
                                     RedirectAttributes redirectAttributes) throws IOException {

        Partner partner = partnerService.getPartnerById(partnerId);
        File directory = new File(getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS);

        String newLogoPath = null;
        if (directory.exists() && directory.isDirectory()) {
            newLogoPath = getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS + file.getOriginalFilename();
        } else if (directory.mkdirs()) {
            newLogoPath = directory.getPath() + "\\" + file.getOriginalFilename();
        }
        if (newLogoPath != null) {
            if (partner.getImgId() != null) {
                String oldImgPath = partnerService.getPhotoById(partner.getImgId());
                partnerService.deletePartnerOldLogoFromFiles(oldImgPath);
                partnerService.deletePhotoById(partner.getImgId());
            }
            Integer idOfNewPhoto = partnerService.savePhotoToPartnier(null, newLogoPath);
            partnerService.updatePartnerLogo(idOfNewPhoto, partnerId);
            file.transferTo(new File(newLogoPath));
            redirectAttributes.addFlashAttribute("Your account image is changed successfully!");
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/partnerByService/{serviceId}", method = RequestMethod.GET)
    public void getPartnersByServiceId(@PathVariable(value = "serviceId") Integer serviceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        List<Partner> partners = partnerService.getPartnersByServiceId(serviceId);
        response.getWriter().flush();
        for (Partner partner : partners) {
            partner.setFeedbacks(partnerService.getFeedbacks(partner.getId()));
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + partner.getImgId());
            partner.setServices(partnerService.getPartnerServiceDetailsByPartner(partner.getId()));
            partner.setPhotoDtos(new ArrayList<>());
            for (PhotoDto photoDto : partnerService.getPhotosByParentId(partner.getId())) {
                photoDto.setImage_path(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoDto.getId());
                partner.getPhotoDtos().add(photoDto);
            }
        }
        String partnerAccountImageString = JsonConverter.toJson(new JsonMapBuilder()
                .add("partners", partners)
                .build());
        response.getWriter().write(partnerAccountImageString);
    }


    @RequestMapping(value = "/partnerphoto/{photoId}", method = RequestMethod.GET)
    public void getPhotoById(@PathVariable(value = "photoId") Integer photoId, HttpServletResponse response) throws IOException {
        String imgPath = partnerService.getPhotoById(photoId);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(getImageInputStreamByImgPath(imgPath), response.getOutputStream());
    }

    private InputStream getImageInputStreamByImgPath(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    @RequestMapping(value = "/partnerPhotos/{partnerId}", method = RequestMethod.GET)
    public void getPartnerPhotos(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        List<PhotoDto> photoDtos = partnerService.getPhotosByParentId(partnerId);
        response.getWriter().flush();

        for (PhotoDto photoDto : photoDtos) {
            photoDto.setImage_path(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoDto.getId());
        }
        String partnerAccountImageString = JsonConverter.toJson(new JsonMapBuilder()
                .add("photos", photoDtos)
                .build());
        response.getWriter().write(partnerAccountImageString);
    }

    @RequestMapping(value = "/addReserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addReserveToPartnier(@RequestBody Request request, HttpServletResponse response) throws IOException {
        boolean thereIsValidation = false;
        JsonMapBuilder errorJsonMapBuilder = new JsonMapBuilder();
        if (request.getStatus() != 2 && (request.getDuration() == null || request.getDuration() <= 0)) {
            errorJsonMapBuilder.add("isWrongDuration", Boolean.TRUE.toString());
            thereIsValidation = true;
        }
        if (request.getDuration() != null && partnerService.getCrossedRequestFromDuration(request) != null) {
            errorJsonMapBuilder.add("crossedRequestDuration", partnerService.getCrossedRequestFromDuration(request));
            thereIsValidation = true;
        }
        if (request.getStartTime() != null && partnerService.getCrossedRequestFromStartTime(request) != null) {
            errorJsonMapBuilder.add("crossedRequestStart", partnerService.getCrossedRequestFromStartTime(request));
            thereIsValidation = true;
        }

        if (!thereIsValidation) {
            Integer createdReservationId = partnerService.addReservation(request);
            if (request.getClientId() != null) {
                EventDto eventDto = new EventDto();
                eventDto.setClientId(request.getClientId());
                eventDto.setRequestId(createdReservationId);
                eventDto.setPartnerId(request.getPartnerId());
                eventDto.setSeen(false);

                String requestJson = JsonConverter.toJson(eventDto);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                partnerService.eventListenerClient().getRestTemplate()
                        .postForObject(partnerService.eventListenerClient()
                                .getDestinationService().getUrl() + "eventsListener/addneweventtopartner", entity, String.class);
            }
            response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                    .add("newReservationId", createdReservationId)
                    .build()));
        } else {
            response.getWriter().write(JsonConverter.toJson(errorJsonMapBuilder.build()));
        }

    }

    @RequestMapping(value = "/updateReserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateReserve(@RequestBody Request request, HttpServletResponse response) throws IOException {
        boolean thereIsValidation = false;
        JsonMapBuilder errorJsonMapBuilder = new JsonMapBuilder();
        if (request.getStatus() == 1 && (request.getDuration() == null || request.getDuration() <= 0)) {
            errorJsonMapBuilder.add("isWrongDuration", Boolean.TRUE.toString());
            thereIsValidation = true;
        }
        if (request.getStatus() == 1 && request.getDuration() != null && partnerService.getCrossedRequestFromDuration(request) != null) {
            errorJsonMapBuilder.add("crossedRequestDuration", JsonConverter.toJson(partnerService.getCrossedRequestFromDuration(request)));
            thereIsValidation = true;
        }
        if (request.getStatus() == 1 && request.getStartTime() != null && partnerService.getCrossedRequestFromStartTime(request) != null) {
            errorJsonMapBuilder.add("crossedRequestStart", JsonConverter.toJson(partnerService.getCrossedRequestFromStartTime(request)));
            thereIsValidation = true;
        }


        if (!thereIsValidation) {

            partnerService.updateReserve(request);
            EventDto eventDto = new EventDto();
            eventDto.setPartnerId(request.getPartnerId());
            eventDto.setRequestId(request.getId());
            eventDto.setClientId(request.getClientId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (request.getClientId() != null && request.getStatus() == 1) {
                String requestJson = JsonConverter.toJson(eventDto);
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                partnerService.eventListenerClient().getRestTemplate().postForObject(partnerService.eventListenerClient().getDestinationService().getUrl() + "eventsListener/addacceptedeventtoclient", entity, String.class);
            } else if (request.getClientId() != null && request.getStatus() == 3) {
                eventDto.setSeen(false);

                String requestJson = JsonConverter.toJson(eventDto);
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                partnerService.eventListenerClient().getRestTemplate().postForObject(partnerService.eventListenerClient().getDestinationService().getUrl() + "eventsListener/adddoneeventtoclient", entity, String.class);

            }
        } else {
            response.getWriter().write(JsonConverter.toJson(errorJsonMapBuilder.build()));
        }
    }


    @RequestMapping(value = "/deletereserve/{reserveId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteReserveToPartnier(@PathVariable("reserveId") Integer reserveId, HttpServletResponse response) throws IOException {
        partnerService.deleteReservationById(reserveId);
    }


    @RequestMapping(value = "/reservationsforpartner/{partnerId}/{status}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getReservationsByPartnierId(@PathVariable("partnerId") Integer partnerId, @PathVariable("status") Integer status, HttpServletResponse response) throws IOException {
        List<Request> reservations = partnerService.getReservationsByPartnerId(partnerId, status);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder().add("reservations", reservations).build()));
    }

    @RequestMapping(value = "/reservationsforclient/{clientId}/{status}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getReservationsByClientId(@PathVariable("clientId") Integer clientId, @PathVariable("status") Integer status, HttpServletResponse response) throws IOException {
        List<Request> reservations = partnerService.getReservationsByClientId(clientId, status);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder().add("reservations", reservations).build()));
    }

    @RequestMapping(value = "/deleteFollowerByClientPartnerId/{clientId}/{partnerId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteFollowerByClientPartnerId(@PathVariable("clientId") Integer clientId, @PathVariable("partnerId") Integer partnerId) {
        partnerService.deleteFollowerByClientPartnerId(clientId, partnerId);
    }

    @RequestMapping(value = "/addfollowertopartner", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addFollowerToPartner(@RequestBody Follower follower, HttpServletResponse response) throws IOException {
        Integer newFollowerId = partnerService.addFollowerToPartnier(follower.getPartnerId(), follower.getClientId());
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                .add("newFollowerId", newFollowerId).build()));
    }

    @RequestMapping(value = "/followersforpartner/{partnerId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getFollowersByPartnerId(@PathVariable("partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
        List<Follower> followers = partnerService.getFollowersByPartnerId(partnerId);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder().add("followers", followers).build()));
    }

    @RequestMapping(value = "/followersforclient/{clientId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getFollowersByClientId(@PathVariable("clientId") Integer partnerId, HttpServletResponse response) throws IOException {
        List<Follower> followers = partnerService.getFollowersByPartnerId(partnerId);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder().add("followers", followers).build()));
    }


    @RequestMapping(value = "/follower/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getFollowerById(@PathVariable("id") Integer partnerId, HttpServletResponse response) throws IOException {
        Follower follower = partnerService.getFollowerById(partnerId);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder().add("follower", follower).build()));
    }


    @RequestMapping(value = "/addpartner", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerPartner(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        boolean thereIsValidation = false;
        JsonMapBuilder errorJsonMapBuilder = new JsonMapBuilder();
        if (partner.getTelephone() == null || partner.getTelephone().isEmpty()) {
            errorJsonMapBuilder.add("isShortTelephone", Boolean.TRUE.toString());
            thereIsValidation = true;
        } else if (partnerService.getPartnerByTelephone(partner.getTelephone()) != null) {
            errorJsonMapBuilder.add("isDoubleTelephone", Boolean.TRUE.toString());
            thereIsValidation = true;
        }
        if (partner.getPassword() == null || partner.getPassword().isEmpty() || partner.getPassword().length() < 3) {
            errorJsonMapBuilder.add("isInvalidPassword", Boolean.TRUE.toString());
            thereIsValidation = true;
        }
        if (partner.getUsername() == null || partner.getUsername().isEmpty() || partner.getUsername().length() < 3) {
            errorJsonMapBuilder.add("isInvalidUsername", Boolean.TRUE.toString());
            thereIsValidation = true;
        } else if (partnerService.getPartnerByUsername(partner.getUsername()) != null) {
            errorJsonMapBuilder.add("isDoubleUsername", Boolean.TRUE.toString());
            thereIsValidation = true;
        }
        if (partner.getName() == null || partner.getName().isEmpty() || partner.getName().length() < 3) {
            errorJsonMapBuilder.add("isInvalidName", Boolean.TRUE.toString());
            thereIsValidation = true;
        }
        if (partner.getServiceId() == null) {
            errorJsonMapBuilder.add("isInvalidServiceId", Boolean.TRUE.toString());
            thereIsValidation = true;
        }

        if (!thereIsValidation) {
            Integer newPartnerId = partnerService.addPartner(partner);
            response.getWriter().write(JsonConverter.toJson(new
                    JsonMapBuilder()
                    .add("newPartnerId", newPartnerId)
                    .build()));

        } else {
            response.getWriter().write(JsonConverter.toJson(errorJsonMapBuilder.build()));
        }
    }


    @RequestMapping(value = "/signinpartner", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void signin(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        boolean thereIsValidation = false;
        JsonMapBuilder errorJsonMapBuilder = new JsonMapBuilder();
        if (partner.getTelephone() == null || partner.getTelephone().isEmpty()) {
            errorJsonMapBuilder.add("isShortTelephone", Boolean.TRUE.toString());
            thereIsValidation = true;
        }
        if (partner.getPassword() == null || partner.getPassword().isEmpty() || partner.getPassword().length() < 3) {
            errorJsonMapBuilder.add("isInvalidPassword", Boolean.TRUE.toString());
            thereIsValidation = true;
        }

        if (!thereIsValidation) {

            Integer partnerId = partnerService.signInPartner(partner.getTelephone(), partner.getPassword());
            if (partnerId != null) {
                response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                        .add("partnerId", partnerId)
                        .add("isShortTelephone", Boolean.FALSE.toString())
                        .add("isInvalidPassword", Boolean.FALSE.toString())
                        .build()));
            } else {
                response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                        .add("wrongpartner", Boolean.TRUE.toString())
                        .build()));
            }
        } else {
            response.getWriter().write(JsonConverter.toJson(errorJsonMapBuilder.build()));
        }
    }

    @RequestMapping(value = "/addservicedetailtopartner", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addServiceDetailToPartner(@RequestBody PartnerServiceDetail partnerServiceDetail, HttpServletResponse response) throws IOException {
        Integer serviceId = partnerService.addServiceDetailToPartner(partnerServiceDetail.getPartnerId(), partnerServiceDetail.getServiceId(), partnerServiceDetail.getDefaultduration(), partnerServiceDetail.getPrice());
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                .add("serviceId", serviceId)
                .build()));
    }

    @RequestMapping(value = "/updateservicedetailtopartner", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateServiceDetailToPartner(@RequestBody PartnerServiceDetail partnerServiceDetail, HttpServletResponse response) throws IOException {
        Integer count = partnerService.updatePartnerServiceDetail(partnerServiceDetail);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                .add("count", count)
                .build()));
    }


    @RequestMapping(value = "/deleteservicedetailtopartner/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteServiceDetailToPartner(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        partnerService.deletePartnerServiceDetail(id);
    }

    @RequestMapping(value = "/deletephotofrompartner/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deletePhotoFromPartner(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        partnerService.deletePhotoById(id);
    }

    @RequestMapping(value = "/getservicedetailsforpartner/{partnerId}", method = RequestMethod.GET)
    public void getServiceDetailForPartner(@PathVariable("partnerId") Integer id, HttpServletResponse response) throws IOException {
        List<PartnerServiceDetail> serviceDetailList = partnerService.getPartnerServiceDetailsByPartner(id);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                .add("services", serviceDetailList)
                .build()));

    }

    @RequestMapping(value = "/getautocompletedrequests", method = RequestMethod.GET)
    @ResponseBody
    public void getAutocompletedRequestsAsEvents(HttpServletResponse response) throws IOException {
        List<EventDto> eventDtos = partnerService.getAutoCompletedRequests();
        response.getWriter().write(JsonConverter.toJson(eventDtos));
    }

    @RequestMapping(value = "/addfeedback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addFeedbackToPartner(@RequestBody Feedback feedback, HttpServletResponse response) {
        Integer newFeedbackId = partnerService.addFeedbackToPartner(feedback);
        if (newFeedbackId != null) {
            partnerService.addIsRatedFlagToRequest(feedback.getRequestId());
        }
        try {
            response.getWriter().write("allisok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/filteredpartners/{serviceId}/{term}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getPartnersByServiceTermId(@PathVariable("serviceId") Integer serviceId, @PathVariable("term") String term, HttpServletResponse response, HttpServletRequest request) {
        response.setCharacterEncoding("UTF-8");
        List<Partner> partners;
        if (term != null && !term.isEmpty()) {
            partners = partnerService.getPartnersByServiceTermId(serviceId, term);
        } else {
            partners = partnerService.getPartnersByServiceId(serviceId);
        }

        try {
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Partner partner : partners) {
            partner.setFeedbacks(partnerService.getFeedbacks(partner.getId()));
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + partner.getImgId());
            partner.setServices(partnerService.getPartnerServiceDetailsByPartner(partner.getId()));

            partner.setPhotoDtos(new ArrayList<>());
            for (PhotoDto photoDto : partnerService.getPhotosByParentId(partner.getId())) {
                photoDto.setImage_path(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoDto.getId());
                partner.getPhotoDtos().add(photoDto);
            }
        }
        String partnersString = JsonConverter.toJson(new JsonMapBuilder()
                .add("partners", partners)
                .build());
        try {
            response.getWriter().write(partnersString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isUnauthorizedRequestType(HttpServletRequest request) {
        return request.getRequestURI().equals("/partner/signinpartner/")
                || request.getRequestURI().equals("/partner/addpartner/")
                || request.getRequestURI().contains("/partner/partnerphoto/");
    }


    private String getBasePathOfResources() {
        return new File(".").getAbsoluteFile().getParentFile().getParentFile().getPath();
    }


}
