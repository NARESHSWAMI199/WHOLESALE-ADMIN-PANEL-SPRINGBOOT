package com.sales.wholesaler.controller;

import com.sales.entities.User;
import com.sales.global.GlobalConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("removebg")
@RestController
public class RemoveBg {


    @Value(value = "${removebg.absolute}")
    String outputPath;

    @Value(value = "${removebg.get}")
    String relativePath;

    @PostMapping("/")
    public ResponseEntity<Map<String,String>> uploadImage(HttpServletRequest request, @RequestParam("image") MultipartFile file) throws IOException {
        User user = (User) request.getAttribute("user");
        String baseUrl = GlobalConstant.removeBgUrl; // Replace with your Flask API URL
        File filePath = new File(outputPath+user.getSlug()+"/"+file.getOriginalFilename());
        if (!filePath.exists()){
            filePath.mkdirs();
        }
        file.transferTo(filePath);

        // Create a request body with the base64 image
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("input_path", filePath.getAbsolutePath());
        body.add("output_path", outputPath+user.getSlug()+"/");
        body.add("output_filename","result_"+ file.getOriginalFilename());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Send the request to the Flask API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl, requestEntity, String.class);

        // Extract the output path from the response
        String outputPathRes = responseEntity.getBody();
        Map<String,String> result = new HashMap<>();
        result.put("downloadPath", outputPathRes);
        filePath.delete();
        return new ResponseEntity<>(result, responseEntity.getStatusCode());
    }


    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile( HttpServletRequest request, @PathVariable(required = true) String filename) throws Exception {
        User user = (User) request.getAttribute("user");
        Path path = Paths.get(relativePath +user.getSlug()+ "/"+filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }

}
