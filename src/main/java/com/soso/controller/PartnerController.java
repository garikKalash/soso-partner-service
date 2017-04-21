package com.soso.controller;

import com.soso.models.Partner;
import com.soso.service.JsonConverter;
import com.soso.service.JsonMapBuilder;
import com.soso.service.PartnerService;
import com.soso.service.authentication.AuthenticationTokenService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

    @RequestMapping(value = "/partnerRoom", params = {"partnerId"}, method = RequestMethod.GET)
    public void getPartnerById(@RequestParam(value = "partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        Partner partner = partnerService.getPartnerById(partnerId);
        partner.setFeedbacks(partnerService.getFeedbacks(partnerId));
        if (partner.getImgpath() == null || partner.getImgpath().isEmpty()) {
            partner.setImgpath(getImageBase64ByImgPath(getBasePathOfResources() + "\\work\\soso-partner-uploads\\default-account-image.jpg"));
        } else {
            partner.setImgpath(getImageBase64ByImgPath(partner.getImgpath()));
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

    @RequestMapping(value = "/accountImage", params = {"partnerId"}, method = RequestMethod.GET)
    public void getPartnerAccountPage(@RequestParam(value = "partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
        Partner partner = partnerService.getPartnerById(partnerId);
        String imgPath;
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.getWriter().flush();
        if (partner.getImgpath() == null || partner.getImgpath().isEmpty()) {
            imgPath = getBasePathOfResources() + "\\work\\soso-partner-uploads\\default-account-image.jpg";
        } else {
            imgPath = partner.getImgpath();
        }
        String partnerAccountImageString = JsonConverter.toJson(new JsonMapBuilder().add("accountImage", getImageBase64ByImgPath(imgPath)).build());
        response.getWriter().write(partnerAccountImageString);
    }

    @RequestMapping(value = "/addImageToPartnier", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public void addImageToPartnier(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer partnerId,RedirectAttributes redirectAttributes) {

        try {
            String photoPath = getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS + file.getOriginalFilename();
            file.transferTo(new File(photoPath));
            partnerService.savePhotoToPartnier(partnerId, photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(value = "/partnerPhotos/{partnerId}", method = RequestMethod.GET)
    public void getPartnerPhotos(@PathVariable(value = "partnerId") Integer partnerId, HttpServletResponse response) throws IOException {
        List<String> photoPaths = partnerService.getPhotosByParentId(partnerId);
        response.getWriter().flush();

        List<String> photosInBase64 = new ArrayList<>();
        for (String path : photoPaths) {
            String base64Image = getImageBase64ByImgPath(path);
            photosInBase64.add(base64Image);
        }
        String partnerAccountImageString = JsonConverter.toJson(new JsonMapBuilder()
                .add("photos", new JsonMapBuilder()
                        .add("image", photosInBase64)
                        .build())
                .build());
        response.getWriter().write(partnerAccountImageString);
    }


    private String getImageBase64ByImgPath(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        return new String(Base64.encodeBase64(os.toByteArray()), "UTF-8");
    }


    @RequestMapping(value = "/uploadAccountImage", method = RequestMethod.POST, consumes = {"multipart/mixed", "multipart/form-data"})
    public String uploadAccountImage(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer partnerId,
                                     RedirectAttributes redirectAttributes) throws IOException {

        Partner partner = partnerService.getPartnerById(partnerId);
        String newLogoPath = getBasePathOfResources() + RELATIVE_PATH_FOR_UPLOADS + file.getOriginalFilename();
        if (partner.getImgpath() != null && !partner.getImgpath().isEmpty()) {
            partnerService.deletePartnerOldLogoFromFiles(partner.getImgpath());
        }

        partnerService.updatePartnerLogo(newLogoPath, partnerId);
        file.transferTo(new File(newLogoPath));
        redirectAttributes.addFlashAttribute("Your account image is changed successfully!");

        return "redirect:/";
    }

    private String getBasePathOfResources() {
        return new File(".").getAbsoluteFile().getParentFile().getParentFile().getPath();
    }

}
