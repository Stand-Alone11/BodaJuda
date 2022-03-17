package com.aeye.thirdeye.api;

import com.aeye.thirdeye.entity.Image;
import com.aeye.thirdeye.service.ImageService;
import com.google.gson.Gson;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/mo")
public class MobileController {

    @Autowired
    private ImageService imageService;

    private Gson gson = new Gson();

    @Value("${spring.http.multipart.location}")
    private String absolutePath;

    @PostMapping
    public ResponseEntity<?> insertImage(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart(value="label") String image){

        try {
            Image req = gson.fromJson(image, Image.class);
            imageService.insertImage(file, req);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<Integer>(0, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<Integer>(1, HttpStatus.OK);
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> showImage(@RequestParam("name") long name){
        byte[] imageByteArray = null;

        try {
            imageByteArray = imageService.showImage(name);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<String>("fail",HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<byte[]>(imageByteArray,HttpStatus.OK);
    }
}
