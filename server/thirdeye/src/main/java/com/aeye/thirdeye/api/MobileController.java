package com.aeye.thirdeye.api;

import com.aeye.thirdeye.dto.ImageDto;
import com.aeye.thirdeye.entity.Image;
import com.aeye.thirdeye.service.ImageService;
import com.aeye.thirdeye.service.SlackImageService;
import com.google.gson.Gson;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.ActionResponseSender;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.response.ActionResponse;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mo")
public class MobileController {

    private final ImageService imageService;

    private final SlackImageService slackImageService;

    private Gson gson = new Gson();

    @Value("${spring.http.multipart.location}")
    private String absolutePath;

    @Value("${notification.slack.webhook.url}")
    private String url;

    @PostMapping
    public ResponseEntity<?> insertImage(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart(value="label") String image){

        try {
            Image req = gson.fromJson(image, Image.class);
            ImageDto imageInfo = imageService.insertImage(file, req);
            slackImageService.fileUpload(imageInfo);
            slackImageService.makeRequestLayout(imageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<Integer>(0, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<Integer>(1, HttpStatus.OK);
    }

    // Slack 버튼 결과 여기로 받아서 처리
    @RequestMapping(value = "/inspect/response", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> inspectResponse(@RequestParam String payload){

        BlockActionPayload blockActionPayload = slackImageService.makeResponseLayout(payload);
        ActionResponse response = null;
        String nowActionId = blockActionPayload.getActions().get(0).getActionId();

        if(nowActionId.equals("typeAaction")
                || nowActionId.equals("typeBaction")
                || nowActionId.equals("typeCaction")){

            response =
                    ActionResponse.builder()
                            .replaceOriginal(true)
                            .blocks(blockActionPayload.getMessage().getBlocks())
                            .build();
        }
        else {
            response =
                    ActionResponse.builder()
                            .replaceOriginal(true)
                            .blocks(blockActionPayload.getMessage().getBlocks())
                            .build();
            // 승인
            if(nowActionId.equals("action_approve")){


            }
            // 거부
            else{


            }
        }

        Slack slack = Slack.getInstance();
        ActionResponseSender sender = new ActionResponseSender(slack);
        try {
            sender.send(blockActionPayload.getResponseUrl(), response);
        }catch (IOException e){
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
