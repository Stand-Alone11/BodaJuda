package com.aeye.thirdeye.api;

import com.aeye.thirdeye.dto.ImageDto;
import com.aeye.thirdeye.dto.response.SimpleProjectDto;
import com.aeye.thirdeye.entity.Image;
import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.service.ImageService;
import com.aeye.thirdeye.service.SlackImageService;
import com.aeye.thirdeye.token.JwtTokenProvider;
import com.google.gson.Gson;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.ActionResponseSender;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.response.ActionResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mo")
@Api(tags = "사진 전송 API")
public class ImageAPIController {

    private final ImageService imageService;

    private final SlackImageService slackImageService;

    private final JwtTokenProvider jwtTokenProvider;

    private Gson gson = new Gson();

    @Value("${spring.servlet.multipart.location}")
    private String absolutePath;

    @Value("${notification.slack.webhook.url}")
    private String url;

    /**
     * 사진 및 라벨링 데이터 전송
     * @param file
     * @return
     * 반환 코드 : 200, 406
     */
    @PostMapping
    @ApiOperation(value = "사진 및 라벨링 데이터 전송", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "token 인증 실패"),
            @ApiResponse(code = 406, message = "입력값 에러")
    })
    public ResponseEntity<?> insertImage(@ApiParam(value = "사진 파일") @RequestPart(value = "file", required = false) MultipartFile file,
                                         @ApiParam(value = "L_X, L_Y, R_X, R_Y") @RequestPart(value="label") String image,
                                         Long projectId,
                                         HttpServletRequest request){

        String token = request.getHeader("Authorization");

        if(token != null && !jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalidate Token");
        }

        try {
            Image req = gson.fromJson(image, Image.class);
            User user = token != null ? jwtTokenProvider.getUser(token) : null;
            ImageDto imageInfo = imageService.insertImage(file, req, user, projectId);
            slackImageService.fileUpload(imageInfo);
            slackImageService.makeRequestLayout(imageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        return ResponseEntity.ok(null);
    }

    /**
     * Slack 버튼 결과 여기로 받아서 처리
     * @param payload
     * @return
     * 반환 코드 : 200
     */
    @RequestMapping(value = "/inspect/response", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "슬랙 응답 api", notes = "")
    public ResponseEntity<?> inspectResponse(@RequestParam String payload){

        BlockActionPayload blockActionPayload = slackImageService.makeResponseLayout(payload);
        ActionResponse response = null;
        String nowActionId = blockActionPayload.getActions().get(0).getActionId();
        String checked = "";
        if(nowActionId.equals("typeAaction")
                || nowActionId.equals("typeBaction")
                || nowActionId.equals("typeCaction")
                || nowActionId.equals("typeDaction")){

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
            int seq = Integer.parseInt(blockActionPayload.getActions().get(0).getValue());
            // 승인
            if(nowActionId.equals("action_approve")){
                try {
                    imageService.approveImage(seq);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            // 거부
            else{
                try {
                    imageService.rejectImage(seq);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
