package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.ImageDto;
import com.aeye.thirdeye.entity.Image;
import com.aeye.thirdeye.entity.Project;
import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.repository.ImageRepository;
import com.aeye.thirdeye.repository.ProjectRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private final ProjectRepository projectRepository;


    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = new Gson();
    @Value("${spring.servlet.multipart.location}")
    private String absolutePath;

    @Transactional
    public ImageDto insertImage(MultipartFile file, Image image, User user, Long projectId) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
//        System.out.println(width + ", " + height);
//        int xs = (int) (height * image.getL_X());
//        int xe = (int) (height * image.getR_X());
//        int ys = (int) (width * image.getL_Y());
//        int ye = (int) (width * image.getR_Y());
        int xs = image.getL_X(); // 1500
        int xe = image.getR_X(); // 2000
        int ys = image.getL_Y(); // 2000
        int ye = image.getR_Y(); // 2700
//        System.out.println(xs + ", " + xe);
//        System.out.println(ys + ", " + ye);
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
        graphics2D.drawImage(cropedImage, null,-(nh-nw)/2 , (nw-nh)/2);

        Image savedImage = imageRepository.save(image);
        String fileName = Long.toString(savedImage.getId());
        File folder = new File(absolutePath + "tmpImgs");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        ImageIO.write(newImageFromBuffer, "jpg", new File(folder + File.separator + fileName + "_cropped.jpg"));

        File newFile = new File(folder + File.separator + fileName + ".jpg");
        file.transferTo(newFile);

        Project nowProject = projectRepository.findById(projectId).orElse(null);

        savedImage.setImage(newFile.getAbsolutePath());
        savedImage.setUser(user);
        savedImage.setProject(nowProject);
        savedImage.setTypeA(nowProject.getTypeA());
        savedImage.setTypeB(nowProject.getTypeB());
        savedImage.setTypeC(nowProject.getTypeC());
        savedImage.setTitle(nowProject.getTitle());
        savedImage.setProvider(nowProject.getProvider());
        imageRepository.save(savedImage);

        savedImage.setImage(folder + File.separator + fileName + "_cropped.jpg");
        return new ImageDto(savedImage);
    }

    @Transactional
    public void approveImage(int seq) throws Exception {
        Image nowImage = imageRepository.findById(Long.valueOf(seq)).orElse(null);

        File curFile = new File(nowImage.getImage());
        File curFile2 = new File(nowImage.getImage().substring(0,nowImage.getImage().length()-12)+".jpg");
        String pathPrefix = absolutePath + "gpuImgs/";
        String pathPrefix2 = absolutePath + "savedImgs/";
        String nowFaceYN = nowImage.getFaceYN();
        if(nowFaceYN == null || nowFaceYN.trim().length() == 0){
            nowFaceYN = "전면";
        }
        String pathSurfix = nowImage.getTypeA() + "/"
                + nowImage.getTypeB() + "/"
                + nowImage.getTypeC() + "/";
        String folderName = nowImage.getProvider() + "_"
                + nowImage.getTitle() + "_"
                + nowFaceYN + "/";

        LocalDate now = LocalDate.now();

        String fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + System.nanoTime();


        nowImage.setImage(pathPrefix2 + pathSurfix + fileName + ".jpg");

        File folder = new File(pathPrefix + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File imgFile = new File(folder + File.separator + fileName + ".jpg");
        System.out.println(imgFile.getAbsolutePath());
        curFile.renameTo(imgFile);

        File folder2 = new File(pathPrefix2 + pathSurfix + folderName);
        if (!folder2.exists()) {
            folder2.mkdirs();
        }
        File imgFile2 = new File(folder2 + File.separator + fileName + ".jpg");
        curFile2.renameTo(imgFile2);
        File jsonFile2 = new File(folder2 + File.separator + fileName + ".json");
        jsonFile2.createNewFile();

        ImageDto imageDto = new ImageDto(nowImage);
        FileWriter fileWriter = new FileWriter(jsonFile2);
        gson.toJson(imageDto, fileWriter);
        fileWriter.close();


        imageRepository.save(nowImage);
    }

    public void rejectImage(int seq) throws Exception {
        Image nowImage = imageRepository.findById(Long.valueOf(seq)).orElse(null);

        File curFile = new File(nowImage.getImage());
        File curFile2 = new File(nowImage.getImage().substring(0,nowImage.getImage().length()-12)+".jpg");

        if (curFile.exists()) {
            curFile.delete();
        }
        if(curFile2.exists()){
            curFile2.delete();
        }
    }
}
