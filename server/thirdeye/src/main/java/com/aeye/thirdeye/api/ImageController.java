package com.aeye.thirdeye.api;

import com.aeye.thirdeye.service.SlackImageService;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.ActionResponseSender;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.response.ActionResponse;
import com.slack.api.model.block.LayoutBlock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final SlackImageService slackImageService;

    @Value("${notification.slack.webhook.url}")
    private String url;

    @PostMapping("/image/test")
    public ResponseEntity<?> test(@RequestPart(value = "image", required = false) MultipartFile file,
                                  @RequestBody Map<String, String> input){

        return null;
    }

    // 슬랙 검수 코드
    @PostMapping("/slack/request")
    public ResponseEntity<?> reallytest(){
        List<LayoutBlock> layoutBlocks = slackImageService.makeLayout();

        // 위에 만든 payload Slack에 전송
        try {
            Slack.getInstance().send(url,
                    payload(p -> p
                            .text("슬랙에 메시지를 출력하지 못했습니다.")
                            .blocks(layoutBlocks)
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Slack 버튼 결과 여기로 받음 일단
    @RequestMapping(value = "/slack/response", method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> test12(@RequestParam String payload){

        BlockActionPayload blockActionPayload = slackImageService.responseSlackLayout(payload);
        ActionResponse response =
                ActionResponse.builder()
                        .replaceOriginal(true) // 변경은되는데 여기서 변경이 안돼서
                        .deleteOriginal(true) // 삭제로 구현해놓음
                        .blocks(blockActionPayload.getMessage().getBlocks())
                        .build();

        Slack slack = Slack.getInstance();
        ActionResponseSender sender = new ActionResponseSender(slack);

        try {
            sender.send(blockActionPayload.getResponseUrl(), response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK).body("크허허허허");
    }

}
