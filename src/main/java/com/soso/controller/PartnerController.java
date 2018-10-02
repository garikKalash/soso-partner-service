package com.soso.controller;

import com.soso.models.*;
import com.soso.service.JsonConverter;
import com.soso.service.PartnerService;
import com.soso.validator.FeedbackValidator;
import com.soso.validator.PartnerValidator;
import com.soso.validator.ReserveValidator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * Created by Garik Kalashyan on 3/4/2017.
 */

@CrossOrigin("*")
@Controller
@RequestMapping("partner")
public class PartnerController {

    private static final String RELATIVE_PATH_FOR_UPLOADS = File.separatorChar  + "work" + File.separatorChar  + "soso-partner-service-uploads" + File.separatorChar ;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private FeedbackValidator feedbackValidator;

    @Autowired
    private ReserveValidator reserveValidator;

    @Autowired
    private PartnerValidator partnerValidator;


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
    public ResponseEntity saveEditedMainInfo(@RequestBody Partner partner,
                                   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language,
                                   Errors errors) throws IOException {
        partnerValidator.validateMainInfo(partner, language, errors);
        if(!errors.hasErrors()){
             partnerService.saveEditedMainInfo(partner.getId(), partner.getTelephone());
             return new ResponseEntity("edited", HttpStatus.OK);
        }else{
             return new ResponseEntity(constructMapFromErrors(errors), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/saveEditedAddress", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveEditedAddress(@RequestBody Address address,
                                  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language,
                                  Errors errors) throws IOException {
        partnerValidator.validateEditedAddress(address, language, errors);
        if(!errors.hasErrors()){
            partnerService.saveEditedAddress(address.getPartnerId(), address.getLatitude(), address.getLongitude(), address.getAddress());
            return new ResponseEntity(address, HttpStatus.OK);
        }else{
            return new ResponseEntity(constructMapFromErrors(errors), HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/saveEditedNotice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveEditedNotice(@RequestBody Partner partner, HttpServletResponse response) throws IOException {
        partnerService.saveEditedNotice(partner.getId(), partner.getNotices());
    }

    @RequestMapping(value = "/partners/{partnerId}", method = RequestMethod.GET)
    public ResponseEntity<Partner> getPartnerById(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response, HttpServletRequest request) throws IOException {
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

        return new ResponseEntity<>(partner, HttpStatus.OK);
    }


    @RequestMapping(value = "/partnermaindetails/{partnerId}", method = RequestMethod.GET)
    public ResponseEntity<Partner> getPartnerMainDetailsById(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setCharacterEncoding("UTF-8");
        Partner partner = partnerService.getPartnerMainDetailsById(partnerId);
        if (partner.getImgId() != null) {
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + partner.getImgId());
        } else {
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + 39);

        }
        return new ResponseEntity<>(partner, HttpStatus.OK);
    }

    @RequestMapping(value = "/partners", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Partner>> getAllPartners(){
        List<Partner> partnerList = partnerService.getAllPartners();
        return new ResponseEntity<>(partnerList, HttpStatus.OK);
    }

    @RequestMapping(value = "/accountImage/{partnerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> getPartnerAccountPage(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
        Partner partner = partnerService.getPartnerById(partnerId);
        if (partner != null) {
            Optional<Integer> optionalImgId = Optional.of(partner.getId());
            return new ResponseEntity<>(optionalImgId.orElse(39), HttpStatus.OK); // 39 is the id of default account image path
        }
        return null;
    }

    @RequestMapping(value = "/addImageToPartnier", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<PhotoDto> addImageToPartnier(@RequestParam("file") CommonsMultipartFile file,
                                                     @RequestParam("id") Integer partnerId,
                                                     HttpServletRequest request) {
        System.out.println("***** --> Initializing file with name " + getBasePathOfResources()+ RELATIVE_PATH_FOR_UPLOADS +" <--  *****");
        File directory = new File(getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS);
        String photoPath = getPath(directory, file.getOriginalFilename());
        PhotoDto newPhotoDto = null;
        try {
            System.out.println("***** --> Transfering file with path " + photoPath +" <--  *****");
            file.transferTo(new File(getBasePathOfResources() + photoPath));
            Integer newPhotoId = partnerService.savePhotoToPartnier(partnerId, photoPath);
            String newPathForPhoto = request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + newPhotoId;
            newPhotoDto = new PhotoDto(newPhotoId, partnerId,newPathForPhoto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(newPhotoDto, HttpStatus.OK);
    }

    private String getPath(File directory, String originalFilename) {
        if (directory.exists() && directory.isDirectory()) {
            System.out.println("***** --> Directory is existed " + getBasePathOfResources()+ RELATIVE_PATH_FOR_UPLOADS +" <--  *****");
            return RELATIVE_PATH_FOR_UPLOADS + originalFilename;
        } else if (directory.mkdirs()) {
            System.out.println("***** --> Creating file with name " + getBasePathOfResources()+ RELATIVE_PATH_FOR_UPLOADS +" <--  *****");
            return RELATIVE_PATH_FOR_UPLOADS + originalFilename;
        }
        return null;
    }


    @RequestMapping(value = "/uploadAccountImage", method = RequestMethod.POST, consumes = {"multipart/mixed", "multipart/form-data"})
    @ResponseBody
    public ResponseEntity<String> uploadAccountImage(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer partnerId,
                                     HttpServletRequest request) throws IOException {
        Partner partner = partnerService.getPartnerById(partnerId);
        System.out.println("***** --> Initializing file with name " + getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS + " <--  *****");
        String urlOfNewAccountImage = null;
        File directory = new File(getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS);
        String newLogoPath = getPath(directory, file.getOriginalFilename());
        if (newLogoPath != null) {
            if (partner.getImgId() != null && partner.getImgId() != 39) { // 39 is the id of default image for the new account
                String oldImgPath = partnerService.getPhotoById(partner.getImgId());
                partnerService.deletePhotoFromFiles(getBasePathOfResources() + oldImgPath);
                partnerService.deletePhotoById(partner.getImgId());
            }
            Integer idOfNewPhoto = partnerService.savePhotoToPartnier(null, newLogoPath);
            partnerService.updatePartnerLogo(idOfNewPhoto, partnerId);
            System.out.println("***** --> Transfering file with name " + newLogoPath + " <--  *****");
            BufferedImage image = ImageIO.read(file.getInputStream());
            image = resizeImage(image, 200, 200);
            ImageIO.write(image,"jpg", new File(getBasePathOfResources() + newLogoPath));
            urlOfNewAccountImage = request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + idOfNewPhoto;

        }
        return new ResponseEntity<>(urlOfNewAccountImage, HttpStatus.OK);
    }

    private BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }


    @RequestMapping(value = "/partnerphoto/{photoId}", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getPhotoById(@PathVariable(value = "photoId") Integer photoId, HttpServletResponse response) throws IOException {
        String imgPath = partnerService.getPhotoById(photoId);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        if(imgPath != null){
            return IOUtils.toByteArray(getImageInputStreamByImgPath(getBasePathOfResources() + imgPath));
        }
        return null;
    }

    private InputStream getImageInputStreamByImgPath(String imagePath) throws IOException {
        imagePath = imagePath.replaceAll("/", File.separator);
        imagePath = imagePath.replaceAll("\\\\", File.separator);
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    @RequestMapping(value = "/partnerPhotos/{partnerId}", method = RequestMethod.GET)
    public ResponseEntity<List<PhotoDto>> getPartnerPhotos(@PathVariable(value = "partnerId") Integer partnerId, HttpServletRequest request) throws IOException {
        List<PhotoDto> photoDtos = partnerService.getPhotosByParentId(partnerId);
        for (PhotoDto photoDto : photoDtos) {
            photoDto.setImage_path(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoDto.getId());
        }

        return new ResponseEntity<>(photoDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/addReserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addReserveToPartnier(@RequestBody Request request,
                                               @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language,
                                               Errors errors) throws IOException {
        reserveValidator.validateReserve(request, language,errors);
        if(!errors.hasErrors()){
            Integer createdReservationId = partnerService.addReservation(request);
            request.setId(createdReservationId);
            if (request.getClientId() != null) {
                notifyForNewEventToPartnier(request);
            }
            return new ResponseEntity(createdReservationId, HttpStatus.CREATED);
        }else{
            return new ResponseEntity(constructMapFromErrors(errors), HttpStatus.BAD_REQUEST);
        }
    }

    private void notifyForNewEventToPartnier(Request request){
        EventDto eventDto = new EventDto();
        eventDto.setClientId(request.getClientId());
        eventDto.setRequestId(request.getId());
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

    @RequestMapping(value = "/updatereserve", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateReserve(@RequestBody Request request,
                              @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language,
                              Errors errors) throws IOException {
        reserveValidator.validateReserve(request,language,errors);
        if(!errors.hasErrors()){
            partnerService.updateReserve(request);
            notifyAboutUpdateToClient(request);
            return new ResponseEntity("ok", HttpStatus.OK);
        }else{
            return new ResponseEntity(constructMapFromErrors(errors), HttpStatus.BAD_REQUEST);
        }
    }

    private void notifyAboutUpdateToClient(Request request){
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
    }


    @RequestMapping(value = "/deletereserve/{reserveId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteReserveToPartnier(@PathVariable("reserveId") Integer reserveId, HttpServletResponse response) throws IOException {
        return new ResponseEntity<>(partnerService.deleteReservationById(reserveId) > 0, HttpStatus.OK);
    }


    @RequestMapping(value = "/reservationsforpartner/{partnerId}/{status}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Request>> getReservationsByPartnierId(@PathVariable("partnerId") Integer partnerId, @PathVariable("status") Integer status, HttpServletResponse response) throws IOException {
        List<Request> reservations = partnerService.getReservationsByPartnerId(partnerId, status);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @RequestMapping(value = "/reservationsforclient/{clientId}/{status}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Request>> getReservationsByClientId(@PathVariable("clientId") Integer clientId, @PathVariable("status") Integer status, HttpServletResponse response) throws IOException {
        List<Request> reservations = partnerService.getReservationsByClientId(clientId, status);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }





    @RequestMapping(value = "/addservicedetailtopartner", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> addServiceDetailToPartner(@RequestBody PartnerServiceDetail partnerServiceDetail) throws IOException {
        Integer serviceId = partnerService.addServiceDetailToPartner(partnerServiceDetail.getPartnerId(), partnerServiceDetail.getServiceId(), partnerServiceDetail.getDefaultduration(), partnerServiceDetail.getPrice());
        return new ResponseEntity<>(serviceId, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/updateservicedetailtopartner", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> updateServiceDetailToPartner(@RequestBody PartnerServiceDetail partnerServiceDetail, HttpServletResponse response) throws IOException {
        return new ResponseEntity<>(partnerService.updatePartnerServiceDetail(partnerServiceDetail), HttpStatus.OK);
    }


    @RequestMapping(value = "/deleteservicedetailtopartner/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteServiceDetailToPartner(@PathVariable("id") Integer id) throws IOException {
        return new ResponseEntity<>(partnerService.deletePartnerServiceDetail(id) != 0, HttpStatus.OK);
    }

    @RequestMapping(value = "/deletephotofrompartner/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deletePhotoFromPartner(@PathVariable("id") Integer id) throws IOException {
        String imgPath = partnerService.getPhotoById(id);
        imgPath = imgPath.replaceAll("/", File.separator);
        imgPath = imgPath.replaceAll("\\\\", File.separator);
        if(partnerService.deletePhotoById(id) != 0){
            partnerService.deletePhotoFromFiles(getBasePathOfResources() + imgPath);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getservicedetailsforpartner/{partnerId}", method = RequestMethod.GET)
    public ResponseEntity<List> getServiceDetailForPartner(@PathVariable("partnerId") Integer id) throws IOException {
        return new ResponseEntity<>(partnerService.getPartnerServiceDetailsByPartner(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/getservicedetails/partner/{partnerId}/service/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<PartnerServiceDetail> getServiceDetailForPartner(@PathVariable("partnerId") Integer id, @PathVariable("serviceId") Integer serviceId) throws IOException {
        return new ResponseEntity<>(partnerService.getServiceDetailByPartnerAndByService(id, serviceId), HttpStatus.OK);
    }

    @RequestMapping(value = "/getautocompletedrequests", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List> getAutocompletedRequestsAsEvents() throws IOException {
        return new ResponseEntity<>(partnerService.getAutoCompletedRequests(), HttpStatus.OK);
    }

    @RequestMapping(value = "/addfeedback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addFeedbackToPartner(@RequestBody Feedback feedback, @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language, Errors errors) {
        feedbackValidator.validate(feedback, language, errors);
        if(!errors.hasErrors()){
            Integer newFeedbackId = partnerService.addFeedbackToPartner(feedback);
            if (newFeedbackId != null) {
              return   new ResponseEntity<>(partnerService.addIsRatedFlagToRequest(feedback.getRequestId()) != null, HttpStatus.OK);
            }
        }else{
            return new ResponseEntity(constructMapFromErrors(errors), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @RequestMapping(value = {"/filteredpartners/{serviceId}", "/filteredpartners/{serviceId}/{term}"}, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getPartnersByServiceTermId(@PathVariable("serviceId") Integer serviceId, @PathVariable(value = "term", required = false) String term, HttpServletRequest request) {
        List<Partner> partners;
        if (term != null && !term.isEmpty()) {
            partners = partnerService.getPartnersByServiceTermId(serviceId, term);
        } else {
            partners = partnerService.getPartnersByServiceId(serviceId);
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
        return new ResponseEntity<>(partners, HttpStatus.OK);
    }

    @RequestMapping(value = "/filteredpartnersbyradius/{serviceId}/{radius}/{latitude}/{longitude}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getPartnersInGivenRange(@PathVariable("serviceId") Integer serviceId, @PathVariable("radius") BigDecimal range, @PathVariable("latitude") BigDecimal latitude ,@PathVariable("longitude") BigDecimal longitude ,HttpServletResponse response, HttpServletRequest request){
        return new ResponseEntity<>(partnerService.getPartnersInGivenRange(new javafx.util.Pair<>(latitude, longitude), range, serviceId), HttpStatus.OK);
    }




    private String getBasePathOfResources() {
        return new File(".").getAbsoluteFile().getParentFile().getPath();
    }

    private Map<String, String> constructMapFromErrors(Errors errors){
        Map<String, String> errorsMap =  new HashMap<>();
        errors.getAllErrors().forEach(objectError -> {
            errorsMap.put(objectError.getCode(), objectError.getDefaultMessage());
        });
        return errorsMap;
    }



}
