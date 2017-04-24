package com.soso.controller;

import com.soso.models.Partner;
import com.soso.models.Request;
import com.soso.service.JsonConverter;
import com.soso.service.JsonMapBuilder;
import com.soso.service.PartnerService;
import com.soso.service.authentication.AuthenticationTokenService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @RequestMapping(value = "/partnerRoom/{partnerId}", method = RequestMethod.GET)
    public void getPartnerById(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setCharacterEncoding("UTF-8");
        Partner partner = partnerService.getPartnerById(partnerId);
        partner.setFeedbacks(partnerService.getFeedbacks(partnerId));
        partner.setImages(new ArrayList<>());
        for (Integer photoId : partnerService.getPhotosByParentId(partnerId)) {
            partner.getImages().add(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoId);
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
    public void addImageToPartnier(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer partnerId, RedirectAttributes redirectAttributes) {

        try {
            String photoPath = getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS + file.getOriginalFilename();
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
        String newLogoPath = getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS + file.getOriginalFilename();
        if (partner.getImgId() != null) {
            String oldImgPath = partnerService.getPhotoById(partner.getImgId());
            partnerService.deletePartnerOldLogoFromFiles(oldImgPath);
            partnerService.deletePhotoById(partner.getImgId());
        }
        Integer idOfNewPhoto = partnerService.savePhotoToPartnier(null, newLogoPath);
        partnerService.updatePartnerLogo(idOfNewPhoto, partnerId);
        file.transferTo(new File(newLogoPath));
        redirectAttributes.addFlashAttribute("Your account image is changed successfully!");

        return "redirect:/";
    }

    @RequestMapping(value = "/partnerByService/{serviceId}", method = RequestMethod.GET)
    public void getPartnersByServiceId(@PathVariable(value = "serviceId") Integer serviceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        List<Partner> partners = partnerService.getPartnersByServiceId(serviceId);
        response.getWriter().flush();
        for (Partner partner : partners) {
            partner.setFeedbacks(partnerService.getFeedbacks(partner.getId()));
            partner.setImgpath(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/accountImage/" + partner.getId());
            partner.setImages(new ArrayList<>());
            for (Integer photoId : partnerService.getPhotosByParentId(partner.getId())) {
                partner.getImages().add(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoId);
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
        List<Integer> photoIds = partnerService.getPhotosByParentId(partnerId);
        response.getWriter().flush();
        List<String> paths = new ArrayList<>();

        for (Integer photoId : photoIds) {
            paths.add(request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + "/partner/partnerphoto/" + photoId);
        }
        String partnerAccountImageString = JsonConverter.toJson(new JsonMapBuilder()
                .add("photos", paths)
                .build());
        response.getWriter().write(partnerAccountImageString);
    }

    @RequestMapping(value = "/addReserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addReserveToPartnier(@RequestBody Request request, HttpServletResponse response) throws IOException {
        Integer createdReservationId = partnerService.addReservation(request);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder()
                .add("newReservationId", createdReservationId)
                .build()));


    }

    @RequestMapping(value = "/deletereserve/{reserveId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteReserveToPartnier(@PathVariable("reserveId") Integer reserveId, HttpServletResponse response) throws IOException {
        partnerService.deleteReservationById(reserveId);
    }


    @RequestMapping(value = "/reservationsforpartner/{partnerId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getReservationsByPartnierId(@PathVariable("partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
      List<Request> reservations =   partnerService.getReservationsByPartnerId(partnerId);
      response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder().add("reservations",reservations).build()));
    }

    @RequestMapping(value = "/reservationsforclient/{clientId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getReservationsByClientId(@PathVariable("clientId") Integer clientId, HttpServletResponse response) throws IOException {
        List<Request> reservations =   partnerService.getReservationsByPartnerId(clientId);
        response.getWriter().write(JsonConverter.toJson(new JsonMapBuilder().add("reservations",reservations).build()));
    }



    private String getBasePathOfResources() {
        return new File(".").getAbsoluteFile().getParentFile().getParentFile().getPath();
    }


}
