package com.sales.wholesaler.controller;

import com.sales.entities.User;
import com.sales.global.GlobalConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequestMapping("removebg")
@RestController
public class RemoveBg {

    private static final Logger logger = LoggerFactory.getLogger(RemoveBg.class);

    @Value(value = "${removebg.absolute}")
    String outputPath;

    @Value(value = "${removebg.get}")
    String relativePath;

    @PostMapping("/")
    public ResponseEntity<Map<String,String>> uploadImage(HttpServletRequest request, @RequestParam("image") MultipartFile file) throws IOException {
        logger.info("Starting uploadImage method");
        User user = (User) request.getAttribute("user");
        String baseUrl = GlobalConstant.removeBgUrl; // Replace with your Flask API URL
        Path baseDir = Paths.get(outputPath).toAbsolutePath().normalize();
        Path userFolder = baseDir.resolve(user.getSlug()).normalize();
        Path targetPath = userFolder.resolve(Objects.requireNonNull(FilenameUtils.getName(file.getOriginalFilename()))).normalize();
        if (!targetPath.startsWith(baseDir)) {
            throw new SecurityException("Invalid file path attempt detected!");
        }
        File filePath = targetPath.toFile();
        if (!filePath.exists()){
            boolean dirCreated = filePath.getParentFile().mkdirs();
            if(dirCreated) logger.info("New dir created :{}",filePath.getName());

        }

        file.transferTo(filePath);

       /* if (UploadImageValidator.isValidImage(file, 200,
                200, GlobalConstant.maxWidth, GlobalConstant.maxHeight,
                GlobalConstant.allowedAspectRatios, GlobalConstant.allowedFormats)) {
            file.transferTo(filePath);
        }
        else {
            throw new MyException("Image is not fit in accept ratio. please resize you image before upload.");
        }
    */


        // Create a request body with the base64 image
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("input_path", filePath.getAbsolutePath());
        body.add("output_path", outputPath+user.getSlug()+GlobalConstant.PATH_SEPARATOR);
        body.add("output_filename","result_"+ file.getName());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Send the request to the Flask API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl, requestEntity, String.class);

        // Extract the output path from the response
        String outputPathRes = responseEntity.getBody();
        Map<String,String> result = new HashMap<>();
        result.put("downloadPath","/removebg/"+outputPathRes);
        Files.delete(targetPath);
        logger.info("File : {} successfully deleted",filePath.getAbsolutePath());
        logger.info("Completed uploadImage method");
        return new ResponseEntity<>(result, responseEntity.getStatusCode());
    }


    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile( HttpServletRequest request, @PathVariable(required = true) String filename) throws MalformedURLException {
        logger.info("Starting getFile method");
        User user = (User) request.getAttribute("user");
        Path relative = Paths.get(relativePath);
        Path userSlug = relative.resolve(user.getSlug()).normalize();
        Path path = userSlug.resolve(filename).normalize();
        Resource resource = new UrlResource(path.toUri());
        logger.info("Completed getFile method");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }

}
