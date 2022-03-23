package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.ImageDto;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class ImageService{

    private final ImageRepository imageRepository;

    
    Gson gson = new Gson();
    @Value("${spring.http.multipart.location}")
    private String absolutePath;

    @Transactional
    public ImageDto insertImage(MultipartFile file, Image image) throws Exception{

        Image savedImage = imageRepository.save(image);
        String fileName = Long.toString(savedImage.getId());
        File folder = new File(absolutePath + "tmpImgs");

        if(!folder.exists()){
            folder.mkdirs();
            System.out.println("폴더 생성");
//            folder.setReadable(true);
//            folder.setWritable(true);
        }
        
        File newFile = new File(folder + File.separator + fileName + ".jpg");
        file.transferTo(newFile);

        File jsonFile = new File(folder + File.separator + fileName + ".json");
        jsonFile.createNewFile();
        FileWriter fileWriter = new FileWriter(jsonFile);
        gson.toJson(savedImage,fileWriter);
        fileWriter.close();

        savedImage.setImage(newFile.getAbsolutePath());
        imageRepository.save(savedImage);

        savedImage.setImage(newFile.getAbsolutePath());

        return new ImageDto(savedImage);
    }

    @Transactional
    public void approveImage(int seq) throws Exception{
        Image nowImage = imageRepository.findById(Long.valueOf(seq)).orElse(null);

        File curFile = new File(nowImage.getImage());
        String pathPrefix = absolutePath + "gpuImgs/";
        String pathPrefix2 = absolutePath + "savedImgs/";
        String pathSurfix = nowImage.getTypeA() + "/"
                + nowImage.getTypeB() + "/"
                + nowImage.getTypeC() + "/"
                + nowImage.getProvider() + "_"
                + nowImage.getTitle() + "_"
                + nowImage.getFaceYN() + "/";

        LocalDate now = LocalDate.now();

        String fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + System.nanoTime();


        nowImage.setImage(pathPrefix2 + pathSurfix + fileName + ".jpg");

        File folder = new File(pathPrefix + pathSurfix);
        if(!folder.exists()){
            folder.mkdirs();
        }

        File imgFile = new File(folder + File.separator + fileName + ".jpg");
        curFile.renameTo(imgFile);
        File jsonFile = new File(folder + File.separator + fileName + ".json");
        jsonFile.createNewFile();
        FileWriter fileWriter = new FileWriter(jsonFile);
        gson.toJson(nowImage,fileWriter);
        fileWriter.close();

        File folder2 = new File(pathPrefix2 + pathSurfix);
        if(!folder2.exists()){
            folder2.mkdirs();
        }
        File imgFile2 = new File(folder2 + File.separator + fileName + ".jpg");

        Files.copy(imgFile.toPath(), imgFile2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        curFile.renameTo(imgFile);
        File jsonFile2 = new File(folder2 + File.separator + fileName + ".json");
        Files.copy(jsonFile.toPath(), jsonFile2.toPath(), StandardCopyOption.REPLACE_EXISTING);


        imageRepository.save(nowImage);
    }

    @Transactional
    public void rejectImage(int seq) throws Exception{
        Image nowImage = imageRepository.findById(Long.valueOf(seq)).orElse(null);

        File curFile = new File(nowImage.getImage());

        if(curFile.exists()){
            curFile.delete();
            System.out.println("파일 삭제");
        }
    }
}
