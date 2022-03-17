package com.aeye.thirdeye.service;

import com.aeye.thirdeye.entity.Image;
import com.aeye.thirdeye.repository.ImageRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class ImageService{

    private final ImageRepository imageRepository;

    Gson gson = new Gson();
    @Value("${spring.http.multipart.location}")
    private String absolutePath;

    public byte[] showImage(long id) throws Exception{
        Image image = imageRepository.findById(id).get();
        String path = image.getImage();
        InputStream is = new FileInputStream(path);
        byte[] imageByteArray = IOUtils.toByteArray(is);
        is.close();
        return imageByteArray;
    }

    @Transactional
    public void insertImage(MultipartFile file, Image image) throws Exception{

        Image savedImage = imageRepository.save(image);
        String fileName = Long.toString(savedImage.getId());
        File newFile = new File(absolutePath);

        if(!newFile.exists()){
            newFile.mkdirs();
        }

        newFile = new File(absolutePath + fileName + ".jpg");
        file.transferTo(newFile);
        savedImage.setImage(newFile.getAbsolutePath());
        imageRepository.save(savedImage);


        File newFile2 = new File(absolutePath + fileName + ".json");
        System.out.println(newFile2.getAbsolutePath());
        newFile2.createNewFile();

        FileWriter fileWriter = new FileWriter(newFile2);
        gson.toJson(savedImage,fileWriter);
        fileWriter.close();

        savedImage.setImage(absolutePath+fileName + ".jpg");
    }
}
