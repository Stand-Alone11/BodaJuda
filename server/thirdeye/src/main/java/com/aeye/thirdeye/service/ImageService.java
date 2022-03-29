package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.ImageDto;
import com.aeye.thirdeye.entity.Image;
import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.repository.ImageRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;


    Gson gson = new Gson();
    @Value("${spring.servlet.multipart.location}")
    private String absolutePath;

    @Transactional
    public ImageDto insertImage(MultipartFile file, Image image, User user) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
//        System.out.println(image.getL_X());
//        System.out.println(image.getL_Y());
//        System.out.println(image.getR_X());
//        System.out.println(image.getR_Y());

        int xs = (int) (height * image.getL_X());
        int xe = (int) (height * image.getR_X());
        int ys = (int) (width * image.getL_Y());
        int ye = (int) (width * image.getR_Y());

        int xx = ys;
        int yy = height - xe;
        int h = xe - xs;
        int w = ye - ys;

        BufferedImage cropedImage = bufferedImage.getSubimage(xx, yy, w, h);
        int nw = cropedImage.getWidth();
        int nh = cropedImage.getHeight();
        int nt = cropedImage.getType();

        BufferedImage newImageFromBuffer = new BufferedImage(nh, nw, nt);
        Graphics2D graphics2D = newImageFromBuffer.createGraphics();
        graphics2D.rotate(Math.toRadians(90), nw/2, nh/2);
        if(nw > nh)
            graphics2D.drawImage(cropedImage, null,-(nh-nw)/2 , (nw-nh)/2);
        else
            graphics2D.drawImage(cropedImage, null,(nh-nw)/2 , -(nw-nh)/2);

//        System.out.println(width + ", " + height);
        Image savedImage = imageRepository.save(image);
        String fileName = Long.toString(savedImage.getId());
        File folder = new File(absolutePath + "tmpImgs");

//        ImageIO.write(cropedImage, "jpg", new File(folder + File.separator + fileName + "_cropped.jpg"));
        ImageIO.write(newImageFromBuffer, "jpg", new File(folder + File.separator + fileName + "_cropped.jpg"));

        if (!folder.exists()) {
            folder.mkdirs();
//            System.out.println("폴더 생성");
        }

        File newFile = new File(folder + File.separator + fileName + ".jpg");
        file.transferTo(newFile);

        savedImage.setImage(newFile.getAbsolutePath());
        savedImage.setUser(user);
        imageRepository.save(savedImage);

        savedImage.setImage(folder + File.separator + fileName + "_cropped.jpg");
        return new ImageDto(savedImage);
    }

    @Transactional
    public void approveImage(int seq) throws Exception {
        Image nowImage = imageRepository.findById(Long.valueOf(seq)).orElse(null);

        String croppedImagePath = nowImage.getImage().substring(0,nowImage.getImage().length()-4) + "cropped.jpg";
        File curFile = new File(croppedImagePath);
        File curFile2 = new File(nowImage.getImage());
        String pathPrefix = absolutePath + "gpuImgs/";
        String pathPrefix2 = absolutePath + "savedImgs/";
        String pathSurfix = nowImage.getTypeA() + "/"
                + nowImage.getTypeB() + "/"
                + nowImage.getTypeC() + "/"
                + nowImage.getProvider() + "_"
                + nowImage.getTitle() + "_"
                + nowImage.getFaceYN() + "/";

        LocalDate now = LocalDate.now();

        String fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + System.nanoTime();


        nowImage.setImage(pathPrefix2 + pathSurfix + fileName + ".jpg");

        File folder = new File(pathPrefix + pathSurfix);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File imgFile = new File(folder + File.separator + fileName + ".jpg");
        curFile.renameTo(imgFile);

        File folder2 = new File(pathPrefix2 + pathSurfix);
        if (!folder2.exists()) {
            folder2.mkdirs();
        }
        File imgFile2 = new File(folder2 + File.separator + fileName + ".jpg");
        curFile2.renameTo(imgFile2);
        File jsonFile2 = new File(folder2 + File.separator + fileName + ".json");
        FileWriter fileWriter = new FileWriter(jsonFile2);
        gson.toJson(nowImage, fileWriter);
        fileWriter.close();


        imageRepository.save(nowImage);
    }

    public void rejectImage(int seq) throws Exception {
        Image nowImage = imageRepository.findById(Long.valueOf(seq)).orElse(null);

        String croppedImagePath = nowImage.getImage().substring(0,nowImage.getImage().length()-4) + "cropped.jpg";
        File curFile = new File(croppedImagePath);
        File curFile2 = new File(nowImage.getImage());

        if (curFile.exists()) {
            curFile.delete();
        }
        if(curFile2.exists()){
            curFile2.delete();
        }
    }
}
